package com.skyblock.core.mob;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

/**
 * Singleton manager for SkyBlock mob loot tables.
 *
 * <p>Holds an {@link EnumMap} from {@link EntityType} to a list of
 * {@link LootEntry} records covering 12 mob types. Call
 * {@link #rollLoot(EntityType)} to get the items a player should receive
 * when they kill a mob of that type.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MobLootManager {

    /** A single weighted loot entry: material, quantity, and drop chance (0–1). */
    public static final class LootEntry {
        private final Material material;
        private final int amount;
        private final double chance;

        public LootEntry(Material material, int amount, double chance) {
            this.material = material;
            this.amount   = amount;
            this.chance   = chance;
        }

        public Material getMaterial() { return material; }
        public int      getAmount()   { return amount; }
        public double   getChance()   { return chance; }
    }

    private static final MobLootManager INSTANCE = new MobLootManager();

    private final EnumMap<EntityType, List<LootEntry>> lootTable =
            new EnumMap<>(EntityType.class);

    private final Random random = new Random();

    private MobLootManager() {
        register(EntityType.ZOMBIE,
                entry(Material.ROTTEN_FLESH,       2, 1.00),
                entry(Material.IRON_INGOT,          1, 0.08),
                entry(Material.CARROT,              1, 0.05));

        register(EntityType.SKELETON,
                entry(Material.BONE,               2, 1.00),
                entry(Material.ARROW,              4, 1.00),
                entry(Material.BOW,                1, 0.05));

        register(EntityType.SPIDER,
                entry(Material.STRING,             2, 1.00),
                entry(Material.SPIDER_EYE,         1, 0.33),
                entry(Material.COBWEB,             1, 0.10));

        register(EntityType.CREEPER,
                entry(Material.GUNPOWDER,          2, 1.00),
                entry(Material.TNT,                1, 0.05));

        register(EntityType.BLAZE,
                entry(Material.BLAZE_ROD,          1, 0.50),
                entry(Material.BLAZE_POWDER,       2, 1.00),
                entry(Material.MAGMA_CREAM,        1, 0.15));

        register(EntityType.ENDERMAN,
                entry(Material.ENDER_PEARL,        1, 0.50),
                entry(Material.OBSIDIAN,           1, 0.05));

        register(EntityType.WITCH,
                entry(Material.GLOWSTONE_DUST,     2, 0.85),
                entry(Material.REDSTONE,           2, 0.85),
                entry(Material.SUGAR,              2, 0.85),
                entry(Material.GLASS_BOTTLE,       2, 0.85));

        register(EntityType.SLIME,
                entry(Material.SLIME_BALL,         2, 1.00));

        register(EntityType.MAGMA_CUBE,
                entry(Material.MAGMA_CREAM,        1, 0.25),
                entry(Material.BLAZE_POWDER,       1, 0.10));

        register(EntityType.GHAST,
                entry(Material.GHAST_TEAR,         1, 0.30),
                entry(Material.GUNPOWDER,          2, 1.00));

        register(EntityType.WITHER_SKELETON,
                entry(Material.COAL,               1, 1.00),
                entry(Material.BONE,               2, 0.80),
                entry(Material.WITHER_SKELETON_SKULL, 1, 0.025));

        register(EntityType.ZOMBIFIED_PIGLIN,
                entry(Material.ROTTEN_FLESH,       1, 1.00),
                entry(Material.GOLD_NUGGET,        2, 1.00),
                entry(Material.GOLD_INGOT,         1, 0.025));
    }

    /**
     * Returns the single shared {@code MobLootManager} instance.
     *
     * @return the singleton instance
     */
    public static MobLootManager getInstance() {
        return INSTANCE;
    }

    /**
     * Rolls the loot table for the given entity type.
     *
     * <p>Each {@link LootEntry} is checked independently; entries whose
     * {@code chance} roll succeeds are included in the result. Returns an
     * empty list if the entity type has no custom loot defined.</p>
     *
     * @param entityType the type of mob that was killed
     * @return mutable list of {@link ItemStack}s to grant the killer
     */
    public List<ItemStack> rollLoot(EntityType entityType) {
        List<LootEntry> entries = lootTable.get(entityType);
        if (entries == null) {
            return Collections.emptyList();
        }
        List<ItemStack> drops = new ArrayList<>();
        for (LootEntry entry : entries) {
            if (random.nextDouble() < entry.chance) {
                drops.add(new ItemStack(entry.material, entry.amount));
            }
        }
        return drops;
    }

    /**
     * Returns an unmodifiable view of the loot entries for the given entity type,
     * or {@code null} if no entries are registered.
     *
     * @param entityType the entity type to look up
     * @return unmodifiable entry list, or {@code null}
     */
    public List<LootEntry> getEntries(EntityType entityType) {
        List<LootEntry> entries = lootTable.get(entityType);
        return entries == null ? null : Collections.unmodifiableList(entries);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static LootEntry entry(Material material, int amount, double chance) {
        return new LootEntry(material, amount, chance);
    }

    @SafeVarargs
    private final void register(EntityType type, LootEntry... entries) {
        List<LootEntry> list = new ArrayList<>(entries.length);
        for (LootEntry e : entries) {
            list.add(e);
        }
        lootTable.put(type, list);
    }
}
