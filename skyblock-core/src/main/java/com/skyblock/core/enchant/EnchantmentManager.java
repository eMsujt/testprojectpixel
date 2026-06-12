package com.skyblock.core.enchant;

import java.util.Map;
import java.util.UUID;

/**
 * Singleton facade over {@link EnchantManager} for enchantment lookups and mutations.
 */
public final class EnchantmentManager {

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();

    private final EnchantManager enchantManager = EnchantManager.getInstance();

    private EnchantmentManager() {}

    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId, EnchantManager.EnchantType type) {
        return enchantManager.getLevel(playerId, type);
    }

    public void setEnchantment(UUID playerId, EnchantManager.EnchantType type, int level) {
        enchantManager.setEnchant(playerId, type, level);
    }

    public boolean removeEnchantment(UUID playerId, EnchantManager.EnchantType type) {
        return enchantManager.removeEnchant(playerId, type);
    }

    public Map<EnchantManager.EnchantType, Integer> getEnchantments(UUID playerId) {
        return enchantManager.getEnchants(playerId);
    }

    public int getMaxLevel(EnchantManager.EnchantType type) {
        return enchantManager.getMaxLevel(type);
    }

    public boolean remove(UUID playerId) {
        return enchantManager.remove(playerId);
    }
}
