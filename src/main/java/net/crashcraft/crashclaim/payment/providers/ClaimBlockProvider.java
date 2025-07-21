package net.crashcraft.crashclaim.payment.providers;

import co.aikar.idb.DB;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.crashcraft.crashclaim.config.GlobalConfig;
import net.crashcraft.crashclaim.localization.Localization;
import net.crashcraft.crashclaim.payment.PaymentProvider;
import net.crashcraft.crashclaim.payment.ProviderInitializationException;
import net.crashcraft.crashclaim.payment.TransactionRecipe;
import net.crashcraft.crashclaim.payment.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class ClaimBlockProvider implements PaymentProvider, Listener {
    private final Cache<UUID, Double> balanceCache = Caffeine.newBuilder().build();

    @Override
    public String getProviderIdentifier() {
        return "ClaimBlockProvider";
    }

    @Override
    public boolean checkRequirements() {
        return true;
    }

    @Override
    public void setup() throws ProviderInitializationException {

    }

    @Override
    public void makeTransaction(UUID user, TransactionType type, String comment, double amount, Consumer<TransactionRecipe> callback) {
        int realAmount = (int) Math.ceil(amount);

        switch (type){
            case WITHDRAW -> getBalance(user, (bal) -> {
                if (realAmount > bal){
                    callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Transaction", "Insufficient Funds"));
                } else {
                    try {
                        DB.executeUpdate("REPLACE INTO claimblocks(amount, player_id) VALUES(?, (SELECT id FROM players WHERE uuid = ?))",
                                bal - realAmount,
                                user);
                        balanceCache.put(user, bal - realAmount);
                        callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Withdraw"));
                    } catch (SQLException e){
                        e.printStackTrace();
                        callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Transaction", "Database Error"));
                    }
                }
            });

            case DEPOSIT -> getBalance(user, (bal) -> {
                try {
                    DB.executeUpdate("REPLACE INTO claimblocks(amount, player_id) VALUES(?, (SELECT id FROM players WHERE uuid = ?))",
                            bal + realAmount,
                            user.toString());
                    balanceCache.put(user, bal + realAmount);
                    callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Deposit"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Transaction", "Database Error"));
                }
            });
        }
    }

    @Override
    public void getBalance(UUID user, Consumer<Double> callback) {
        DB.getFirstColumnAsync("SELECT amount FROM claimblocks WHERE player_id = (SELECT id FROM players WHERE uuid = ?)", user.toString())
                .thenAccept((bal) -> {
                    double result = (bal == null) ? 0D : (double)((int) bal);
                    balanceCache.put(user, result);
                    callback.accept(result);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    callback.accept(0D);
                    return null;
                });
    }

    @Override
    public double getCachedBalance(UUID user) {
        Double cached = balanceCache.getIfPresent(user);
        return cached != null ? cached : 0D;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getBalance(player.getUniqueId(), (balance) -> {
            player.sendMessage(Localization.ECONOMY__CHECK_SELF_BALANCE.getMessage(player,
                    "balance", Integer.toString(balance.intValue())));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        balanceCache.invalidate(event.getPlayer().getUniqueId());
    }
}
