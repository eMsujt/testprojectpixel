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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SlayerMenu extends AbstractSkyBlockMenu {

    // six boss slots across row 2, matching Hypixel's Slayer GUI.
    static final int[] BOSS_SLOTS = {10, 11, 12, 13, 14, 15};
    // five tier slots for the per-boss tier selector
    private static final int[] TIER_SLOTS = {20, 21, 22, 23, 24};

    // Wiki order (slots 10-15): the four core slayers, the Rift slayer, then Blaze last.
    private static final SlayerBoss[] DISPLAYED_BOSSES = {
            SlayerBoss.REVENANT_HORROR,
            SlayerBoss.TARANTULA_BROODFATHER,
            SlayerBoss.SVEN_PACKMASTER,
            SlayerBoss.VOIDGLOOM_SERAPH,
            SlayerBoss.RIFTSTALKER_BLOODFIEND,
            SlayerBoss.INFERNO_DEMONLORD
    };

    /** Verbatim wiki display name (skull glyph + boss-name colour) per slayer type. */
    private static final Map<SlayerType, String> BOSS_TITLES = new EnumMap<>(SlayerType.class);
    /** Verbatim wiki flavour description per slayer type. */
    private static final Map<SlayerType, String[]> BOSS_DESC = new EnumMap<>(SlayerType.class);

    static {
        BOSS_TITLES.put(SlayerType.ZOMBIE,   "§c☠ §eRevenant Horror");
        BOSS_TITLES.put(SlayerType.SPIDER,   "§c☠ §cTarantula Broodfather");
        BOSS_TITLES.put(SlayerType.WOLF,     "§c☠ §cSven Packmaster");
        BOSS_TITLES.put(SlayerType.ENDERMAN, "§c☠ §cVoidgloom Seraph");
        BOSS_TITLES.put(SlayerType.VAMPIRE,  "§c☠ §cRiftstalker Bloodfiend");
        BOSS_TITLES.put(SlayerType.BLAZE,    "§c☠ §cInferno Demonlord");

        BOSS_DESC.put(SlayerType.ZOMBIE, new String[]{
                "§7Abhorrent Zombie stuck", "§7between life and death for", "§7an eternity."});
        BOSS_DESC.put(SlayerType.SPIDER, new String[]{
                "§7Monstrous Spider who poisons", "§7and devours its victims."});
        BOSS_DESC.put(SlayerType.WOLF, new String[]{
                "§7Rabid Wolf genetically", "§7modified by a famous mad", "§7scientist. Eats bones and", "§7flesh."});
        BOSS_DESC.put(SlayerType.ENDERMAN, new String[]{
                "§7If Necron is the right-hand", "§7of the Wither King, this",
                "§7dark demigod is the", "§7left-hand."});
        BOSS_DESC.put(SlayerType.VAMPIRE, new String[]{
                "§7A half-vampire, half-thrall,", "§7immortal golem creation",
                "§7representing the multiverse", "§7coven in combat."});
        BOSS_DESC.put(SlayerType.BLAZE, new String[]{
                "§7Even demons fear this", "§7incarnation of pure evil,",
                "§7constantly feeding on its", "§7burning desire for", "§7destruction."});
    }

    private static final QuestTier[] TIERS = {
            QuestTier.TIER_1, QuestTier.TIER_2, QuestTier.TIER_3, QuestTier.TIER_4, QuestTier.TIER_5
    };

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V"};

    private static final Map<SlayerType, Material> HEAD_ICONS = new EnumMap<>(SlayerType.class);

    static {
        HEAD_ICONS.put(SlayerType.ZOMBIE,   Material.ROTTEN_FLESH);
        HEAD_ICONS.put(SlayerType.SPIDER,   Material.COBWEB);
        HEAD_ICONS.put(SlayerType.WOLF,     Material.MUTTON);
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
        super(player, selected == null ? "Slayer" : bossName(selected), 6);
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
                    .displayName("§aActive Quest: §c" + bossName(active.type)
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
            int kills = manager.getKillCount(playerId, type);

            List<String> lore = new ArrayList<>();
            for (String line : BOSS_DESC.getOrDefault(type, new String[0])) lore.add(line);
            lore.add("");
            lore.add("§7Slayer Level: §e" + level + "  §7Kills: §e" + kills);
            lore.add("");
            lore.add(active == null ? "§eClick to view boss!" : "§cFinish your active quest first.");

            setItem(BOSS_SLOTS[i], new ItemBuilder(HEAD_ICONS.get(type))
                    .displayName(BOSS_TITLES.getOrDefault(type, "§c" + boss.getDisplayName()))
                    .lore(lore.toArray(new String[0]))
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

        // Info buttons along row 4, matching Hypixel's Slayer GUI.
        setItem(28, new ItemBuilder(Material.GRAY_DYE)
                .displayName("§bAuto-Slayer")
                .lore("§7Upon defeating a boss,",
                      "§aautomatically §7completes the",
                      "§7quest and starts another of the",
                      "§7same type if you have enough",
                      "§6coins §7in your purse or bank.")
                .build(), e -> e.setCancelled(true));

        setItem(29, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .displayName("§bSlayer Leaderboards")
                .lore("§8Ultimate bragging rights",
                      "",
                      "§7Revenant Horror: §8N/A",
                      "§7Tarantula Broodfather: §8N/A",
                      "§7Sven Packmaster: §8N/A",
                      "§7Voidgloom Seraph: §8N/A",
                      "§7Riftstalker Bloodfiend: §8N/A",
                      "§7Inferno Demonlord: §8N/A")
                .build(), e -> e.setCancelled(true));

        setItem(32, new ItemBuilder(Material.WHEAT)
                .displayName("§aGlobal Combat Wisdom §7Buff")
                .lore("§8Slayer Bonus",
                      "",
                      "§7Earn extra Combat EXP based on",
                      "§7your unique slayer boss kills.",
                      "",
                      "§7Tier I, II, III grant §3+1 ☯ Combat Wisdom§7.",
                      "§7Tier IV grants §3+2 ☯ Combat Wisdom§7.")
                .build(), e -> e.setCancelled(true));

        setItem(33, new ItemBuilder(Material.POWERED_RAIL)
                .displayName("§aSlayer Bonus Rewards")
                .lore("§7Unlock bonuses by reaching a",
                      "§7LVL on the first 3 bosses.",
                      "",
                      "§c✖ LVL 6",
                      "§7Earn §a+3 §7of any boss's main",
                      "§7token drop when slaying its",
                      "§7mini-bosses.",
                      "",
                      "§c✖ LVL 7",
                      "§7Slayers are §64% cheaper§7.")
                .build(), e -> e.setCancelled(true));

        setItem(35, new ItemBuilder(Material.PAINTING)
                .displayName("§dRNG Meter")
                .lore("§7Your §dRNG Meter §7fills with",
                      "§dSlayer XP §7every time you",
                      "§7defeat a §aTier III §7or higher!",
                      "",
                      "§7When your meter is full, your",
                      "§7selected drop will be guaranteed",
                      "§7to drop the next time you defeat",
                      "§7the boss!")
                .build(), e -> e.setCancelled(true));

        setItem(49, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> { e.setCancelled(true); player.closeInventory(); });
    }

    private void buildTierSelector() {
        UUID playerId = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        setItem(4, new ItemBuilder(HEAD_ICONS.get(selected))
                .displayName("§c" + bossName(selected))
                .lore("§7Choose a tier to begin.",
                      "§7Higher tiers cost more and drop better loot.")
                .build(), e -> e.setCancelled(true));

        for (int i = 0; i < TIERS.length && i < TIER_SLOTS.length; i++) {
            QuestTier tier = TIERS[i];
            int cost = manager.getSpawnCost(selected, tier);
            int target = SlayerManager.KILLS_TO_SPAWN_BOSS.getOrDefault(tier, 0);
            setItem(TIER_SLOTS[i], new ItemBuilder(Material.RED_DYE)
                    .displayName("§c" + bossName(selected) + " §7- Tier " + ROMAN[i])
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
        player.sendMessage("§aStarted a §c" + bossName(selected) + " §7Tier " + tierRoman(tier)
                + " §aslayer quest! Kill " + selected.getDisplayName() + " mobs to spawn the boss.");
        new SlayerMenu(player).open(player);
    }

    private static String tierRoman(QuestTier tier) {
        return ROMAN[tier.ordinal()];
    }

    /** The boss's display name (e.g. "Revenant Horror") for a slayer type, else the type name. */
    private static String bossName(SlayerType type) {
        SlayerBoss boss = SlayerBoss.forType(type);
        return boss != null ? boss.getDisplayName() : type.getDisplayName();
    }
}
