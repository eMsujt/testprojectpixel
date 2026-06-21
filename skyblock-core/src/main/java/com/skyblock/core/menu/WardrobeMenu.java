package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 6-row Wardrobe menu. Slot 4 holds a player-head header; the nine armor presets
 * stored in {@link WardrobeManager} are laid out across row 2 (inventory slots
 * {@code 9}–{@code 17}). Locked slots show a barrier; unlocked but empty slots
 * show a gray pane; occupied slots show the saved helmet.
 */
public final class WardrobeMenu extends Menu {

    public static final int SLOT_COUNT = 9;

    /** Inventory slot of the player-head header. */
    private static final int HEADER_SLOT = 4;
    /** Inventory slot of the first armor preset; presets fill {@code OFFSET..OFFSET+8}. */
    private static final int PRESET_OFFSET = 9;

    private final UUID playerId;

    public WardrobeMenu(UUID playerId) {
        super("§eWardrobe", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        setItem(HEADER_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§eWardrobe")
                .lore("§7Save and swap armor outfits.")
                .build());

        WardrobeManager mgr = WardrobeManager.getInstance();
        WardrobeSlot[] slots = WardrobeSlot.values();
        for (int i = 0; i < SLOT_COUNT; i++) {
            WardrobeSlot slot = slots[i];
            int target = PRESET_OFFSET + i;
            if (!mgr.isSlotUnlocked(playerId, slot)) {
                setItem(target, new ItemBuilder(Material.BARRIER)
                        .displayName("§cLocked")
                        .lore("§7This wardrobe slot is locked.")
                        .build());
            } else {
                ItemStack[] armor = mgr.getOutfit(playerId, slot);
                if (armor != null && armor[0] != null) {
                    setItem(target, new ItemBuilder(armor[0])
                            .displayName("§e" + slot.getDisplayName())
                            .build());
                } else {
                    setItem(target, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .displayName("§7" + slot.getDisplayName())
                            .lore("§7No outfit saved.")
                            .build());
                }
            }
        }
    }

    @Override
    public void open(Player player) {
        super.open(player);
    }
}
