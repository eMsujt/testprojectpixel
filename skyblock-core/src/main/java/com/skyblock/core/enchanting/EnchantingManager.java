package com.skyblock.core.enchanting;

import java.util.Map;
import java.util.UUID;

/**
 * Singleton facade over {@link EnchantmentManager}.
 *
 * <p>Exposes enchantment read/write operations under the {@code EnchantingManager}
 * name expected by the enchanting skill system, delegating every call to the
 * underlying {@link EnchantmentManager} singleton so there is a single source
 * of truth for enchantment data.</p>
 */
public final class EnchantingManager {

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    private final EnchantmentManager delegate = EnchantmentManager.getInstance();

    private EnchantingManager() {
    }

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId, EnchantmentManager.SkyBlockEnchantment enchantment) {
        return delegate.getLevel(playerId, enchantment);
    }

    public void setEnchantment(UUID playerId, EnchantmentManager.SkyBlockEnchantment enchantment, int level) {
        delegate.setEnchantment(playerId, enchantment, level);
    }

    public boolean removeEnchantment(UUID playerId, EnchantmentManager.SkyBlockEnchantment enchantment) {
        return delegate.removeEnchantment(playerId, enchantment);
    }

    public Map<EnchantmentManager.SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        return delegate.getEnchantments(playerId);
    }

    public int getMaxLevel(EnchantmentManager.SkyBlockEnchantment enchantment) {
        return delegate.getMaxLevel(enchantment);
    }

    public boolean remove(UUID playerId) {
        return delegate.remove(playerId);
    }
}
