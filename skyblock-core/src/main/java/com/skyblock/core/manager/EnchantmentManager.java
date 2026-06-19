package com.skyblock.core.manager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock enchantment tracking.
 *
 * <p>Delegates all state to {@link EnchantingManager}. Prefer
 * {@link SkyBlockEnchant} over the internal {@link EnchantingManager.SkyBlockEnchantment}
 * when calling through this class.</p>
 */
public final class EnchantmentManager {

    /** Every SkyBlock enchant with its maximum level. */
    public enum SkyBlockEnchant {
        // Combat
        SHARPNESS(7),
        CRITICAL(7),
        SMITE(7),
        BANE_OF_ARTHROPODS(7),
        FIRST_STRIKE(4),
        GIANT_KILLER(7),
        ENDER_SLAYER(7),
        DRAGON_HUNTER(5),
        THUNDERLORD(7),
        VAMPIRISM(6),
        LIFE_STEAL(5),
        LETHALITY(6),
        EXECUTE(5),
        PROSECUTE(5),
        OVERLOAD(5),
        // Utility / Special
        TELEKINESIS(1),
        LOOTING(4),
        SMELTING_TOUCH(1),
        MAGNET(1),
        SILK_TOUCH(1),
        // Fishing
        LUCK_OF_THE_SEA(7),
        ANGLER(6),
        FRAIL(5),
        EXPERTISE(10),
        // Farming
        CULTIVATING(10),
        GREEN_THUMB(5),
        DEDICATION(4),
        REPLENISH(1),
        HARVESTING(6),
        TURBO_WHEAT(5),
        TURBO_COCO(5),
        TURBO_CACTUS(5),
        TURBO_MELON(5),
        TURBO_PUMPKIN(5),
        TURBO_WARTS(5),
        TURBO_MUSHROOMS(5),
        TURBO_POTATO(5),
        TURBO_CARROT(5),
        TURBO_SUGAR_CANE(5),
        // Mining / Tool
        EFFICIENCY(5),
        FORTUNE(4),
        // Armor
        PROTECTION(7),
        THORNS(3),
        GROWTH(7),
        FEATHER_FALLING(7),
        SUGAR_RUSH(3),
        REJUVENATE(5),
        // Misc
        LUCK(7),
        CHANCE(5),
        ULTIMATE_WISE(5),
        // Dungeon / Extra
        SHREDDER(5),
        SCAVENGER(4),
        SOUL_EATER(5),
        VENOMOUS(5),
        VICIOUS(5);

        private final int maxLevel;

        SkyBlockEnchant(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();
    private final EnchantingManager delegate = EnchantingManager.getInstance();

    private EnchantmentManager() {}

    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId, EnchantingManager.SkyBlockEnchantment type) {
        return delegate.getLevel(playerId, type);
    }

    public void setEnchantment(UUID playerId, EnchantingManager.SkyBlockEnchantment type, int level) {
        delegate.setEnchantment(playerId, type, level);
    }

    public boolean removeEnchantment(UUID playerId, EnchantingManager.SkyBlockEnchantment type) {
        return delegate.removeEnchantment(playerId, type);
    }

    public Map<EnchantingManager.SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        return delegate.getEnchantments(playerId);
    }

    public int getMaxLevel(EnchantingManager.SkyBlockEnchantment type) {
        return delegate.getMaxLevel(type);
    }

    public Map<EnchantingManager.SkyBlockEnchantment, Integer> getEnchantTable() {
        return delegate.getEnchantTable();
    }

    public int getRequiredBookshelfPower(EnchantingManager.SkyBlockEnchantment type) {
        return delegate.getRequiredBookshelfPower(type);
    }

    public boolean isUltimate(EnchantingManager.SkyBlockEnchantment type) {
        return delegate.isUltimate(type);
    }

    public Set<EnchantingManager.SkyBlockEnchantment> getConflicts(EnchantingManager.SkyBlockEnchantment type) {
        return delegate.getConflicts(type);
    }

    public int getEnchantCost(EnchantingManager.SkyBlockEnchantment type, int level) {
        return delegate.getEnchantCost(type, level);
    }

    public boolean remove(UUID playerId) {
        return delegate.remove(playerId);
    }

    public List<String> getEnchantingHistory(UUID playerId) {
        return delegate.getEnchantingHistory(playerId);
    }

    public int getEnchantingLevel(UUID playerId) {
        return delegate.getEnchantingLevel(playerId);
    }

    public void setEnchantingLevel(UUID playerId, int level) {
        delegate.setEnchantingLevel(playerId, level);
    }

    public void addBook(UUID playerId, EnchantingManager.EnchantmentBook book) {
        delegate.addBook(playerId, book);
    }

    public List<EnchantingManager.EnchantmentBook> getBooks(UUID playerId) {
        return delegate.getBooks(playerId);
    }

    public EnchantingManager.EnchantmentBook applyBook(UUID playerId, int bookIndex) {
        return delegate.applyBook(playerId, bookIndex);
    }

    public Map<String, Integer> getEnchantLevels(UUID playerId) {
        Map<EnchantingManager.SkyBlockEnchantment, Integer> enumMap = delegate.getEnchantments(playerId);
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<EnchantingManager.SkyBlockEnchantment, Integer> e : enumMap.entrySet()) {
            result.put(e.getKey().name(), e.getValue());
        }
        return result;
    }

    public int getEnchantLevel(UUID playerId, String enchantName) {
        try {
            return delegate.getLevel(playerId, EnchantingManager.SkyBlockEnchantment.valueOf(enchantName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public void setEnchantLevel(UUID playerId, String enchantName, int level) {
        try {
            delegate.setEnchantment(playerId, EnchantingManager.SkyBlockEnchantment.valueOf(enchantName.toUpperCase()), level);
        } catch (IllegalArgumentException ignored) {}
    }

    public List<String> getEnchantHistory(UUID playerId) {
        return delegate.getEnchantingHistory(playerId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
