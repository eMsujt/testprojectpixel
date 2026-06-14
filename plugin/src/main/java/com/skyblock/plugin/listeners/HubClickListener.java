package com.skyblock.plugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class HubClickListener implements Listener {

    private static final String TITLE = "Hub";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!TITLE.equals(event.getView().getTitle())) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        org.bukkit.inventory.meta.ItemMeta meta = event.getCurrentItem().getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String name = meta.getDisplayName();
        switch (name) {
            case "Garden"               -> player.performCommand("skyblock garden");
            case "Slayer"               -> player.performCommand("skyblock slayer");
            case "Fishing"              -> player.performCommand("skyblock fishing");
            case "Dungeons"             -> player.performCommand("skyblock dungeons");
            case "Skills"               -> player.performCommand("skyblock skills");
            case "Enchanting"           -> player.performCommand("skyblock enchanting");
            case "Auction House"        -> player.performCommand("skyblock auction");
            case "Bazaar"               -> player.performCommand("skyblock bazaar");
            case "Profile"              -> player.performCommand("skyblock profile");
            case "Pets"                 -> player.performCommand("skyblock pets");
            case "Kuudra"               -> player.performCommand("skyblock kuudra");
            case "Collections"          -> player.performCommand("skyblock collections");
            case "Island"               -> player.performCommand("skyblock island");
            case "Heart of the Mountain"-> player.performCommand("skyblock hotm");
            case "Bank"                 -> player.performCommand("skyblock bank");
            default -> { /* unknown slot — ignore */ }
        }
    }
}
