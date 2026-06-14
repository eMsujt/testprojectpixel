package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Awards skill XP through {@link SkillManager} whenever a player breaks a block,
 * reels in a fish or kills a mob, mapping the broken {@link Material} or killed
 * {@link EntityType} to its governing {@link SkillType} (Farming, Mining, Foraging,
 * Fishing or Combat) and firing level-up rewards when the player's level increases.
 *
 * <p>Farming crops only count once mature: {@link Ageable} crops must have reached
 * their maximum age, while non-ageable produce (pumpkins, melons, sugar cane, …)
 * always counts.</p>
 *
 * <p>The per-action XP tables are loaded from the bundled {@code skills-xp.yml}
 * resource (read straight from the jar); when that resource is missing or a section
 * is unreadable the listener falls back to its built-in {@code DEFAULT_*} tables.</p>
 */
public final class SkillProgressionListener implements Listener {

    /** Built-in fallback Farming XP per crop block, keyed by {@link Material}. */
    private static final Map<Material, Long> DEFAULT_FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,                3L),
            Map.entry(Material.POTATOES,             3L),
            Map.entry(Material.CARROTS,              3L),
            Map.entry(Material.BEETROOTS,            3L),
            Map.entry(Material.NETHER_WART,          5L),
            Map.entry(Material.PUMPKIN,              8L),
            Map.entry(Material.MELON,                2L),
            Map.entry(Material.SUGAR_CANE,           2L),
            Map.entry(Material.CACTUS,               2L),
            Map.entry(Material.COCOA,                3L),
            Map.entry(Material.MUSHROOM_STEM,        3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   3L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 3L)
    );

    /** Built-in fallback Mining XP per ore/stone block, keyed by {@link Material}. */
    private static final Map<Material, Long> DEFAULT_MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,             1L),
            Map.entry(Material.COBBLESTONE,       1L),
            Map.entry(Material.COAL_ORE,          5L),
            Map.entry(Material.IRON_ORE,          5L),
            Map.entry(Material.GOLD_ORE,         10L),
            Map.entry(Material.DIAMOND_ORE,      30L),
            Map.entry(Material.EMERALD_ORE,      30L),
            Map.entry(Material.LAPIS_ORE,        25L),
            Map.entry(Material.REDSTONE_ORE,      7L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 10L)
    );

    /** Built-in fallback Foraging XP per log broken, keyed by {@link Material}. */
    private static final Map<Material, Long> DEFAULT_FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L),
            Map.entry(Material.MANGROVE_LOG, 6L),
            Map.entry(Material.CHERRY_LOG,   6L)
    );

    /** Built-in fallback Combat XP per mob killed, keyed by {@link EntityType}. */
    private static final Map<EntityType, Long> DEFAULT_COMBAT_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,   5L),
            Map.entry(EntityType.SKELETON, 5L),
            Map.entry(EntityType.SPIDER,   5L),
            Map.entry(EntityType.CREEPER,  7L),
            Map.entry(EntityType.ENDERMAN, 12L)
    );

    /** Built-in fallback Fishing XP awarded per fish reeled in. */
    private static final long DEFAULT_FISHING_XP = 6L;

    /** Farming XP granted per crop block, keyed by {@link Material}. */
    private final Map<Material, Long> farmingXp;

    /** Mining XP granted per ore/stone block, keyed by {@link Material}. */
    private final Map<Material, Long> miningXp;

    /** Foraging XP granted per log broken, keyed by {@link Material}. */
    private final Map<Material, Long> foragingXp;

    /** Combat XP granted per mob killed, keyed by {@link EntityType}. */
    private final Map<EntityType, Long> combatXp;

    /** Fishing XP granted per fish reeled in. */
    private final long fishingXp;

    private final SkillManager skillManager = SkillManager.getInstance();

    /**
     * Loads the per-action XP tables from {@code skills-xp.yml}, falling back to the
     * built-in {@code DEFAULT_*} tables for any section that is absent or unreadable.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public SkillProgressionListener(JavaPlugin plugin) {
        YamlConfiguration cfg = loadConfig(plugin);
        this.farmingXp = loadSection(plugin, cfg, "farming", DEFAULT_FARMING_XP);
        this.miningXp = loadSection(plugin, cfg, "mining", DEFAULT_MINING_XP);
        this.foragingXp = loadSection(plugin, cfg, "foraging", DEFAULT_FORAGING_XP);
        this.combatXp = loadEntitySection(plugin, cfg, "combat", DEFAULT_COMBAT_XP);
        this.fishingXp = cfg != null ? cfg.getLong("fishing", DEFAULT_FISHING_XP) : DEFAULT_FISHING_XP;
    }

    private static YamlConfiguration loadConfig(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("skills-xp.yml");
        if (resource == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read skills-xp.yml: " + e.getMessage());
            return null;
        }
    }

    private static Map<Material, Long> loadSection(JavaPlugin plugin, YamlConfiguration cfg,
                                                   String name, Map<Material, Long> defaults) {
        ConfigurationSection section = cfg != null ? cfg.getConfigurationSection(name) : null;
        if (section == null) {
            return defaults;
        }
        Map<Material, Long> table = new EnumMap<>(Material.class);
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) {
                plugin.getLogger().warning("Unknown material in skills-xp.yml (" + name + "): " + key);
                continue;
            }
            table.put(material, section.getLong(key));
        }
        return table.isEmpty() ? defaults : table;
    }

    private static Map<EntityType, Long> loadEntitySection(JavaPlugin plugin, YamlConfiguration cfg,
                                                           String name, Map<EntityType, Long> defaults) {
        ConfigurationSection section = cfg != null ? cfg.getConfigurationSection(name) : null;
        if (section == null) {
            return defaults;
        }
        Map<EntityType, Long> table = new EnumMap<>(EntityType.class);
        for (String key : section.getKeys(false)) {
            EntityType entity;
            try {
                entity = EntityType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown entity in skills-xp.yml (" + name + "): " + key);
                continue;
            }
            table.put(entity, section.getLong(key));
        }
        return table.isEmpty() ? defaults : table;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        Long farming = farmingXp.get(type);
        if (farming != null) {
            if (isMature(block)) {
                grantXP(event.getPlayer(), SkillType.FARMING, farming);
            }
            return;
        }

        Long mining = miningXp.get(type);
        if (mining != null) {
            grantXP(event.getPlayer(), SkillType.MINING, mining);
            return;
        }

        Long foraging = foragingXp.get(type);
        if (foraging != null) {
            grantXP(event.getPlayer(), SkillType.FORAGING, foraging);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        Long combat = combatXp.get(event.getEntityType());
        if (combat != null) {
            grantXP(killer, SkillType.COMBAT, combat);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            grantXP(event.getPlayer(), SkillType.FISHING, fishingXp);
        }
    }

    /**
     * Returns whether the crop block has finished growing. {@link Ageable} crops
     * are mature only at their maximum age; all other produce is always mature.
     */
    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }

    private void grantXP(Player player, SkillType skill, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, skill);
        skillManager.addXP(id, skill, amount);
        int after = skillManager.getLevel(id, skill);
        String name = skill.name().charAt(0) + skill.name().substring(1).toLowerCase();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§3+" + amount + " " + name + " XP"));
        if (after > before) {
            skillManager.grantLevelUpRewards(id, skill, before, after);
            player.sendTitle("§aSkill Level Up!", "§e" + name + " §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
