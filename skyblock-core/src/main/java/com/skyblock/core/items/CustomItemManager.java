package com.skyblock.core.items;

import com.skyblock.core.model.ItemType;
import com.skyblock.core.model.Rarity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry of every custom SkyBlock item defined by the server.
 *
 * <p>All items are declared statically as {@link SkyBlockItem} enum constants.
 * The manager wraps that enum with an id-keyed lookup for O(1) retrieval.</p>
 */
public final class CustomItemManager {

    /** Every custom item available in SkyBlock. */
    public enum SkyBlockItem {

        // --- Weapons ---
        ASPECT_OF_THE_END("ASPECT_OF_THE_END", "Aspect of the End",       ItemType.WEAPON,    Rarity.EPIC),
        LEAPING_SWORD     ("LEAPING_SWORD",     "Leaping Sword",           ItemType.WEAPON,    Rarity.RARE),
        ZOMBIE_SWORD      ("ZOMBIE_SWORD",       "Zombie Sword",            ItemType.WEAPON,    Rarity.RARE),
        THICK_SWORD       ("THICK_SWORD",        "Thick Sword",             ItemType.WEAPON,    Rarity.UNCOMMON),
        JERRY_SWORD       ("JERRY_SWORD",        "Jerry Sword",             ItemType.WEAPON,    Rarity.COMMON),
        ASPECT_OF_THE_DRAGONS("ASPECT_OF_THE_DRAGONS", "Aspect of the Dragons", ItemType.WEAPON, Rarity.LEGENDARY),
        LIVID_DAGGER      ("LIVID_DAGGER",       "Livid Dagger",            ItemType.WEAPON,    Rarity.LEGENDARY),

        // --- Tools ---
        TREECAPITATOR     ("TREECAPITATOR",      "Treecapitator",           ItemType.TOOL,      Rarity.RARE),
        PROMISING_SHOVEL  ("PROMISING_SHOVEL",   "Promising Shovel",        ItemType.TOOL,      Rarity.COMMON),
        JUNGLE_AXE        ("JUNGLE_AXE",         "Jungle Axe",              ItemType.TOOL,      Rarity.UNCOMMON),

        // --- Armor ---
        HARDENED_DIAMOND_HELMET   ("HARDENED_DIAMOND_HELMET",    "Hardened Diamond Helmet",    ItemType.ARMOR, Rarity.RARE),
        HARDENED_DIAMOND_CHESTPLATE("HARDENED_DIAMOND_CHESTPLATE","Hardened Diamond Chestplate",ItemType.ARMOR, Rarity.RARE),
        HARDENED_DIAMOND_LEGGINGS ("HARDENED_DIAMOND_LEGGINGS",  "Hardened Diamond Leggings",  ItemType.ARMOR, Rarity.RARE),
        HARDENED_DIAMOND_BOOTS    ("HARDENED_DIAMOND_BOOTS",     "Hardened Diamond Boots",     ItemType.ARMOR, Rarity.RARE),
        SUPERIOR_DRAGON_HELMET    ("SUPERIOR_DRAGON_HELMET",     "Superior Dragon Helmet",     ItemType.ARMOR, Rarity.LEGENDARY),
        SUPERIOR_DRAGON_CHESTPLATE("SUPERIOR_DRAGON_CHESTPLATE", "Superior Dragon Chestplate", ItemType.ARMOR, Rarity.LEGENDARY),
        SUPERIOR_DRAGON_LEGGINGS  ("SUPERIOR_DRAGON_LEGGINGS",   "Superior Dragon Leggings",   ItemType.ARMOR, Rarity.LEGENDARY),
        SUPERIOR_DRAGON_BOOTS     ("SUPERIOR_DRAGON_BOOTS",      "Superior Dragon Boots",      ItemType.ARMOR, Rarity.LEGENDARY),

        // --- Accessories ---
        SPEED_TALISMAN    ("SPEED_TALISMAN",     "Speed Talisman",          ItemType.ACCESSORY, Rarity.COMMON),
        FEATHER_TALISMAN  ("FEATHER_TALISMAN",   "Feather Talisman",        ItemType.ACCESSORY, Rarity.COMMON),
        VACCINE_TALISMAN  ("VACCINE_TALISMAN",   "Vaccine Talisman",        ItemType.ACCESSORY, Rarity.UNCOMMON),

        // --- Consumables ---
        MANA_FLUX_POWER_ORB("MANA_FLUX_POWER_ORB", "Mana Flux Power Orb", ItemType.CONSUMABLE, Rarity.UNCOMMON);

        private final String id;
        private final String displayName;
        private final ItemType itemType;
        private final Rarity rarity;

        SkyBlockItem(String id, String displayName, ItemType itemType, Rarity rarity) {
            this.id = id;
            this.displayName = displayName;
            this.itemType = itemType;
            this.rarity = rarity;
        }

        /** Returns the unique string id of this item. */
        public String getId() {
            return id;
        }

        /** Returns the human-readable display name of this item. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns the functional category of this item. */
        public ItemType getItemType() {
            return itemType;
        }

        /** Returns the rarity tier of this item. */
        public Rarity getRarity() {
            return rarity;
        }
    }

    private static final Map<String, SkyBlockItem> BY_ID =
            Collections.unmodifiableMap(
                    Arrays.stream(SkyBlockItem.values())
                          .collect(Collectors.toMap(SkyBlockItem::getId, Function.identity())));

    /**
     * Returns the item registered under the given id, if any.
     *
     * @param id the item's unique string id
     * @return the {@link SkyBlockItem}, or empty if none matches
     */
    public Optional<SkyBlockItem> getById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_ID.get(id));
    }

    /**
     * Returns all registered items.
     *
     * @return an unmodifiable collection of every {@link SkyBlockItem}
     */
    public Collection<SkyBlockItem> getItems() {
        return BY_ID.values();
    }

    /**
     * Returns all items of the given type.
     *
     * @param type the category to filter by, must not be null
     * @return matching items, empty if none
     * @throws IllegalArgumentException if {@code type} is null
     */
    public Collection<SkyBlockItem> getItemsByType(ItemType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        return Arrays.stream(SkyBlockItem.values())
                     .filter(item -> item.itemType == type)
                     .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all items of the given rarity.
     *
     * @param rarity the rarity tier to filter by, must not be null
     * @return matching items, empty if none
     * @throws IllegalArgumentException if {@code rarity} is null
     */
    public Collection<SkyBlockItem> getItemsByRarity(Rarity rarity) {
        if (rarity == null) {
            throw new IllegalArgumentException("rarity must not be null");
        }
        return Arrays.stream(SkyBlockItem.values())
                     .filter(item -> item.rarity == rarity)
                     .collect(Collectors.toUnmodifiableList());
    }
}
