package com.skyblock.plugin.item;

import com.skyblock.plugin.items.SkyBlockItem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * In-memory registry of the built-in weapon reforges.
 *
 * <p>Each reforge is defined in code with the {@link SkyBlockItem.StatBlock} of
 * bonuses it grants and is looked up by its lowercase id. The set mirrors the
 * weapon reforges available at a SkyBlock anvil.</p>
 */
public final class ReforgeManager {

    private static final ReforgeManager INSTANCE = new ReforgeManager();

    private final Map<String, Reforge> reforges = new LinkedHashMap<>();

    private ReforgeManager() {
        // health, defense, strength, intelligence, critChance, critDamage, speed
        register(new Reforge("sharp", "Sharp",
                new SkyBlockItem.StatBlock(0, 0, 0, 0, 5, 25, 0)));
        register(new Reforge("gentle", "Gentle",
                new SkyBlockItem.StatBlock(0, 0, 0, 0, 7, 0, 5)));
        register(new Reforge("heroic", "Heroic",
                new SkyBlockItem.StatBlock(0, 0, 15, 0, 0, 0, 0)));
        register(new Reforge("spicy", "Spicy",
                new SkyBlockItem.StatBlock(0, 0, 5, 0, 1, 15, 5)));
        register(new Reforge("legendary", "Legendary",
                new SkyBlockItem.StatBlock(10, 5, 5, 5, 3, 10, 1)));
        register(new Reforge("fierce", "Fierce",
                new SkyBlockItem.StatBlock(0, 0, 4, 0, 3, 18, 0)));
        register(new Reforge("odd", "Odd",
                new SkyBlockItem.StatBlock(0, 0, 0, 12, 9, 0, 0)));
        register(new Reforge("fast", "Fast",
                new SkyBlockItem.StatBlock(0, 0, 0, 0, 4, 0, 15)));
    }

    public static ReforgeManager getInstance() {
        return INSTANCE;
    }

    /** Adds a reforge to the registry keyed by its lowercase id. */
    private void register(Reforge reforge) {
        reforges.put(reforge.id().toLowerCase(Locale.ROOT), reforge);
    }

    /** Returns the registered reforge with the given id, or {@code null} if absent. */
    public Reforge getReforge(String id) {
        return id == null ? null : reforges.get(id.toLowerCase(Locale.ROOT));
    }

    /** Returns an unmodifiable view of all registered reforges keyed by id. */
    public Map<String, Reforge> getReforges() {
        return Collections.unmodifiableMap(reforges);
    }

    /**
     * An immutable reforge: a unique id, a display name and the
     * {@link SkyBlockItem.StatBlock} of bonuses it grants.
     *
     * @param id          the reforge's unique id
     * @param displayName the reforge's human-readable name
     * @param statBlock   the stat bonuses the reforge grants
     */
    public record Reforge(String id, String displayName, SkyBlockItem.StatBlock statBlock) {
    }
}
