package com.skyblock.core.menu;

import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonClass;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class DungeonsMenu extends Menu {

    /** Inventory slots for the five DungeonClass icons (row 3, centred). */
    static final int[] CLASS_SLOTS = {20, 21, 22, 23, 24};

    static final Map<DungeonClass, Material> CLASS_ICONS = new EnumMap<>(DungeonClass.class);

    static {
        CLASS_ICONS.put(DungeonClass.HEALER,  Material.GOLDEN_APPLE);
        CLASS_ICONS.put(DungeonClass.MAGE,    Material.BLAZE_POWDER);
        CLASS_ICONS.put(DungeonClass.BERSERK, Material.IRON_SWORD);
        CLASS_ICONS.put(DungeonClass.ARCHER,  Material.BOW);
        CLASS_ICONS.put(DungeonClass.TANK,    Material.SHIELD);
    }

    private final UUID playerId;

    public DungeonsMenu(UUID playerId) {
        super("§5Dungeons", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = SkyblockUtils.buildItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        SkyblockUtils.fillBorder(getRows(), this::setItem, pane);

        DungeonManager manager = DungeonManager.getInstance();
        DungeonClass selected = manager.getClass(playerId);

        DungeonClass[] classes = DungeonClass.values();
        for (int i = 0; i < classes.length; i++) {
            DungeonClass cls = classes[i];
            double xp = manager.getClassXp(playerId, cls);
            boolean isSelected = cls.equals(selected);

            String name = (isSelected ? "§c§l" : "§c") + cls.getDisplayName();
            setItem(CLASS_SLOTS[i], SkyblockUtils.buildItem(CLASS_ICONS.get(cls),
                    name,
                    "§7XP: §e" + String.format("%.1f", xp),
                    isSelected ? "§a§l[SELECTED]" : "§7Click to select"));
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
