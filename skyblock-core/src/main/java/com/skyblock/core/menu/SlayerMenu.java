package com.skyblock.core.menu;

import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.QuestTier;
import com.skyblock.core.manager.SlayerManager.SlayerBoss;
import com.skyblock.core.manager.SlayerManager.SlayerQuest;
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
    // five tier slots for the per-boss tier selector
    private static final int[] TIER_SLOTS = {20, 21, 22, 23, 24};

    // Canonical order: the five core slayers, then the Rift slayer (Riftstalker) last.
    private static final SlayerBoss[] DISPLAYED_BOSSES = {
            SlayerBoss.REVENANT_HORROR,
            SlayerBoss.TARANTULA_BROODFATHER,
            SlayerBoss.SVEN_PACKMASTER,
            SlayerBoss.VOIDGLOOM_SERAPH,
            SlayerBoss.INFERNO_DEMONLORD,
            SlayerBoss.RIFTSTALKER_BLOODFIEND
    };

    private static final QuestTier[] TIERS = {
            QuestTier.TIER_1, QuestTier.TIER_2, QuestTier.TIER_3, QuestTier.TIER_4, QuestTier.TIER_5
    };

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V"};

    private static final Map<SlayerType, Material> HEAD_ICONS = new EnumMap<>(SlayerType.class);

    static {
        HEAD_ICONS.put(SlayerType.ZOMBIE,   Material.ROTTEN_FLESH);
        HEAD_ICONS.put(SlayerType.SPIDER,   Material.SPIDER_EYE);
        HEAD_ICONS.put(SlayerType.WOLF,     Material.BONE);
        HEAD_ICONS.put(SlayerType.ENDERMAN, Material.ENDER_PEARL);
        HEAD_ICONS.put(SlayerType.BLAZE,    Material.BLAZE_POWDER);
        HEAD_ICONS.put(SlayerType.VAMPIRE,  Material.REDSTONE);
    }

    /** Non-null = the tier selector for that boss; null = the boss list. */
    private final SlayerType selected;

    public SlayerMenu(Player player) {
        this(player, null);
    }

    private SlayerMenu(Player player, SlayerType selected) {
        super(player, selected == null ? "§4Slayer" : "§4Slayer §8» §7" + selected.getDisplayName(), 6);
        this.selected = selected;
    }

    @Override
    protected void populate() {
        SkyblockUtils.fillBorder(getRows(), this::setItem, Material.BLACK_STAINED_GLASS_PANE);
        if (selected == null) {
            buildBossList();
        } else {
            buildTierSelector();
        }
    }

    private void buildBossList() {
        UUID playerId = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        SlayerQuest active = manager.getActiveQuest(playerId);

        // Active-quest panel (slot 4) with progress + a cancel option.
        if (active != null) {
            int target = SlayerManager.KILLS_TO_SPAWN_BOSS.getOrDefault(active.tier, 0);
            setItem(4, new ItemBuilder(Material.DIAMOND_SWORD)
                    .displayName("§aActive Quest: §c" + active.type.getDisplayName()
                            + " §7Tier " + tierRoman(active.tier))
                    .lore(active.isBossSpawned()
                                    ? "§7Boss spawned — §cslay it!"
                                    : "§7Combat to spawn the boss: §e" + active.getKills() + "§7/§e" + target,
                            "",
                            "§eRight-click to cancel this quest.")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (e.isRightClick()) {
                            manager.cancelQuest(playerId);
                            player.sendMessage("§cSlayer quest cancelled.");
                            new SlayerMenu(player).open(player);
                        }
                    });
        }

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
                            "§7Bosses slain: §e" + kills,
                            "",
                            active == null ? "§eClick to start a quest!"
                                    : "§cFinish your active quest first.")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (manager.getActiveQuest(playerId) != null) {
                            player.sendMessage("§cYou already have an active slayer quest.");
                            return;
                        }
                        new SlayerMenu(player, type).open(player);
                    });
        }

        setItem(49, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> { e.setCancelled(true); player.closeInventory(); });
    }

    private void buildTierSelector() {
        UUID playerId = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        SlayerBoss boss = SlayerBoss.forType(selected);

        setItem(4, new ItemBuilder(HEAD_ICONS.get(selected))
                .displayName("§c" + (boss != null ? boss.getDisplayName() : selected.getDisplayName()))
                .lore("§7Choose a tier to begin.",
                      "§7Higher tiers cost more and drop better loot.")
                .build(), e -> e.setCancelled(true));

        for (int i = 0; i < TIERS.length && i < TIER_SLOTS.length; i++) {
            QuestTier tier = TIERS[i];
            int cost = manager.getSpawnCost(selected, tier);
            int target = SlayerManager.KILLS_TO_SPAWN_BOSS.getOrDefault(tier, 0);
            setItem(TIER_SLOTS[i], new ItemBuilder(Material.RED_DYE)
                    .displayName("§c" + selected.getDisplayName() + " §7- Tier " + ROMAN[i])
                    .lore("§7Combat XP to spawn boss: §e" + target,
                            "§7Cost: §6" + String.format("%,d", cost) + " coins",
                            "",
                            "§eClick to start!")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        startQuest(tier, cost);
                    });
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To the slayer list")
                .build(), e -> { e.setCancelled(true); new SlayerMenu(player).open(player); });
    }

    private void startQuest(QuestTier tier, int cost) {
        UUID playerId = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        if (manager.getActiveQuest(playerId) != null) {
            player.sendMessage("§cYou already have an active slayer quest.");
            return;
        }
        if (cost > 0 && !EconomyManager.getInstance().withdraw(playerId, (long) cost)) {
            player.sendMessage("§cYou can't afford this quest (§6" + String.format("%,d", cost) + " coins§c).");
            return;
        }
        manager.startQuest(playerId, selected, tier);
        player.sendMessage("§aStarted a §c" + selected.getDisplayName() + " §7Tier " + tierRoman(tier)
                + " §aslayer quest! Kill " + selected.getDisplayName() + " mobs to spawn the boss.");
        new SlayerMenu(player).open(player);
    }

    private static String tierRoman(QuestTier tier) {
        return ROMAN[tier.ordinal()];
    }
}
