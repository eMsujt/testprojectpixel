package com.skyblock.core.menu;

import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.SlayerBoss;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * 54-slot Slayer overview menu. Shows the 5 canonical slayer bosses
 * (Revenant Horror / Tarantula Broodfather / Sven Packmaster /
 * Voidgloom Seraph / Inferno Demonlord) as mob-head items with the
 * player's current level, XP and kill count for each {@link SlayerType}.
 */
public final class SlayerMenu extends Menu {

    static final int[] BOSS_SLOTS = {20, 21, 22, 23, 24};

    private static final int SUMMARY_SLOT = 49;

    private static final SlayerBoss[] DISPLAYED_BOSSES = {
            SlayerBoss.REVENANT_HORROR,
            SlayerBoss.TARANTULA_BROODFATHER,
            SlayerBoss.SVEN_PACKMASTER,
            SlayerBoss.VOIDGLOOM_SERAPH,
            SlayerBoss.INFERNO_DEMONLORD
    };

    private static final Map<SlayerType, Material> HEAD_ICONS = new EnumMap<>(SlayerType.class);

    static {
        HEAD_ICONS.put(SlayerType.ZOMBIE,   Material.ZOMBIE_HEAD);
        HEAD_ICONS.put(SlayerType.SPIDER,   Material.COBWEB);
        HEAD_ICONS.put(SlayerType.WOLF,     Material.BONE_BLOCK);
        HEAD_ICONS.put(SlayerType.ENDERMAN, Material.ENDER_EYE);
        HEAD_ICONS.put(SlayerType.BLAZE,    Material.BLAZE_ROD);
    }

    private final UUID playerId;

    public SlayerMenu(UUID playerId) {
        super("§cSlayer", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        setItem(18, pane); setItem(19, pane);
        setItem(25, pane); setItem(26, pane);

        SlayerManager manager = SlayerManager.getInstance();

        for (int i = 0; i < DISPLAYED_BOSSES.length; i++) {
            SlayerBoss boss = DISPLAYED_BOSSES[i];
            SlayerType type = boss.type;
            int level = manager.getLevel(playerId, type);
            long xp = manager.getExperience(playerId, type);
            int kills = manager.getKillCount(playerId, type);
            int[] data = SlayerManager.SLAYER_BOSS_DATA.get(type.name());
            int maxLevel = data != null ? data[0] : SlayerManager.MAX_LEVEL;

            setItem(BOSS_SLOTS[i], new ItemBuilder(HEAD_ICONS.get(type))
                    .displayName("§c" + boss.getDisplayName())
                    .lore(
                            "§7Type: §e" + type.getDisplayName(),
                            "§7Level: §e" + level + "§7/§e" + maxLevel,
                            "§7XP: §e" + xp,
                            "§7Bosses slain: §e" + kills)
                    .build());
        }

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.DIAMOND_SWORD)
                .displayName("§cSlayer Overview")
                .lore(
                        "§7Defeat slayer bosses to earn",
                        "§7slayer XP and rare drops.")
                .build());
    }
}
