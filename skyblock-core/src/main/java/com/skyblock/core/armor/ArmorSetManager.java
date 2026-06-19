package com.skyblock.core.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock named armor sets.
 *
 * <p>Defines twenty full-armor sets identified by item display name.
 * Call {@link #refresh(Player)} whenever a player's armor changes; it reads
 * the display names of all four slots, matches them against the known sets,
 * and caches the result. Use {@link #getActiveSet(UUID)} to query the result.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ArmorSetManager {

    /** A named SkyBlock armor set, its full-set {@link ArmorSetBonus}, and piece display names. */
    public enum ArmorSet {
        HARDENED_DIAMOND(
                "Hardened Diamond",
                new ArmorSetBonus("All pieces grant extra Defense", 100, 0, 0, 0),
                "Hardened Diamond Helmet", "Hardened Diamond Chestplate",
                "Hardened Diamond Leggings", "Hardened Diamond Boots"),
        PERFECT(
                "Perfect",
                new ArmorSetBonus("+1 Defense per 1 Defense on each piece", 200, 100, 0, 0),
                "Perfect Helmet", "Perfect Chestplate",
                "Perfect Leggings", "Perfect Boots"),
        SUPERIOR_DRAGON(
                "Superior Dragon",
                new ArmorSetBonus("+5% bonus to all stats", 100, 200, 100, 5),
                "Superior Dragon Helmet", "Superior Dragon Chestplate",
                "Superior Dragon Leggings", "Superior Dragon Boots"),
        STRONG_DRAGON(
                "Strong Dragon",
                new ArmorSetBonus("+75 Strength and +75 Crit Damage", 0, 0, 75, 0),
                "Strong Dragon Helmet", "Strong Dragon Chestplate",
                "Strong Dragon Leggings", "Strong Dragon Boots"),
        UNSTABLE_DRAGON(
                "Unstable Dragon",
                new ArmorSetBonus("+100 Crit Damage", 0, 0, 0, 0),
                "Unstable Dragon Helmet", "Unstable Dragon Chestplate",
                "Unstable Dragon Leggings", "Unstable Dragon Boots"),
        OLD_DRAGON(
                "Old Dragon",
                new ArmorSetBonus("+5 HP per armor piece worn", 0, 50, 0, 0),
                "Old Dragon Helmet", "Old Dragon Chestplate",
                "Old Dragon Leggings", "Old Dragon Boots"),
        WISE_DRAGON(
                "Wise Dragon",
                new ArmorSetBonus("+100 Magic Find", 0, 0, 0, 0),
                "Wise Dragon Helmet", "Wise Dragon Chestplate",
                "Wise Dragon Leggings", "Wise Dragon Boots"),
        YOUNG_DRAGON(
                "Young Dragon",
                new ArmorSetBonus("+70 Speed", 0, 0, 0, 70),
                "Young Dragon Helmet", "Young Dragon Chestplate",
                "Young Dragon Leggings", "Young Dragon Boots"),
        PROTECTOR_DRAGON(
                "Protector Dragon",
                new ArmorSetBonus("+1% Defense per 2,000 max HP", 150, 0, 0, 0),
                "Protector Dragon Helmet", "Protector Dragon Chestplate",
                "Protector Dragon Leggings", "Protector Dragon Boots"),
        HOLY_DRAGON(
                "Holy Dragon",
                new ArmorSetBonus("+5% HP and +1% Magic Find per piece", 0, 100, 0, 0),
                "Holy Dragon Helmet", "Holy Dragon Chestplate",
                "Holy Dragon Leggings", "Holy Dragon Boots"),
        FAIRY(
                "Fairy",
                new ArmorSetBonus("+5 HP per Fairy Soul collected", 0, 50, 0, 5),
                "Fairy Helmet", "Fairy Chestplate",
                "Fairy Leggings", "Fairy Boots"),
        NECRON(
                "Necron",
                new ArmorSetBonus("+3% damage dealt to all mobs", 100, 50, 50, 0),
                "Necron's Helmet", "Necron's Chestplate",
                "Necron's Leggings", "Necron's Boots"),
        MAXOR(
                "Maxor",
                new ArmorSetBonus("+25% Speed to Strength conversion", 0, 0, 50, 0),
                "Maxor's Helmet", "Maxor's Chestplate",
                "Maxor's Leggings", "Maxor's Boots"),
        STORM(
                "Storm",
                new ArmorSetBonus("+30% Strength while your HP is above 50%", 0, 0, 30, 0),
                "Storm's Helmet", "Storm's Chestplate",
                "Storm's Leggings", "Storm's Boots"),
        GOLDOR(
                "Goldor",
                new ArmorSetBonus("+100 Defense while in the Dungeons", 200, 0, 0, 0),
                "Goldor's Helmet", "Goldor's Chestplate",
                "Goldor's Leggings", "Goldor's Boots"),
        TARANTULA(
                "Tarantula",
                new ArmorSetBonus("+25% Crit Damage", 0, 0, 0, 0),
                "Tarantula Helmet", "Tarantula Chestplate",
                "Tarantula Leggings", "Tarantula Boots"),
        MASTIFF(
                "Mastiff",
                new ArmorSetBonus("+50 HP per Dungeon Star on each piece", 0, 200, 0, 0),
                "Mastiff Helmet", "Mastiff Chestplate",
                "Mastiff Leggings", "Mastiff Boots"),
        ZOMBIE_SOLDIER(
                "Zombie Soldier",
                new ArmorSetBonus("+2 Defense per Revenant Horror kill", 50, 0, 0, 0),
                "Zombie Soldier Helmet", "Zombie Soldier Chestplate",
                "Zombie Soldier Leggings", "Zombie Soldier Boots"),
        REVENANT(
                "Revenant",
                new ArmorSetBonus("+5% damage against Undead mobs", 75, 25, 0, 0),
                "Revenant Horror Helmet", "Revenant Horror Chestplate",
                "Revenant Horror Leggings", "Revenant Horror Boots"),
        MINERAL(
                "Mineral",
                new ArmorSetBonus("+1 Defense per 1,000 ore mined", 100, 0, 0, 0),
                "Mineral Helmet", "Mineral Chestplate",
                "Mineral Leggings", "Mineral Boots");

        private final String displayName;
        private final ArmorSetBonus bonus;
        // Bukkit armor order: 3=helmet, 2=chestplate, 1=leggings, 0=boots
        private final String helmetName;
        private final String chestplateName;
        private final String leggingsName;
        private final String bootsName;

        ArmorSet(String displayName, ArmorSetBonus bonus,
                 String helmetName, String chestplateName,
                 String leggingsName, String bootsName) {
            this.displayName    = displayName;
            this.bonus          = bonus;
            this.helmetName     = helmetName;
            this.chestplateName = chestplateName;
            this.leggingsName   = leggingsName;
            this.bootsName      = bootsName;
        }

        public String getDisplayName() { return displayName; }
        public ArmorSetBonus getBonus() { return bonus; }

        /** Returns {@code true} if the four stripped display names match this set. */
        boolean matches(String helmet, String chest, String legs, String boots) {
            return helmetName.equals(helmet)
                    && chestplateName.equals(chest)
                    && leggingsName.equals(legs)
                    && bootsName.equals(boots);
        }

        public static ArmorSet fromName(String name) {
            for (ArmorSet s : values()) {
                if (s.displayName.equalsIgnoreCase(name) || s.name().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            return null;
        }
    }

    private static final ArmorSetManager INSTANCE = new ArmorSetManager();

    /** Per-player currently active set (null when no named set is worn). */
    private final Map<UUID, ArmorSet> activeSets = new HashMap<>();

    private ArmorSetManager() {}

    public static ArmorSetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads the player's current armor display names, matches them against
     * known sets, and caches the result. Call whenever armor slots change.
     *
     * @param player the player whose armor changed
     * @return the now-active {@link ArmorSet}, or {@code null} if none
     */
    public ArmorSet refresh(Player player) {
        Objects.requireNonNull(player, "player");
        ItemStack[] armor = player.getInventory().getArmorContents();
        // Bukkit armor array: 0=boots, 1=leggings, 2=chestplate, 3=helmet
        String helmet     = displayName(armor[3]);
        String chestplate = displayName(armor[2]);
        String leggings   = displayName(armor[1]);
        String boots      = displayName(armor[0]);

        ArmorSet active = null;
        for (ArmorSet set : ArmorSet.values()) {
            if (set.matches(helmet, chestplate, leggings, boots)) {
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
     * Returns the active {@link ArmorSet} for the given player, or {@code null} if none.
     *
     * @param playerId the player's UUID
     * @return active set or {@code null}
     */
    public ArmorSet getActiveSet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeSets.get(playerId);
    }

    /** Removes any cached set state for the player (call on quit). */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeSets.remove(playerId);
    }

    /** Returns an unmodifiable view of all active sets, keyed by player UUID. */
    public Map<UUID, ArmorSet> getActiveSets() {
        return Collections.unmodifiableMap(activeSets);
    }

    /** Strips Minecraft color codes and returns the item display name, or "" if none. */
    private static String displayName(ItemStack item) {
        if (item == null) return "";
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return "";
        return meta.getDisplayName().replaceAll("§[0-9a-fk-or]", "");
    }
}
