package com.skyblock.core.menu;

import com.skyblock.core.manager.DungeonsManager;
import com.skyblock.core.manager.DungeonsManager.DungeonClass;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class DungeonsMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§bDungeons";
    private static final int ROWS = 6;
    private static final int CLOSE_SLOT = 49;

    /** Inventory slots for the five DungeonClass icons (row 3, centred). */
    static final int[] CLASS_SLOTS = {20, 21, 22, 23, 24};

    static final Map<DungeonClass, Material> CLASS_ICONS = new EnumMap<>(DungeonClass.class);

    static {
        CLASS_ICONS.put(DungeonClass.HEALER,  Material.SPLASH_POTION);
        CLASS_ICONS.put(DungeonClass.MAGE,    Material.BLAZE_ROD);
        CLASS_ICONS.put(DungeonClass.BERSERK, Material.IRON_SWORD);
        CLASS_ICONS.put(DungeonClass.ARCHER,  Material.BOW);
        CLASS_ICONS.put(DungeonClass.TANK,    Material.LEATHER_CHESTPLATE);
    }

    public DungeonsMenu(Player player) {
        super(player, TITLE, ROWS);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        DungeonsManager manager = DungeonsManager.getInstance();

        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < ROWS * 9; slot++) {
            setItem(slot, pane);
        }

        int highestFloor = manager.getHighestFloor(playerId);
        setItem(4, new ItemBuilder(Material.ENDER_EYE)
                .displayName("§5Catacombs")
                .lore(
                        "§7Highest Floor: §e" + (highestFloor > 0 ? "F" + highestFloor : "None"),
                        "",
                        "§7Select a class below to enter",
                        "§7the Catacombs dungeons.")
                .build());

        DungeonClass selected = manager.getPlayerClass(playerId);
        DungeonClass[] classes = DungeonClass.values();
        for (int i = 0; i < classes.length && i < CLASS_SLOTS.length; i++) {
            DungeonClass cls = classes[i];
            boolean isSelected = cls.equals(selected);
            double xp = manager.getClassXp(playerId, cls);
            int level = manager.getClassLevel(playerId, cls);
            String displayName = cls.name().charAt(0) + cls.name().substring(1).toLowerCase();

            String name = (isSelected ? "§d§l" : "§d") + displayName;
            setItem(CLASS_SLOTS[i], new ItemBuilder(CLASS_ICONS.get(cls))
                    .displayName(name)
                    .lore(
                            "§7Level: §e" + level,
                            "§7XP: §e" + String.format("%.1f", xp),
                            "",
                            isSelected ? "§a§l[SELECTED]" : "§7Click to select")
                    .build());
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER).displayName("§cClose").build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
