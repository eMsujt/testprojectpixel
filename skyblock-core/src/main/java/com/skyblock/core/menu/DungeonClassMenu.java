package com.skyblock.core.menu;

import com.skyblock.core.manager.DungeonClassManager;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonClass;
import com.skyblock.core.model.Stat;
import com.skyblock.core.util.SkyblockUtil.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * GUI menu opened by {@code /dungeonclass}. Renders all five dungeon classes as
 * named icon items with their passive-stat-per-level bundle, the player's current
 * class level, total accumulated bonuses, and a selection indicator.
 */
public final class DungeonClassMenu extends Menu {

    /** Inventory slots for the five class icons in the middle row. */
    private static final int[] CLASS_SLOTS = {10, 11, 12, 13, 14};

    public static final Map<DungeonClass, Material> CLASS_ICONS = new EnumMap<>(DungeonClass.class);

    static {
        CLASS_ICONS.put(DungeonClass.HEALER,  Material.GOLDEN_APPLE);
        CLASS_ICONS.put(DungeonClass.MAGE,    Material.BLAZE_POWDER);
        CLASS_ICONS.put(DungeonClass.BERSERK, Material.IRON_SWORD);
        CLASS_ICONS.put(DungeonClass.ARCHER,  Material.BOW);
        CLASS_ICONS.put(DungeonClass.TANK,    Material.SHIELD);
    }

    private final UUID playerId;

    public DungeonClassMenu(Player player) {
        this(player.getUniqueId());
    }

    public DungeonClassMenu(UUID playerId) {
        super("§3Dungeon Classes", 3);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int s = 0; s < 9; s++)  setItem(s, pane);
        for (int s = 9; s < 18; s++) setItem(s, pane);
        for (int s = 18; s < 27; s++) setItem(s, pane);

        DungeonManager dungeons = DungeonManager.getInstance();
        DungeonClassManager classManager = DungeonClassManager.getInstance();
        DungeonClass selected = dungeons.getClass(playerId);

        DungeonClass[] classes = DungeonClass.values();
        for (int i = 0; i < classes.length; i++) {
            DungeonClass cls = classes[i];
            int level = dungeons.getClassLevel(playerId, cls);
            boolean isSelected = cls.equals(selected);

            List<String> lore = new ArrayList<>();
            lore.add("§7Passive stats per level:");
            classManager.getPassiveStatsPerLevel(cls).entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> lore.add(String.format("§7  +%.1f %s", e.getValue(), e.getKey().getDisplayName())));
            lore.add("§7Level: §a" + level + "§7/§a" + DungeonManager.MAX_CLASS_LEVEL);
            if (level > 0) {
                lore.add("§7Total bonuses:");
                classManager.getPassiveStats(cls, level).entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(e -> lore.add(String.format("§7  +%.1f %s", e.getValue(), e.getKey().getDisplayName())));
            }
            if (isSelected) {
                lore.add("§a§l[SELECTED]");
            }

            String name = (isSelected ? "§b§l" : "§b") + cls.getDisplayName();
            setItem(CLASS_SLOTS[i], new ItemBuilder(CLASS_ICONS.get(cls))
                    .displayName(name)
                    .lore(lore)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
