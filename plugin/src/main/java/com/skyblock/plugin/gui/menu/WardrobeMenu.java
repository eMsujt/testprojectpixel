package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Wardrobe menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §6Wardrobe}. Nine armour-set
 * columns span rows 0–3: row 0 (slots 0–8) holds helmets, row 1 (slots 9–17)
 * chestplates, row 2 (slots 18–26) leggings, and row 3 (slots 27–35) boots.
 * Rows 4–5 are filled with gray glass panes, matching Hypixel's layout.</p>
 *
 * <p>{@link #wardrobeSlots} holds the nine armour sets as 4-element arrays
 * {@code [helmet, chestplate, leggings, boots]}; {@code null} elements
 * indicate empty slots.</p>
 */
public class WardrobeMenu extends Menu {

    private static final Material[] ARMOR_PIECES = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    };

    private static final String[] PIECE_NAMES = {
            "Helmet", "Chestplate", "Leggings", "Boots"
    };

    /** Nine armour sets; each element is {@code [helmet, chestplate, leggings, boots]}. */
    private final List<ItemStack[]> wardrobeSlots;

    public WardrobeMenu(Player player) {
        super("§9Wardrobe", 6);
        this.wardrobeSlots = buildWardrobeSlots(player);
    }

    private static List<ItemStack[]> buildWardrobeSlots(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] flat = profile.getWardrobeContents();

        List<ItemStack[]> slots = new ArrayList<>(9);
        for (int col = 0; col < 9; col++) {
            ItemStack[] set = new ItemStack[4];
            for (int row = 0; row < 4; row++) {
                if (flat != null) {
                    int idx = row * 9 + col;
                    set[row] = (idx < flat.length) ? flat[idx] : null;
                }
            }
            slots.add(set);
        }
        return slots;
    }

    @Override
    protected void build() {
        for (int col = 0; col < 9; col++) {
            ItemStack[] set = wardrobeSlots.get(col);
            int setNumber = col + 1;
            for (int row = 0; row < 4; row++) {
                int slot = row * 9 + col;
                ItemStack stored = set[row];
                if (stored != null) {
                    setItem(slot, stored, event -> event.setCancelled(true));
                } else {
                    setItem(slot, new ItemBuilder(ARMOR_PIECES[row])
                                    .displayName("§aWardrobe Slot " + setNumber + " §7- " + PIECE_NAMES[row])
                                    .lore("§7Empty", "§eClick to equip!")
                                    .build(),
                            event -> event.setCancelled(true));
                }
            }
        }

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 36; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }
}
