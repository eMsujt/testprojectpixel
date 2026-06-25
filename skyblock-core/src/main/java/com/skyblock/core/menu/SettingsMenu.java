package com.skyblock.core.menu;

import com.skyblock.core.manager.ScoreboardManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Settings menu, opened from the SkyBlock Menu (slot 50). The main
 * view is laid out 1:1 with Hypixel's Settings GUI (wiki Settings/UI): the four
 * category tiles (Personal 10, Comms 12, Island Settings 14, API Settings 16),
 * the Double Tap to Drop (28) / Profile Viewer (30) toggles with their Lime Dye
 * state items (37/39), Island Management (32) and Tablist Widgets (34), plus a
 * Go Back arrow (48). The Personal tile opens a sub-view holding the working
 * "Sidebar Display" (User Interface) toggle.
 */
public final class SettingsMenu extends Menu {

    private final Player player;
    /** null = main settings; "personal" = the Personal sub-view. */
    private final String section;

    public SettingsMenu(Player player) {
        this(player, null);
    }

    private SettingsMenu(Player player, String section) {
        super(section == null ? "Settings" : "Settings ➜ Personal", 6);
        this.player = player;
        this.section = section;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, pane);

        if ("personal".equals(section)) {
            buildPersonal();
        } else {
            buildMain();
        }
    }

    private void buildMain() {
        setItem(10, new ItemBuilder(Material.PLAYER_HEAD).displayName("§aPersonal")
                .lore("§7General settings related to your", "§7experience.", "",
                      "§7Includes:", "§e⇄⇄ §7Sounds", "§e⇄⇄ §7User Interface",
                      "§e⇄⇄ §7Chat Feedback", "§e⇄⇄ §7Fishing Settings",
                      "§a✔ §7Player Trading", "§a✔ §7Inventory Full Notifications",
                      "§a✔ §7Pickup Arrows with Full Quiver", "",
                      "§eClick for settings!").build(),
                e -> { e.setCancelled(true); new SettingsMenu(player, "personal").open(player); });

        setItem(12, new ItemBuilder(Material.OAK_SIGN).displayName("§aComms")
                .lore("§7Tweak notifications and invites you", "§7get from other players.", "",
                      "§7Includes:", "§a✔ §7Death Messages", "§a✔ §7Guesting Invites",
                      "§a✔ §7Guesting Notifications", "§a✔ §7Co-op Invites",
                      "§a✔ §7Co-op Travel Notifications", "§a✔ §7Bid Notifications",
                      "§a✔ §7Outbid Notifications", "§a✔ §7Fill Notifications", "",
                      "§eClick for settings!").build(),
                e -> e.setCancelled(true));

        setItem(14, new ItemBuilder(Material.REPEATER).displayName("§aIsland Settings")
                .lore("§7Edit SkyBlock settings regarding", "§7your island.", "",
                      "§7§bIsland settings are shared with your", "§bco-op partners!", "",
                      "§eClick for more settings!").build(),
                e -> e.setCancelled(true));

        setItem(16, new ItemBuilder(Material.COMPARATOR).displayName("§aAPI Settings")
                .lore("§7Edit settings regarding third-party", "§7access to your SkyBlock profile.", "",
                      "§7The Hypixel API lets apps and", "§7websites display your player info",
                      "§7and games progress.", "",
                      "§7§bAPI settings apply to your current", "§bprofile only!", "",
                      "§eClick for more settings!").build(),
                e -> e.setCancelled(true));

        setItem(28, new ItemBuilder(Material.DIAMOND_SWORD).displayName("§aDouble Tap to Drop")
                .lore("§7Double tap the drop button to drop", "§7certain items.").build(),
                e -> e.setCancelled(true));
        setItem(30, new ItemBuilder(Material.PLAYER_HEAD).displayName("§aProfile Viewer")
                .lore("§7View player profiles on right-click.").build(),
                e -> e.setCancelled(true));
        setItem(32, new ItemBuilder(Material.GRASS_BLOCK).displayName("§aIsland Management")
                .lore("§7View and manage island settings!", "", "§eClick to view!").build(),
                e -> e.setCancelled(true));
        setItem(34, new ItemBuilder(Material.BOOKSHELF).displayName("§aTablist Widgets")
                .lore("§7View and manage Tablist Widget", "§7settings.", "", "§eClick to view!").build(),
                e -> e.setCancelled(true));

        setItem(37, new ItemBuilder(Material.LIME_DYE).displayName("§aDouble Tap to Drop")
                .lore("§7Click to disable!").build(), e -> e.setCancelled(true));
        setItem(39, new ItemBuilder(Material.LIME_DYE).displayName("§aProfile Viewer")
                .lore("§7Click to disable!").build(), e -> e.setCancelled(true));

        setItem(48, new ItemBuilder(Material.ARROW).displayName("§aGo Back")
                .lore("§7To SkyBlock Menu").build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    private void buildPersonal() {
        boolean sidebar = ScoreboardManager.getInstance().isSidebarVisible(player.getUniqueId());
        setItem(4, new ItemBuilder(Material.PLAYER_HEAD).displayName("§aUser Interface")
                .lore("§7Personal interface settings.").build(), e -> e.setCancelled(true));

        setItem(20, new ItemBuilder(sidebar ? Material.LIME_DYE : Material.GRAY_DYE)
                .displayName("§aSidebar Display: " + (sidebar ? "§aON" : "§cOFF"))
                .lore("§7Show or hide the SkyBlock", "§7sidebar scoreboard.", "",
                      "§eClick to toggle!").build(),
                e -> {
                    e.setCancelled(true);
                    ScoreboardManager.getInstance().setSidebarVisible(player, !sidebar);
                    new SettingsMenu(player, "personal").open(player);
                });

        setItem(48, new ItemBuilder(Material.ARROW).displayName("§aGo Back")
                .lore("§7To Settings").build(),
                e -> { e.setCancelled(true); new SettingsMenu(player).open(player); });
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
    }
}
