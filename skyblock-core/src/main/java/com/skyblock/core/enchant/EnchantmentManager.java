package com.skyblock.core.enchant;

import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing SkyBlock enchantments applied to player items.
 *
 * <p>Delegates state storage to {@link EnchantManager}.</p>
 */
public final class EnchantmentManager {

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();

    private final EnchantManager delegate = EnchantManager.getInstance();

    private EnchantmentManager() {}

    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId, EnchantManager.EnchantType type) {
        return delegate.getLevel(playerId, type);
    }

    public void setEnchantment(UUID playerId, EnchantManager.EnchantType type, int level) {
        delegate.setEnchant(playerId, type, level);
    }

    public boolean removeEnchantment(UUID playerId, EnchantManager.EnchantType type) {
        return delegate.removeEnchant(playerId, type);
    }

    public Map<EnchantManager.EnchantType, Integer> getEnchantments(UUID playerId) {
        return delegate.getEnchants(playerId);
    }

    public int getMaxLevel(EnchantManager.EnchantType type) {
        return delegate.getMaxLevel(type);
    }

    public boolean remove(UUID playerId) {
        return delegate.remove(playerId);
    }
}
