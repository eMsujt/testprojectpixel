package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.SkyblockUtil.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * GUI menu opened by {@code /wardrobe}. Renders the first nine wardrobe slots
 * (SLOT_1 – SLOT_9) from {@link WardrobeManager} as colored banner items: filled
 * slots show their banner, empty-but-unlocked slots show a banner too, and locked
 * slots display a gray-dye placeholder. The currently active armor set is
 * highlighted with an enchant glow.
 */
public final class WardrobeMenu extends Menu {

    static final int SLOT_COUNT = 9;

    /** First inventory slot of the centered armor-set row; slots occupy {@code FIRST_SLOT .. +8}. */
    static final int FIRST_SLOT = 18;

    private static final WardrobeSlot[] DISPLAY_SLOTS = {
            WardrobeSlot.SLOT_1, WardrobeSlot.SLOT_2, WardrobeSlot.SLOT_3,
            WardrobeSlot.SLOT_4, WardrobeSlot.SLOT_5, WardrobeSlot.SLOT_6,
            WardrobeSlot.SLOT_7, WardrobeSlot.SLOT_8, WardrobeSlot.SLOT_9
    };

    /** One distinct banner colour per displayed slot. */
    private static final Material[] SLOT_BANNERS = {
            Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER, Material.LIME_BANNER,
            Material.PINK_BANNER, Material.CYAN_BANNER, Material.RED_BANNER
    };

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
        String active = manager.getActiveArmorSet(playerId);

        for (int i = 0; i < SLOT_COUNT; i++) {
            WardrobeSlot ws = DISPLAY_SLOTS[i];
            int invSlot = FIRST_SLOT + i;

            if (!manager.isSlotUnlocked(playerId, ws)) {
                setItem(invSlot, new ItemBuilder(Material.GRAY_DYE)
                        .displayName("§7" + ws.getDisplayName())
                        .lore("§cLocked")
                        .build());
                continue;
            }

            boolean filled = manager.getOutfit(playerId, ws) != null;
            boolean isActive = ws.name().equals(active);

            ItemBuilder builder = new ItemBuilder(SLOT_BANNERS[i])
                    .displayName("§e" + ws.getDisplayName())
                    .lore(
                            filled ? "§7Saved armor set" : "§8Empty",
                            isActive ? "§aActive" : "§eClick to equip!");
            if (isActive) {
                builder.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
            }
            setItem(invSlot, builder.build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
