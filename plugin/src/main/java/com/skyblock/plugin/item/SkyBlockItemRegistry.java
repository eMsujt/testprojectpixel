package com.skyblock.plugin.item;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory registry of {@link SkyBlockItem} definitions, keyed by id.
 *
 * <p>Items are registered programmatically and looked up by their unique id.</p>
 */
public final class SkyBlockItemRegistry {

    private static final SkyBlockItemRegistry INSTANCE = new SkyBlockItemRegistry();

    private final Map<String, SkyBlockItem> items = new LinkedHashMap<>();

    private SkyBlockItemRegistry() {
    }

    public static SkyBlockItemRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registers an item, replacing any previously registered item with the same id.
     *
     * @param item the item to register, never null
     * @throws NullPointerException if {@code item} is null
     */
    public void register(SkyBlockItem item) {
        Objects.requireNonNull(item, "item");
        items.put(item.id(), item);
    }

    /** Returns the registered item with the given id, or {@code null} if absent. */
    public SkyBlockItem getItem(String id) {
        return items.get(id);
    }

    /** Returns an unmodifiable view of all registered items keyed by id. */
    public Map<String, SkyBlockItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
