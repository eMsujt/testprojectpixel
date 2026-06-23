package com.skyblock.core.menu;

import com.skyblock.core.manager.ScoreboardManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * The SkyBlock Settings menu, opened from the SkyBlock Menu (slot 50). Holds the
 * player's personal toggles; currently a working "Sidebar Display" toggle that
 * shows or hides the scoreboard sidebar via {@link ScoreboardManager}.
 */
public final class SettingsMenu extends Menu {

    private final Player player;

    public SettingsMenu(Player player) {
        super("§aSettings", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        setItem(4, new ItemBuilder(Material.COMPARATOR).displayName("§aSettings")
                .lore("§7Personal SkyBlock options.").build());

        boolean sidebar = ScoreboardManager.getInstance().isSidebarVisible(player.getUniqueId());
        setItem(20, new ItemBuilder(sidebar ? Material.LIME_DYE : Material.GRAY_DYE)
                .displayName("§aSidebar Display: " + (sidebar ? "§aON" : "§cOFF"))
                .lore("§7Show or hide the SkyBlock", "§7sidebar scoreboard.", "",
                        "§eClick to toggle!").build(),
                e -> {
                    e.setCancelled(true);
                    ScoreboardManager.getInstance().setSidebarVisible(player, !sidebar);
                    new SettingsMenu(player).open(player);
                });

        setItem(49, new ItemBuilder(Material.ARROW).displayName("§7Back")
                .lore("§7Return to the SkyBlock Menu.").build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
    }
}
