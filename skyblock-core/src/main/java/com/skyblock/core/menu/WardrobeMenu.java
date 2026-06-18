package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * GUI menu opened by {@code /wardrobe}. Renders the first nine wardrobe slots
 * (SLOT_1 – SLOT_9) from {@link WardrobeManager}: each column shows the four
 * armor pieces (helmet → boots) for that slot. Locked slots display a gray-dye
 * placeholder; unlocked-but-empty slots show leather armor icons.
 */
public final class WardrobeMenu extends Menu {

    static final int SLOT_COUNT = 9;

    private static final WardrobeSlot[] DISPLAY_SLOTS = {
            WardrobeSlot.SLOT_1, WardrobeSlot.SLOT_2, WardrobeSlot.SLOT_3,
            WardrobeSlot.SLOT_4, WardrobeSlot.SLOT_5, WardrobeSlot.SLOT_6,
            WardrobeSlot.SLOT_7, WardrobeSlot.SLOT_8, WardrobeSlot.SLOT_9
    };

    private static final Material[] ARMOR_PLACEHOLDERS = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    };

    /** Armor array indices top-to-bottom: helmet=3, chestplate=2, leggings=1, boots=0. */
    private static final int[] ARMOR_INDICES = {3, 2, 1, 0};

    private final UUID playerId;

    public WardrobeMenu(Player player) {
        this(player.getUniqueId());
    }

    public WardrobeMenu(UUID playerId) {
        super("§eWardrobe", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        WardrobeManager manager = WardrobeManager.getInstance();

        for (int col = 0; col < SLOT_COUNT; col++) {
            WardrobeSlot ws = DISPLAY_SLOTS[col];
            boolean unlocked = manager.isSlotUnlocked(playerId, ws);
            ItemStack[] armor = unlocked ? manager.getOutfit(playerId, ws) : null;

            for (int row = 0; row < 4; row++) {
                int invSlot = (row + 1) * 9 + col;
                int armorIdx = ARMOR_INDICES[row];
                if (!unlocked) {
                    setItem(invSlot, new ItemBuilder(Material.GRAY_DYE)
                            .displayName("§7" + ws.getDisplayName())
                            .lore("§cLocked")
                            .build());
                } else {
                    ItemStack piece = (armor != null && armorIdx < armor.length) ? armor[armorIdx] : null;
                    boolean hasItem = piece != null && piece.getType() != Material.AIR;
                    if (hasItem) {
                        setItem(invSlot, new ItemBuilder(piece)
                                .displayName("§5" + ws.getDisplayName())
                                .lore("§7Click to equip!")
                                .build());
                    } else {
                        setItem(invSlot, new ItemBuilder(ARMOR_PLACEHOLDERS[row])
                                .displayName("§5" + ws.getDisplayName())
                                .lore("§7Empty", "§eClick to equip!")
                                .build());
                    }
                }
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
