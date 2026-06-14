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
            case "Garden"           -> player.performCommand("skyblock garden");
            case "Slayer"           -> player.performCommand("skyblock slayer");
            case "Fishing"          -> player.performCommand("skyblock fishing");
            case "Foraging"         -> player.performCommand("skyblock foraging");
            case "Combat"           -> player.performCommand("skyblock combat");
            case "Dungeons"         -> player.performCommand("skyblock dungeons");
            case "Storage"          -> player.performCommand("skyblock storage");
            case "Crafting"         -> player.performCommand("skyblock crafting");
            case "Enchanting"       -> player.performCommand("skyblock enchanting");
            case "Auction House"    -> player.performCommand("skyblock auction");
            case "Bazaar"           -> player.performCommand("skyblock bazaar");
            case "Profile"          -> player.performCommand("skyblock profile");
            case "Skills"           -> player.performCommand("skyblock skills");
            case "Warps"            -> player.performCommand("skyblock warps");
            case "Community Center" -> player.performCommand("skyblock community");
            default -> { /* unknown slot — ignore */ }
        }
    }
}
