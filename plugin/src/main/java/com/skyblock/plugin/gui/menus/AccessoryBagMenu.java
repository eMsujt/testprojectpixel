package com.skyblock.plugin.gui.menus;

import com.skyblock.core.accessory.AccessoryBagManager;
import com.skyblock.core.talisman.TalismanManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Accessory Bag menu.
 *
 * <p>A 54-slot (6-row) menu displaying the player's accessories (up to
 * {@link AccessoryBagManager#MAX_SLOTS}) in slots 0–44, with an info item in
 * slot 49 showing the total number of accessories equipped.</p>
 */
public class AccessoryBagMenu extends Menu {

    /** Slot for the bag-summary info item. */
    private static final int INFO_SLOT = 49;

    private final Player player;

    public AccessoryBagMenu(Player player) {
        super("Accessory Bag", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        Set<TalismanManager.TalismanType> contents =
                AccessoryBagManager.getInstance().getContents(player.getUniqueId());
        List<TalismanManager.TalismanType> accessories = new ArrayList<>(contents);

        for (int i = 0; i < accessories.size() && i < AccessoryBagManager.MAX_SLOTS; i++) {
            TalismanManager.TalismanType type = accessories.get(i);
            setItem(i, new ItemBuilder(Material.GOLD_NUGGET)
                    .displayName("§6" + formatName(type))
                    .lore("§7+" + (int) type.bonus + " " + formatStatName(type.stat.name()),
                          "§8" + type.rarity.getDisplayName())
                    .build());
        }

        setItem(INFO_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aAccessory Bag")
                .lore("§7Accessories: §f" + accessories.size() + "§7/§f" + AccessoryBagManager.MAX_SLOTS)
                .build());
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
