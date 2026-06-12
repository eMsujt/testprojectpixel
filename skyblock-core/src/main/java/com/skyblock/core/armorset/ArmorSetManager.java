package com.skyblock.core.armorset;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock armor sets.
 *
 * <p>Defines six full-armor sets. When a player wears all four pieces of a set
 * the set bonus is considered active. Call {@link #getActiveSet(UUID)} to query
 * which set (if any) is currently active for a player, and
 * {@link #refresh(Player)} whenever a player's armor changes.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ArmorSetManager {

    /** The armor slots used to check for a complete set. */
    public enum ArmorSlot {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    /** A complete SkyBlock armor set and its stat bonuses. */
    public enum ArmorSet {
        LEATHER_SET(
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS,
                5.0, 0.0),
        IRON_SET(
                Material.IRON_HELMET,
                Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS,
                Material.IRON_BOOTS,
                15.0, 5.0),
        CHAINMAIL_SET(
                Material.CHAINMAIL_HELMET,
                Material.CHAINMAIL_CHESTPLATE,
                Material.CHAINMAIL_LEGGINGS,
                Material.CHAINMAIL_BOOTS,
                10.0, 8.0),
        GOLD_SET(
                Material.GOLDEN_HELMET,
                Material.GOLDEN_CHESTPLATE,
                Material.GOLDEN_LEGGINGS,
                Material.GOLDEN_BOOTS,
                20.0, 2.0),
        DIAMOND_SET(
                Material.DIAMOND_HELMET,
                Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS,
                Material.DIAMOND_BOOTS,
                40.0, 15.0),
        NETHERITE_SET(
                Material.NETHERITE_HELMET,
                Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS,
                Material.NETHERITE_BOOTS,
                60.0, 25.0);

        /** The four required pieces for this set. */
        final EnumMap<ArmorSlot, Material> pieces;

        /** Flat defense bonus granted when the full set is worn. */
        public final double defenseBonus;

        /** Flat health bonus granted when the full set is worn. */
        public final double healthBonus;

        ArmorSet(Material helmet, Material chest, Material legs, Material boots,
                 double defenseBonus, double healthBonus) {
            pieces = new EnumMap<>(ArmorSlot.class);
            pieces.put(ArmorSlot.HELMET,     helmet);
            pieces.put(ArmorSlot.CHESTPLATE, chest);
            pieces.put(ArmorSlot.LEGGINGS,   legs);
            pieces.put(ArmorSlot.BOOTS,      boots);
            this.defenseBonus = defenseBonus;
            this.healthBonus  = healthBonus;
        }

        /** Returns {@code true} if all four slots match the required materials. */
        public boolean matches(EnumMap<ArmorSlot, Material> worn) {
            for (Map.Entry<ArmorSlot, Material> entry : pieces.entrySet()) {
                if (entry.getValue() != worn.get(entry.getKey())) {
                    return false;
                }
            }
            return true;
        }
    }

    private static final ArmorSetManager INSTANCE = new ArmorSetManager();

    /** Per-player currently active set (null when no full set is worn). */
    private final Map<UUID, ArmorSet> activeSets = new HashMap<>();

    private ArmorSetManager() {
    }

    public static ArmorSetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads the player's current armor and updates the active set for them.
     * Call this whenever the player's armor inventory changes.
     *
     * @param player the player whose armor changed
     * @return the now-active {@link ArmorSet}, or {@code null} if none
     */
    public ArmorSet refresh(Player player) {
        Objects.requireNonNull(player, "player");
        EnumMap<ArmorSlot, Material> worn = getWornMaterials(player);
        ArmorSet active = null;
        for (ArmorSet set : ArmorSet.values()) {
            if (set.matches(worn)) {
                active = set;
                break;
            }
        }
        if (active != null) {
            activeSets.put(player.getUniqueId(), active);
        } else {
            activeSets.remove(player.getUniqueId());
        }
        return active;
    }

    /**
     * Returns the currently active {@link ArmorSet} for the player, or
     * {@code null} if they are not wearing a full matching set.
     *
     * @param playerId the player's UUID
     * @return active set or {@code null}
     */
    public ArmorSet getActiveSet(UUID playerId) {
        return activeSets.get(playerId);
    }

    /** Removes any cached set state for the player (call on quit). */
    public void remove(UUID playerId) {
        activeSets.remove(playerId);
    }

    /** Returns an unmodifiable view of all active sets, keyed by player UUID. */
    public Map<UUID, ArmorSet> getActiveSets() {
        return Collections.unmodifiableMap(activeSets);
    }

    private static EnumMap<ArmorSlot, Material> getWornMaterials(Player player) {
        EnumMap<ArmorSlot, Material> map = new EnumMap<>(ArmorSlot.class);
        org.bukkit.inventory.ItemStack[] armor = player.getInventory().getArmorContents();
        // Bukkit armor order: 0=boots, 1=leggings, 2=chestplate, 3=helmet
        map.put(ArmorSlot.BOOTS,       armor[0] != null ? armor[0].getType() : Material.AIR);
        map.put(ArmorSlot.LEGGINGS,    armor[1] != null ? armor[1].getType() : Material.AIR);
        map.put(ArmorSlot.CHESTPLATE,  armor[2] != null ? armor[2].getType() : Material.AIR);
        map.put(ArmorSlot.HELMET,      armor[3] != null ? armor[3].getType() : Material.AIR);
        return map;
    }
}
