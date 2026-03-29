package com.arkflame.flamepearls.managers;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

// Stores and manages the origin of projectiles
public class OriginManager {
    // Origin of launch of projectiles
    private final Map<Projectile, Location> projectileOrigins = new ConcurrentHashMap<>();
    // Players that will teleport

    // Counter for times a projectile had been added
    @Getter
    private int projectileCount = 0;

    public void setOrigin(Projectile projectile, Location location) {
        // Insert the projectile-origin
        projectileOrigins.put(projectile, location);
        // Add new projectile to count
        projectileCount++;
    }

    public Location getOriginAndRemove(Projectile projectile) {
        // Return the value removed
        return projectileOrigins.remove(projectile);
    }

    public Collection<Projectile> getProjectiles() {
        return projectileOrigins.keySet();
    }
}
