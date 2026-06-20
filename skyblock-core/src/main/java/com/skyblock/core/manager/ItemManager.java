package com.skyblock.core.manager;

import com.skyblock.core.items.CustomItemManager;
import com.skyblock.core.items.CustomItemManager.SkyBlockItem;
import com.skyblock.core.model.ItemType;
import com.skyblock.core.model.Rarity;

import java.util.Collection;
import java.util.Optional;

/**
 * Singleton entry-point for custom SkyBlock item data in the manager package.
 *
 * <p>Delegates all storage and lookup to {@link CustomItemManager}. Rarity
 * tiers are defined in {@link Rarity}; item categories in {@link ItemType}.</p>
 */
public final class ItemManager {

    private static final ItemManager INSTANCE = new ItemManager();

    private final CustomItemManager delegate = new CustomItemManager();

    private ItemManager() {}

    public static ItemManager getInstance() {
        return INSTANCE;
    }

    public Optional<SkyBlockItem> getById(String id) {
        return delegate.getById(id);
    }

    public Collection<SkyBlockItem> getItems() {
        return delegate.getItems();
    }

    public Collection<SkyBlockItem> getItemsByType(ItemType type) {
        return delegate.getItemsByType(type);
    }

    public Collection<SkyBlockItem> getItemsByRarity(Rarity rarity) {
        return delegate.getItemsByRarity(rarity);
    }
}
