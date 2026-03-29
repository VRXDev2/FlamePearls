package com.arkflame.flamepearls.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.arkflame.flamepearls.FlamePearls;

// Stores and manages the last times pearls were thrown.
public class CooldownManager {
    // Last time pearl was thrown by a player (Cooldown checks)
    private final Map<UUID, Long> lastPearlThrows = new ConcurrentHashMap<>();

    public void updateLastPearl(Player player) {
        lastPearlThrows.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public double getCooldown(Player player) {
        // Get the time passed since last pearl in milliseconds
        long timeSinceLastPearl = System.currentTimeMillis() - lastPearlThrows.getOrDefault(player.getUniqueId(), 0L);
        double cooldown = FlamePearls.getInstance().getGeneralConfigHolder().getPearlCooldown(player) * 1000D;

        // Return the cooldown minus the time passed and convert to seconds
        return (cooldown - Math.min(cooldown, timeSinceLastPearl)) / 1000D;
    }

    public void resetCooldown(Player player) {
        lastPearlThrows.remove(player.getUniqueId());
    }

    public String getFancyCooldown(Player player) {
        double cooldown = getCooldown(player);
        return String.format("%.1f", cooldown);
    }
}
