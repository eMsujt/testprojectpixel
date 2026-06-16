package com.skyblock.core.manager;

import com.skyblock.core.enchanting.EnchantingManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock enchantment tracking.
 *
 * <p>Delegates all state to {@link EnchantingManager}. Use
 * {@link EnchantingManager.SkyBlockEnchantment} as the enchant type.</p>
 */
public final class EnchantmentManager {

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
