package com.arkflame.flamepearls.listeners;

import com.arkflame.flamepearls.FlamePearls;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

import com.arkflame.flamepearls.config.GeneralConfigHolder;
import com.arkflame.flamepearls.managers.CooldownManager;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    private final CooldownManager cooldownManager;
    private final GeneralConfigHolder generalConfigHolder;

    public PlayerInteractListener(CooldownManager cooldownManager, GeneralConfigHolder generalConfigHolder) {
        this.cooldownManager = cooldownManager;
        this.generalConfigHolder = generalConfigHolder;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (generalConfigHolder.getDisabledWorlds().contains(world.getName())) return;

        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();

        ItemStack heldItem = null;
        if (main.getType() == Material.ENDER_PEARL) heldItem = main;
        else if (off.getType() == Material.ENDER_PEARL) heldItem = off;

        if (heldItem == null) return;

        double cooldown = cooldownManager.getCooldown(player);
        if (cooldown > 0.1) {
            event.setCancelled(true);
            // this was debug, we don't want the message
//            player.sendMessage("You are on cooldown for " + cooldown + " seconds.");
            return;
        }

        cooldownManager.updateLastPearl(player);

        Bukkit.getScheduler().runTaskLater(FlamePearls.getInstance(), () -> {
            double pearlCooldownSeconds = generalConfigHolder.getPearlCooldown(player);
            int ticks = (int) (pearlCooldownSeconds * 20);
            player.setCooldown(Material.ENDER_PEARL, ticks);
        }, 1L);
    }
}
