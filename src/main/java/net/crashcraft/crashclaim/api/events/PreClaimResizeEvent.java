package net.crashcraft.crashclaim.api.events;

import net.crashcraft.crashclaim.claimobjects.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player attempts to resize a claim,
 * before the resize operation is processed.
 * If this event is cancelled, the claim will not be resized.
 */
public class PreClaimResizeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final Claim claim;
    private final Location draggedCorner;
    private final Location newTargetLocation;

    public PreClaimResizeEvent(Player player, Claim claim, Location draggedCorner, Location newTargetLocation) {
        this.player = player;
        this.claim = claim;
        this.draggedCorner = draggedCorner;
        this.newTargetLocation = newTargetLocation;
        this.cancelled = false;
    }

    /**
     * @return The player who is attempting to resize the claim.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The claim that is being modified.
     */
    public Claim getClaim() {
        return claim;
    }

    /**
     * @return The original corner (the first click) that the player is dragging.
     */
    public Location getDraggedCorner() {
        return draggedCorner;
    }

    /**
     * @return The new location (the second click) where the player
     * is dragging the corner to.
     */
    public Location getNewTargetLocation() {
        return newTargetLocation;
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