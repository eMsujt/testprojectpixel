package com.skyblock.core.menu;

import com.skyblock.core.booster.BoosterManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class SkyBlockMenu extends Menu {

    private final Player player;

    public SkyBlockMenu(Player player) {
        super("§aSkyBlock Menu", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        // Hypixel's SkyBlock Menu sits on a solid black-pane background.
        ItemStack bg = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, bg);

        // Profile head (centre of row 2).
        setItem(13, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player)
                .displayName("§aYour SkyBlock Profile")
                .lore("§7Name: §a" + player.getName(), "", "§7View your stats, skills", "§7and overall progress.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new StatsMenu(player).open(player); });

        // Row: Skills & learning (slots 19-25).
        setItem(19, new ItemBuilder(Material.DIAMOND_SWORD).displayName("§aYour Skills")
                .lore("§7View your skill levels and XP.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new SkillsMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });
        setItem(20, new ItemBuilder(Material.PAINTING).displayName("§aCollections")
                .lore("§7Track your collection progress.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new CollectionsMenu(player).open(player); });
        setItem(21, new ItemBuilder(Material.KNOWLEDGE_BOOK).displayName("§aRecipe Book")
                .lore("§7Browse every SkyBlock item", "§7and its recipe.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new ItemsMenu(player).open(player); });
        setItem(22, new ItemBuilder(Material.EXPERIENCE_BOTTLE).displayName("§aSkyBlock Leveling")
                .lore("§7Track your SkyBlock level.").build());
        setItem(23, new ItemBuilder(Material.WRITABLE_BOOK).displayName("§aQuests & Chapters")
                .lore("§7Track your quest progress.").build());
        setItem(24, new ItemBuilder(Material.CLOCK).displayName("§aCalendar and Events")
                .lore("§7See upcoming SkyBlock events.").build());
        setItem(25, new ItemBuilder(Material.ENDER_CHEST).displayName("§aStorage")
                .lore("§7Access your personal storage.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new StorageMenu(player).open(player); });

        // Row: inventory & customization (slots 29-33).
        setItem(29, new ItemBuilder(Material.BUNDLE).displayName("§aYour Bags")
                .lore("§7Your accessory bag, sacks", "§7and more.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new AccessoryBagMenu(player).open(player); });
        setItem(30, new ItemBuilder(Material.BONE).displayName("§aPets")
                .lore("§7Manage your pets.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new PetMenu(player).open(player); });
        setItem(31, new ItemBuilder(Material.CRAFTING_TABLE).displayName("§aCrafting Table")
                .lore("§7Craft items.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new CraftingMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });
        setItem(32, new ItemBuilder(Material.LEATHER_CHESTPLATE).displayName("§aWardrobe")
                .lore("§7Manage your armor sets.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new WardrobeMenu(player).open(player); });
        setItem(33, new ItemBuilder(Material.GOLD_INGOT).displayName("§aPersonal Bank")
                .lore("§7Deposit and withdraw coins.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new BankMenu(player).open(player); });

        // Bottom navigation (slots 47-51).
        setItem(47, new ItemBuilder(Material.COMPASS).displayName("§aFast Travel")
                .lore("§7Warp around the world.").build());
        setItem(48, new ItemBuilder(Material.NAME_TAG).displayName("§aProfile Management")
                .lore("§7Manage your profiles.", "", "§eClick to view!").build(),
                e -> { e.setCancelled(true); new ProfileMenu(player).open(player); });
        setItem(49, new ItemBuilder(Material.BARRIER).displayName("§cClose").build(),
                e -> { e.setCancelled(true); e.getWhoClicked().closeInventory(); });
        setItem(50, new ItemBuilder(Material.COMPARATOR).displayName("§aSettings")
                .lore("§7Adjust your SkyBlock settings.").build());

        // Booster Cookie — live buff status from the BoosterManager.
        BoosterManager booster = BoosterManager.getInstance();
        long now = System.currentTimeMillis();
        boolean cookieActive = booster.hasBooster(player.getUniqueId())
                && booster.getExpiry(player.getUniqueId()) > now;
        ItemBuilder cookie = new ItemBuilder(Material.COOKIE).displayName("§6Booster Cookie");
        if (cookieActive) {
            cookie.lore("§7Boosts your gains while active.", "",
                    "§aActive!",
                    "§7Multiplier: §6" + trim(booster.getMultiplier(player.getUniqueId())) + "x",
                    "§7Time left: §e" + formatDuration(booster.getExpiry(player.getUniqueId()) - now));
        } else {
            cookie.lore("§7Boosts your gains while active.", "",
                    "§cYou have no active Booster Cookie.");
        }
        setItem(51, cookie.build(), e -> {
            e.setCancelled(true);
            if (cookieActive) {
                player.sendMessage("§6Booster Cookie: §aactive §7("
                        + formatDuration(booster.getExpiry(player.getUniqueId()) - System.currentTimeMillis())
                        + " left, §6" + trim(booster.getMultiplier(player.getUniqueId())) + "x§7)");
            } else {
                player.sendMessage("§6Booster Cookie: §cnot active.");
            }
        });
    }

    /** Formats a stat multiplier, dropping the decimal for whole numbers (e.g. {@code 2}, {@code 1.5}). */
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
