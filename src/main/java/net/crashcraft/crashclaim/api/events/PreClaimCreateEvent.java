package net.crashcraft.crashclaim.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player attempts to create a new claim,
 * before the claim object is created or payment is taken.
 * This event provides the player and the proposed claim boundaries.
 * If this event is cancelled, the claim will not be created.
 */
public class PreClaimCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final Location minCorner;
    private final Location maxCorner;

    public PreClaimCreateEvent(Player player, Location minCorner, Location maxCorner) {
        this.player = player;
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.cancelled = false;
    }

    /**
     * @return The player who is attempting to create the claim.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The minimum corner (lowest X, Y, Z) of the proposed claim.
     */
    public Location getMinCorner() {
        return minCorner;
    }

    /**
     * @return The maximum corner (highest X, Y, Z) of the proposed claim.
     */
    public Location getMaxCorner() {
        return maxCorner;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}