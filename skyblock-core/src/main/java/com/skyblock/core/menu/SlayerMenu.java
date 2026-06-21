package com.skyblock.core.menu;

import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.SlayerBoss;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class SlayerMenu extends AbstractSkyBlockMenu {

    // six consecutive slots across the centre row (one per slayer boss)
    static final int[] BOSS_SLOTS = {19, 20, 21, 22, 23, 24};

    private static final SlayerBoss[] DISPLAYED_BOSSES = {
            SlayerBoss.REVENANT_HORROR,
            SlayerBoss.TARANTULA_BROODFATHER,
            SlayerBoss.SVEN_PACKMASTER,
            SlayerBoss.VOIDGLOOM_SERAPH,
            SlayerBoss.INFERNO_DEMONLORD,
            SlayerBoss.RIFTSTALKER_BLOODFIEND
    };

    private static final Map<SlayerType, Material> HEAD_ICONS = new EnumMap<>(SlayerType.class);

    static {
        HEAD_ICONS.put(SlayerType.ZOMBIE,   Material.ROTTEN_FLESH);
        HEAD_ICONS.put(SlayerType.SPIDER,   Material.SPIDER_EYE);
        HEAD_ICONS.put(SlayerType.WOLF,     Material.BONE);
        HEAD_ICONS.put(SlayerType.ENDERMAN, Material.ENDER_PEARL);
        HEAD_ICONS.put(SlayerType.BLAZE,    Material.BLAZE_POWDER);
        HEAD_ICONS.put(SlayerType.VAMPIRE,  Material.REDSTONE);
    }

    public SlayerMenu(Player player) {
        super(player, "§4Slayer", 6);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();

        SkyblockUtils.fillBorder(getRows(), this::setItem, Material.RED_STAINED_GLASS_PANE);

        SlayerManager manager = SlayerManager.getInstance();

        for (int i = 0; i < DISPLAYED_BOSSES.length && i < BOSS_SLOTS.length; i++) {
            SlayerBoss boss = DISPLAYED_BOSSES[i];
            SlayerType type = boss.type;
            int level = manager.getLevel(playerId, type);
            long xp = manager.getExperience(playerId, type);
            int kills = manager.getKillCount(playerId, type);
            int[] data = SlayerManager.SLAYER_BOSS_DATA.get(type.name());
            int maxLevel = data != null ? data[0] : SlayerManager.MAX_LEVEL;

            setItem(BOSS_SLOTS[i], new ItemBuilder(HEAD_ICONS.get(type))
                    .displayName("§c" + boss.getDisplayName())
                    .lore("§7Type: §e" + type.getDisplayName(),
                            "§7Level: §e" + level + "§7/§e" + maxLevel,
                            "§7XP: §e" + xp,
                            "§7Bosses slain: §e" + kills)
                    .build());
        }

    }
}
