package com.skyblock.plugin.menus;

import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.wardrobe.WardrobeManager.WardrobeSlot;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Wardrobe menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Wardrobe} showing seven armour-set
 * presets ({@link WardrobeSlot#SLOT_1} … {@link WardrobeSlot#SLOT_7}) across the
 * first inner row (slots 10-16), framed by a {@code GRAY_STAINED_GLASS_PANE}
 * border. Clicking an occupied preset equips that armour set; an info item and a
 * close button sit on the bottom row.</p>
 */
public class WardrobeMenu extends Menu {

    /** The seven preset slots displayed across the first inner row. */
    private static final int[] PRESET_SLOTS = {10, 11, 12, 13, 14, 15, 16};

    /** The seven wardrobe presets, one per displayed slot. */
    private static final WardrobeSlot[] PRESETS = {
            WardrobeSlot.SLOT_1,
            WardrobeSlot.SLOT_2,
            WardrobeSlot.SLOT_3,
            WardrobeSlot.SLOT_4,
            WardrobeSlot.SLOT_5,
            WardrobeSlot.SLOT_6,
            WardrobeSlot.SLOT_7
    };

    /** Slot for the info item. */
    private static final int INFO_SLOT = 49;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    private final Player player;

    public WardrobeMenu(Player player) {
        super("§6Wardrobe", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        WardrobeManager manager = WardrobeManager.getInstance();
        for (int i = 0; i < PRESETS.length; i++) {
            WardrobeSlot preset = PRESETS[i];
            boolean occupied = manager.getOutfit(player.getUniqueId(), preset) != null;
            setItem(PRESET_SLOTS[i],
                    new ItemBuilder(occupied ? Material.LEATHER_CHESTPLATE : Material.GRAY_STAINED_GLASS_PANE)
                            .displayName("§6" + preset.getDisplayName())
                            .lore(occupied ? "§7Click to equip" : "§7Empty")
                            .build(),
                    occupied ? event -> {
                        ItemStack[] armor = manager.getOutfit(player.getUniqueId(), preset);
                        if (armor != null) {
                            player.getInventory().setArmorContents(armor);
                            manager.setActiveArmorSet(player.getUniqueId(), preset.name());
                            player.sendMessage("§aEquipped " + preset.getDisplayName() + ".");
                            player.closeInventory();
                        }
                    } : null);
        }

        setItem(INFO_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aWardrobe")
                .lore("§7Presets: §f" + PRESETS.length)
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }
}
