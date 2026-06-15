package com.skyblock.plugin.menus;

import com.skyblock.core.accessory.AccessoryBagManager;
import com.skyblock.core.talisman.TalismanManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Accessory Bag menu.
 *
 * <p>A 54-slot (6-row) menu displaying the player's accessories (up to
 * {@link AccessoryBagManager#MAX_SLOTS}) in the inner slots, framed by a
 * {@code PURPLE_STAINED_GLASS_PANE} border, with a {@code NETHER_STAR} magic
 * power summary in slot 4, an info item, and a close button.</p>
 */
public class AccessoryBagMenu extends Menu {

    /** Inner slots (between the border), one per accessory. */
    private static final int[] SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    /** Slot for the magic power summary item. */
    private static final int MAGIC_POWER_SLOT = 4;

    /** Slot for the bag-summary info item. */
    private static final int INFO_SLOT = 49;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    private final Player player;

    public AccessoryBagMenu(Player player) {
        super("§dAccessory Bag §7(§6" + totalMagicPower(player) + " MP§7)", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        Set<TalismanManager.TalismanType> contents =
                AccessoryBagManager.getInstance().getContents(player.getUniqueId());
        List<TalismanManager.TalismanType> accessories = new ArrayList<>(contents);

        for (int i = 0; i < accessories.size() && i < SLOTS.length
                && i < AccessoryBagManager.MAX_SLOTS; i++) {
            TalismanManager.TalismanType type = accessories.get(i);
            setItem(SLOTS[i], new ItemBuilder(Material.GOLD_NUGGET)
                    .displayName("§6" + formatName(type))
                    .lore("§7+" + (int) type.bonus + " " + formatStatName(type.stat.name()),
                          "§8" + type.rarity.getDisplayName())
                    .build());
        }

        setItem(MAGIC_POWER_SLOT, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§dMagical Power")
                .lore("§7Total Magical Power: §d" + totalMagicPower())
                .build());

        setItem(INFO_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aAccessory Bag")
                .lore("§7Accessories: §f" + accessories.size() + "§7/§f" + AccessoryBagManager.MAX_SLOTS)
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }

    private int totalMagicPower() {
        return totalMagicPower(player);
    }

    private static int totalMagicPower(Player player) {
        int total = 0;
        AccessoryBagManager bag = AccessoryBagManager.getInstance();
        for (AccessoryBagManager.AccessoryTier tier : AccessoryBagManager.AccessoryTier.values()) {
            total += bag.getMagicPower(player.getUniqueId(), tier);
        }
        return total;
    }

    private static String formatName(TalismanManager.TalismanType type) {
        String raw = type.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder(raw.length());
        boolean cap = true;
        for (char c : raw.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = (c == ' ');
        }
        return sb.toString();
    }

    private static String formatStatName(String name) {
        String spaced = name.replace('_', ' ');
        StringBuilder sb = new StringBuilder(spaced.length());
        boolean cap = true;
        for (char c : spaced.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = (c == ' ');
        }
        return sb.toString();
    }
}
