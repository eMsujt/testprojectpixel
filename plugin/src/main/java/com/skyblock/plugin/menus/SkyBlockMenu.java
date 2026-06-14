package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The main SkyBlock Menu hub.
 *
 * <p>A 54-slot chest menu titled {@code §6SkyBlock Menu} that replicates
 * Hypixel's layout: a {@link Material#GRAY_STAINED_GLASS_PANE} border framing a
 * grid of icons linking to the game's subsystems.</p>
 */
public class SkyBlockMenu extends Menu {

    /** A SkyBlock Menu entry: its icon, display name, and slot. */
    private enum Entry {
        SKILLS(10, Material.DIAMOND_SWORD, "§aYour Skills", "§7View your skill progress."),
        COLLECTION(11, Material.PAINTING, "§eCollection", "§7View your collections."),
        RECIPE_BOOK(12, Material.CRAFTING_TABLE, "§6Recipe Book", "§7Browse craftable items."),
        PROFILE(13, Material.PLAYER_HEAD, "§bYour SkyBlock Profile", "§7View your profile stats."),
        ACCESSORY_BAG(14, Material.FLOWER_POT, "§dAccessory Bag", "§7Manage your accessories."),
        QUESTS(15, Material.BOOK, "§aQuests", "§7Track your quests."),
        CALENDAR(16, Material.CLOCK, "§eCalendar and Events", "§7See upcoming events."),
        PETS(19, Material.BONE, "§7Pets", "§7Manage your pets."),
        WARDROBE(20, Material.LEATHER_CHESTPLATE, "§6Wardrobe", "§7Equip your armour sets."),
        STORAGE(21, Material.ENDER_CHEST, "§aStorage", "§7Access your storage."),
        BANK(22, Material.GOLD_INGOT, "§6Bank Account", "§7Manage your coins."),
        BAZAAR(23, Material.GOLD_NUGGET, "§6Bazaar", "§7Buy and sell goods."),
        TRADES(24, Material.EMERALD, "§aTrades", "§7Trade with NPCs."),
        FAST_TRAVEL(25, Material.ENDER_PEARL, "§5Fast Travel", "§7Warp around the map.");

        private final int slot;
        private final Material icon;
        private final String displayName;
        private final String lore;

        Entry(int slot, Material icon, String displayName, String lore) {
            this.slot = slot;
            this.icon = icon;
            this.displayName = displayName;
            this.lore = lore;
        }
    }

    public SkyBlockMenu() {
        super("§6SkyBlock Menu", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Entry entry : Entry.values()) {
            setItem(entry.slot, new ItemBuilder(entry.icon)
                    .displayName(entry.displayName)
                    .lore(entry.lore)
                    .build());
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
