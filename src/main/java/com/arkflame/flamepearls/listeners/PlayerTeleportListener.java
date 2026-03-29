package com.arkflame.flamepearls.listeners;

import com.arkflame.flamepearls.config.GeneralConfigHolder;
import com.arkflame.flamepearls.utils.FoliaAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * Instantiates a mirrored approach of PlayerTeleportEvent handling for ender
 * pearls.
 * - On non-Folia servers: handles the actual PlayerTeleportEvent.
 * - On Folia: emulates the same intent using a periodic scheduler
 * by finalizing pending pearl teleports (setAsTeleported) on the player's
 * entity thread.
 */
public class PlayerTeleportListener implements Listener {
    private final GeneralConfigHolder generalConfigHolder;

    public PlayerTeleportListener(GeneralConfigHolder generalConfigHolder) {
        this.generalConfigHolder = generalConfigHolder;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Only process on non-Folia; Folia emulates with ProjectileHitListener
        if (FoliaAPI.isFolia()) {
            return;
        }
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (to == null) {
                return;
            }
            World toWorld = to.getWorld();
            if (toWorld == null) {
                return;
            }
            String toWorldName = toWorld.getName();
            if (generalConfigHolder.isWorldDisabled(toWorldName)) {
                return;
            }
            
            if (generalConfigHolder.isPreventWorldBorderTeleport() && !isInsideWorldBorder(to)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isInsideWorldBorder(Location loc) {
        if (loc.getWorld() == null) return true;

        WorldBorder border = loc.getWorld().getWorldBorder();
        double size = border.getSize() / 2.0;
        double centerX = border.getCenter().getX();
        double centerZ = border.getCenter().getZ();
        double x = loc.getX();
        double z = loc.getZ();
        return (x >= centerX - size && x <= centerX + size) &&
                (z >= centerZ - size && z <= centerZ + size);
    }
}