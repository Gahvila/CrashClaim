package net.crashcraft.crashclaim.payment.providers;

import co.aikar.idb.DB;
import net.crashcraft.crashclaim.payment.PaymentProvider;
import net.crashcraft.crashclaim.payment.ProviderInitializationException;
import net.crashcraft.crashclaim.payment.TransactionRecipe;
import net.crashcraft.crashclaim.payment.TransactionType;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ClaimBlockProvider implements PaymentProvider {
    private final Map<UUID, Double> balanceCache = new ConcurrentHashMap<>();

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

                        // update cache
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

                    // update cache
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

                    // cache the balance
                    balanceCache.put(user, result);

                    callback.accept(result);
                })
                .exceptionally((e) -> {
                    e.printStackTrace();
                    callback.accept(0D);
                    return null;
                });
    }

    @Override
    public double getCachedBalance(UUID user) {
        return balanceCache.getOrDefault(user, 0D);
    }
}
