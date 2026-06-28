package com.skyblock.core.menu;

import com.skyblock.core.booster.BoosterManager;
import com.skyblock.core.model.Stat;
import com.skyblock.core.quest.gui.QuestsMenu;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Menu (nether-star hub). Slot positions and tooltip lore are taken
 * verbatim from the wiki's SkyBlock Menu/UI page so each button matches Hypixel.
 */
public final class SkyBlockMenu extends Menu {

    private final Player player;

    public SkyBlockMenu(Player player) {
        super("§aSkyBlock Menu", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack bg = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, bg);

        StatsManager.PlayerStats stats = StatsManager.getInstance().getStats(player.getUniqueId());

        // Slot 8 — Report Island Name.
        setItem(8, new ItemBuilder(Material.ANVIL).displayName("§aReport Island Name")
                .lore("§7You can report this island if you",
                      "§7think it has an inappropriate name.",
                      "",
                      "§eClick to report it!").build(),
                e -> e.setCancelled(true));

        // Slot 13 — Your SkyBlock Profile (player head + core stats).
        setItem(13, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player)
                .displayName("§aYour SkyBlock Profile")
                .lore(
                        "§7View your equipment, stats,",
                        "§7and more!",
                        "",
                        "§c❤ Health §f" + trim(stats.getStat(Stat.HEALTH)),
                        " §a❈ Defense §f" + trim(stats.getStat(Stat.DEFENSE)),
                        " §f✦ Speed §f" + trim(stats.getStat(Stat.SPEED)),
                        " §c❁ Strength §f" + trim(stats.getStat(Stat.STRENGTH)),
                        " §b✎ Intelligence §f" + trim(stats.getStat(Stat.INTELLIGENCE)),
                        " §9☣ Crit Chance §f" + trim(stats.getStat(Stat.CRIT_CHANCE)) + "%",
                        " §9☠ Crit Damage §f" + trim(stats.getStat(Stat.CRIT_DAMAGE)) + "%",
                        "",
                        "§eClick to view!").build(),
                e -> { e.setCancelled(true); new EquipmentMenu(player).open(player); });

        // Row 3 — Skills / progression.
        setItem(19, new ItemBuilder(Material.DIAMOND_SWORD).displayName("§aYour Skills")
                .lore("§7View your Skill progression and", "§7rewards.", "",
                      "§8Also accessible via /skills", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new SkillsMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });
        setItem(20, new ItemBuilder(Material.PAINTING).displayName("§aCollections")
                .lore("§7View all of the items available", "§7in SkyBlock. Collect more of an",
                      "§7item to unlock rewards on your", "§7way to becoming a master of",
                      "§7SkyBlock!", "", "§8Also accessible via /collection", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new CollectionsMenu(player).open(player); });
        setItem(21, new ItemBuilder(Material.KNOWLEDGE_BOOK).displayName("§aRecipe Book")
                .lore("§7Through your adventure, you will", "§7unlock recipes for all kinds of",
                      "§7special items! You can view how", "§7to craft these items here.", "",
                      "§8Also accessible via /recipes", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new RecipeBrowserMenu(player).open(player); });
        setItem(22, new ItemBuilder(Material.EXPERIENCE_BOTTLE).displayName("§aSkyBlock Leveling")
                .lore("§7Determine how far you've", "§7progressed in SkyBlock and earn",
                      "§7rewards from completing unique", "§7tasks.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new SkyblockLevelMenu(player).open(player); });
        setItem(23, new ItemBuilder(Material.WRITABLE_BOOK).displayName("§aQuests & Chapters")
                .lore("§7Each island has its own series of", "§bChapters §7for you to complete!", "",
                      "§7Complete tasks within a Chapter to", "§7earn small §6rewards§7, or complete",
                      "§7entire Chapters to earn big ones!", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new QuestsMenu(player).open(player); });
        setItem(24, new ItemBuilder(Material.CLOCK).displayName("§aCalendar and Events")
                .lore("§7View the SkyBlock Calendar,", "§7upcoming events, and event", "§7rewards!", "",
                      "§8Also accessible via /calendar", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new CalendarMenu().open(player); });
        setItem(25, new ItemBuilder(Material.ENDER_CHEST).displayName("§aStorage")
                .lore("§7Store global items that you", "§7want to access at any time",
                      "§7from anywhere here.", "", "§8Also accessible via /storage", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new StorageMenu(player).open(player); });

        // Row 4 — bags / customization.
        setItem(29, new ItemBuilder(Material.BUNDLE).displayName("§aYour Bags")
                .lore("§7Different bags allow you to", "§7store many different items",
                      "§7inside!", "", "§8Also accessible via /bags", "", "§eClick to open!").build(),
                e -> { e.setCancelled(true); new AccessoryBagMenu(player).open(player); });
        setItem(30, new ItemBuilder(Material.BONE).displayName("§aPets")
                .lore("§7View and manage all your", "§7Pets.", "",
                      "§7Level up your pets faster by", "§7gaining xp in their favorite", "§7Skill!", "",
                      "§8Also accessible via /pets", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new PetMenu(player).open(player); });
        setItem(31, new ItemBuilder(Material.CRAFTING_TABLE).displayName("§aCrafting Table")
                .lore("§7Opens the crafting grid.", "", "§8Also accessible via /craft", "", "§eClick to open!").build(),
                e -> { e.setCancelled(true); new CraftingMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });
        setItem(32, new ItemBuilder(Material.LEATHER_CHESTPLATE).displayName("§aWardrobe")
                .lore("§7Store armor sets and quickly", "§7swap between them!", "",
                      "§8Also accessible via /wardrobe", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new WardrobeMenu(player).open(player); });
        setItem(33, new ItemBuilder(Material.GOLD_INGOT).displayName("§aPersonal Bank")
                .lore("§7Contact your Banker from", "§7anywhere.", "",
                      "§8Also accessible via /bank", "", "§eClick to open!").build(),
                e -> { e.setCancelled(true); new BankMenu(player).open(player); });

        // Bottom row — travel / profile / settings / cookie.
        setItem(47, new ItemBuilder(Material.COMPASS).displayName("§bFast Travel")
                .lore("§7Teleport to islands you've", "§7already visited.", "",
                      "§8Also accessible via /warp", "§8Right-click to warp home!", "§eClick to pick location!").build(),
                e -> { e.setCancelled(true); new WarpMenu(player).open(player); });
        setItem(48, new ItemBuilder(Material.NAME_TAG).displayName("§aProfile Management")
                .lore("§7You can have multiple", "§7SkyBlock Profiles at the", "§7same time.", "",
                      "§7Each profile has its own", "§7island, inventory, quest", "§7log...", "",
                      "§8Also accessible via /profiles", "", "§eClick to manage!").build(),
                e -> { e.setCancelled(true); new ProfileMenu(player).open(player); });
        setItem(49, new ItemBuilder(Material.BARRIER).displayName("§cClose").build(),
                e -> { e.setCancelled(true); e.getWhoClicked().closeInventory(); });
        setItem(50, new ItemBuilder(Material.REDSTONE_TORCH).displayName("§aSettings")
                .lore("§7View and edit your SkyBlock", "§7settings.", "",
                      "§8Also accessible via /viewsettings", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new SettingsMenu(player).open(player); });

        // Slot 51 — Booster Cookie (live buff status).
        BoosterManager booster = BoosterManager.getInstance();
        long now = System.currentTimeMillis();
        boolean cookieActive = booster.hasBooster(player.getUniqueId())
                && booster.getExpiry(player.getUniqueId()) > now;
        ItemBuilder cookie = new ItemBuilder(Material.COOKIE).displayName("§6Booster Cookie");
        if (cookieActive) {
            cookie.lore("§7Obtain the §dCookie Buff", "§7from Booster Cookies in the",
                    "§7hub's community shop.", "",
                    "§aActive! §7Time left: §e" + formatDuration(booster.getExpiry(player.getUniqueId()) - now), "",
                    "§eClick to get all the info!");
        } else {
            cookie.lore("§7Obtain the §dCookie Buff", "§7from Booster Cookies in the",
                    "§7hub's community shop.", "",
                    "§cYou have no active Booster Cookie.", "",
                    "§eClick to get all the info!");
        }
        setItem(51, cookie.build(), e -> {
            e.setCancelled(true);
            player.sendMessage(cookieActive
                    ? "§6Booster Cookie: §aactive §7(" + formatDuration(booster.getExpiry(player.getUniqueId()) - System.currentTimeMillis()) + " left)"
                    : "§6Booster Cookie: §cnot active.");
        });
    }

    /** Formats a stat value, dropping the decimal for whole numbers. */
    private static String trim(double value) {
        return value == Math.floor(value) ? Long.toString((long) value) : Double.toString(value);
    }

    /** Formats a millisecond duration as a coarse {@code 3d 5h} / {@code 5h 10m} / {@code 10m} string. */
    private static String formatDuration(long ms) {
        long totalSec = Math.max(0, ms / 1000);
        long days = totalSec / 86400;
        long hours = (totalSec % 86400) / 3600;
        long mins = (totalSec % 3600) / 60;
        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + mins + "m";
        return mins + "m";
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
    }
}
