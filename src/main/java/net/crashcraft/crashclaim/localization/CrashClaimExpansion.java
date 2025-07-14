package net.crashcraft.crashclaim.localization;

import co.aikar.idb.DB;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.config.GlobalConfig;
import net.crashcraft.crashclaim.payment.PaymentProcessor;
import net.crashcraft.crashclaim.visualize.api.BaseVisual;
import net.crashcraft.crashclaim.visualize.api.VisualGroup;
import net.crashcraft.crashclaim.visualize.api.claim.BlockClaimVisual;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrashClaimExpansion extends PlaceholderExpansion {
    private final CrashClaim crashClaim;
    private final PaymentProcessor provider;

    public CrashClaimExpansion(CrashClaim crashClaim, PaymentProcessor provider) {
        this.crashClaim = crashClaim;
        this.provider = provider;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player != null){
            switch (params.toLowerCase()) {
                case "balance" -> {
                    double bal = provider.getProvider().getCachedBalance(player.getUniqueId());
                    return Integer.toString((int) bal);
                }
                case "max_balance" -> {
                    return Integer.toString(GlobalConfig.maxClaimBlocks);
                }
                case "total_owned_claims" -> {
                    return Integer.toString(crashClaim.getDataManager().getNumberOwnedClaims(player.getUniqueId()));
                }
                case "total_owned_parent_claims" -> {
                    return Integer.toString(crashClaim.getDataManager().getNumberOwnedParentClaims(player.getUniqueId()));
                }
                case "current_claim_owner" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    Claim claim = crashClaim.getDataManager().getClaim(onlinePlayer.getLocation());

                    if (claim == null){
                        return "";
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(claim.getOwner());
                    return offlinePlayer.getName();
                }
                case "visual_status" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    VisualGroup group = CrashClaim.getPlugin().getVisualizationManager().fetchVisualGroup(onlinePlayer, false);

                    if (group == null){
                        return Localization.PLACEHOLDERAPI__VISUAL_STATUS_HIDDEN.getRawMessage();
                    }

                    for (BaseVisual visual : group.getActiveVisuals()){
                        if (visual.getClaim() != null){
                            return Localization.PLACEHOLDERAPI__VISUAL_STATUS_SHOWN.getRawMessage();
                        }
                    }

                    return Localization.PLACEHOLDERAPI__VISUAL_STATUS_HIDDEN.getRawMessage();
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return crashClaim.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return crashClaim.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return crashClaim.getDescription().getVersion();
    }

    @Override
    public boolean persist(){
        return true;
    }
}
