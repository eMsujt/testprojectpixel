package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 4-row Wardrobe menu. Slots 0-8 each represent one of the nine armor presets
 * stored in {@link WardrobeManager}. Locked slots show a barrier; unlocked but
 * empty slots show a gray pane; occupied slots show the saved helmet.
 */
public final class WardrobeMenu extends Menu {

    public static final int SLOT_COUNT = 9;

    private final UUID playerId;

    public WardrobeMenu(UUID playerId) {
        super("§6Wardrobe", 4);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        WardrobeSlot[] slots = WardrobeSlot.values();
        for (int i = 0; i < SLOT_COUNT; i++) {
            WardrobeSlot slot = slots[i];
            if (!mgr.isSlotUnlocked(playerId, slot)) {
                setItem(i, new ItemBuilder(Material.BARRIER)
                        .displayName("§cLocked")
                        .lore("§7This wardrobe slot is locked.")
                        .build());
            } else {
                ItemStack[] armor = mgr.getOutfit(playerId, slot);
                if (armor != null && armor[0] != null) {
                    setItem(i, new ItemBuilder(armor[0])
                            .displayName("§e" + slot.getDisplayName())
                            .build());
                } else {
                    setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
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
