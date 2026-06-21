package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 6-row GUI titled {@code §6Wardrobe}. Shows all 9 wardrobe slots arranged as
 * 3 pages × 3 armor sets; locked slots display a barrier tile.
 */
public final class WardrobeMenu extends AbstractSkyBlockMenu {

    public static final int SLOT_COUNT = 9;

    private static final int[] SLOT_GUI_POSITIONS = {
            19, 20, 21,
            28, 29, 30,
            37, 38, 39
    };

    public WardrobeMenu(Player player) {
        super(player, "§6Wardrobe", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        buildHeader();
        buildSlots();
    }

    private void buildHeader() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        String active = mgr.getActiveArmorSet(player.getUniqueId());
        int saved = mgr.getOutfitNames(player.getUniqueId()).size();

        List<String> lore = new ArrayList<>();
        lore.add("§7Saved Outfits: §e" + saved + " §7/ §e" + WardrobeManager.MAX_OUTFITS);
        if (active != null) {
            lore.add("§7Active Set: §6" + active);
        } else {
            lore.add("§7Active Set: §cNone");
        }

        setItem(4, new ItemBuilder(Material.ARMOR_STAND)
                .displayName("§6Wardrobe")
                .lore(lore.toArray(new String[0]))
                .build());
    }

    private void buildSlots() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        WardrobeSlot[] slots = WardrobeSlot.values();
        String active = mgr.getActiveArmorSet(player.getUniqueId());

        for (int i = 0; i < SLOT_GUI_POSITIONS.length && i < slots.length; i++) {
            WardrobeSlot wardrobeSlot = slots[i];
            int guiPos = SLOT_GUI_POSITIONS[i];

            if (!mgr.isSlotUnlocked(player.getUniqueId(), wardrobeSlot)) {
                setItem(guiPos, new ItemBuilder(Material.BARRIER)
                        .displayName("§c" + wardrobeSlot.getDisplayName())
                        .lore("§7This slot is locked.")
                        .build());
                continue;
            }

            ItemStack[] armor = mgr.getOutfit(player.getUniqueId(), wardrobeSlot);
            boolean isActive = wardrobeSlot.name().equals(active);

            List<String> lore = new ArrayList<>();
            if (armor != null) {
                String[] labels = {"Helmet", "Chestplate", "Leggings", "Boots"};
                for (int a = 0; a < 4; a++) {
                    String piece = (armor[a] != null)
                            ? formatName(armor[a].getType().name())
                            : "Empty";
                    lore.add("§7" + labels[a] + ": §f" + piece);
                }
                if (isActive) {
                    lore.add("§a§lEQUIPPED");
                } else {
                    lore.add("§eClick to equip");
                }
            } else {
                lore.add("§7No outfit saved.");
            }

            Material icon = (armor != null) ? Material.IRON_CHESTPLATE : Material.GRAY_STAINED_GLASS_PANE;
            setItem(guiPos, new ItemBuilder(icon)
                    .displayName((isActive ? "§a" : "§6") + wardrobeSlot.getDisplayName())
                    .lore(lore.toArray(new String[0]))
                    .build());
        }
    }

    private static String formatName(String name) {
        String lower = name.replace('_', ' ').toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
