package com.skyblock.core.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton estimating the coin networth of items from their base value plus
 * the value added by enchantments, reforges, and dungeon stars.
 *
 * <p>Base values, enchant book values, and reforge stone values are registered
 * up-front and looked up by id when an {@link Item} is priced. Each dungeon
 * star adds a fixed fraction of the item's base value
 * ({@link #STAR_VALUE_FRACTION} per star). Soulbound items cannot be sold or
 * traded, so they contribute nothing to networth.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class NetworthManager {

    /** Fraction of an item's base value added per dungeon star. */
    public static final double STAR_VALUE_FRACTION = 0.05;

    private static final NetworthManager INSTANCE = new NetworthManager();

    /** Base coin value keyed by item id. */
    private final Map<String, Double> baseValues = new HashMap<>();

    /** Coin value of one level of an enchantment, keyed by enchant id. */
    private final Map<String, Double> enchantValues = new HashMap<>();

    /** Coin value of a reforge stone, keyed by reforge id. */
    private final Map<String, Double> reforgeValues = new HashMap<>();

    private NetworthManager() {
    }

    /**
     * Returns the single shared {@code NetworthManager} instance.
     *
     * @return the singleton instance
     */
    public static NetworthManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the base coin value of an item.
     *
     * @param itemId the item id, must not be null
     * @param value  the base coin value, must not be negative
     */
    public void registerBaseValue(String itemId, double value) {
        Objects.requireNonNull(itemId, "itemId");
        requireNonNegative(value, "value");
        baseValues.put(itemId, value);
    }

    /**
     * Returns the registered base value of an item, or 0 if none is registered.
     *
     * @param itemId the item id, must not be null
     * @return the base coin value (0 if unknown)
     */
    public double getBaseValue(String itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return baseValues.getOrDefault(itemId, 0.0);
    }

    /**
     * Registers the coin value contributed by one level of an enchantment.
     *
     * @param enchantId the enchant id, must not be null
     * @param value     the per-level coin value, must not be negative
     */
    public void registerEnchantValue(String enchantId, double value) {
        Objects.requireNonNull(enchantId, "enchantId");
        requireNonNegative(value, "value");
        enchantValues.put(enchantId, value);
    }

    /**
     * Registers the coin value contributed by a reforge stone.
     *
     * @param reforgeId the reforge id, must not be null
     * @param value     the reforge coin value, must not be negative
     */
    public void registerReforgeValue(String reforgeId, double value) {
        Objects.requireNonNull(reforgeId, "reforgeId");
        requireNonNegative(value, "value");
        reforgeValues.put(reforgeId, value);
    }

    /**
     * Computes the networth of a single item: its base value plus the value of
     * its enchantments, reforge, and stars, multiplied by its stack count.
     *
     * <p>Soulbound items return 0 since they cannot be sold or traded.</p>
     *
     * @param item the item to price, must not be null
     * @return the total coin value (0 if soulbound)
     */
    public double calculateValue(Item item) {
        Objects.requireNonNull(item, "item");
        if (item.soulbound) {
            return 0.0;
        }
        double base = getBaseValue(item.itemId);
        double value = base;
        for (Map.Entry<String, Integer> entry : item.enchants.entrySet()) {
            value += enchantValues.getOrDefault(entry.getKey(), 0.0) * entry.getValue();
        }
        if (item.reforge != null) {
            value += reforgeValues.getOrDefault(item.reforge, 0.0);
        }
        value += base * STAR_VALUE_FRACTION * item.stars;
        return value * item.count;
    }

    /**
     * Computes the combined networth of a collection of items.
     *
     * @param items the items to price, must not be null (may be empty)
     * @return the summed coin value
     */
    public double calculateTotal(Collection<Item> items) {
        Objects.requireNonNull(items, "items");
        double total = 0.0;
        for (Item item : items) {
            total += calculateValue(item);
        }
        return total;
    }

    private static void requireNonNegative(double value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
    }

    /**
     * An immutable description of a stack of items for networth pricing.
     *
     * <p>Build instances with {@link #builder(String)}.</p>
     */
    public static final class Item {

        private final String itemId;
        private final int count;
        private final String reforge;
        private final int stars;
        private final boolean soulbound;
        private final Map<String, Integer> enchants;

        private Item(Builder builder) {
            this.itemId = builder.itemId;
            this.count = builder.count;
            this.reforge = builder.reforge;
            this.stars = builder.stars;
            this.soulbound = builder.soulbound;
            this.enchants = Collections.unmodifiableMap(new HashMap<>(builder.enchants));
        }

        public String getItemId() {
            return itemId;
        }

        public int getCount() {
            return count;
        }

        public String getReforge() {
            return reforge;
        }

        public int getStars() {
            return stars;
        }

        public boolean isSoulbound() {
            return soulbound;
        }

        public Map<String, Integer> getEnchants() {
            return enchants;
        }

        /**
         * Creates a builder for an item with the given id and a default count of 1.
         *
         * @param itemId the item id, must not be null
         * @return a new builder
         */
        public static Builder builder(String itemId) {
            return new Builder(itemId);
        }

        /** Mutable builder for {@link Item} instances. */
        public static final class Builder {

            private final String itemId;
            private int count = 1;
            private String reforge;
            private int stars;
            private boolean soulbound;
            private final Map<String, Integer> enchants = new HashMap<>();

            private Builder(String itemId) {
                this.itemId = Objects.requireNonNull(itemId, "itemId");
            }

            /**
             * Sets the stack size.
             *
             * @param count the stack size, must be at least 1
             * @return this builder
             */
            public Builder count(int count) {
                if (count < 1) {
                    throw new IllegalArgumentException("count must be at least 1");
                }
                this.count = count;
                return this;
            }

            /**
             * Sets the reforge applied to the item.
             *
             * @param reforge the reforge id, or {@code null} for none
             * @return this builder
             */
            public Builder reforge(String reforge) {
                this.reforge = reforge;
                return this;
            }

            /**
             * Sets the number of dungeon stars on the item.
             *
             * @param stars the star count, must not be negative
             * @return this builder
             */
            public Builder stars(int stars) {
                if (stars < 0) {
                    throw new IllegalArgumentException("stars must not be negative");
                }
                this.stars = stars;
                return this;
            }

            /**
             * Marks the item as soulbound, excluding it from networth.
             *
             * @param soulbound {@code true} if soulbound
             * @return this builder
             */
            public Builder soulbound(boolean soulbound) {
                this.soulbound = soulbound;
                return this;
            }

            /**
             * Adds an enchantment at the given level.
             *
             * @param enchantId the enchant id, must not be null
             * @param level     the enchant level, must be at least 1
             * @return this builder
             */
            public Builder enchant(String enchantId, int level) {
                Objects.requireNonNull(enchantId, "enchantId");
                if (level < 1) {
                    throw new IllegalArgumentException("level must be at least 1");
                }
                enchants.put(enchantId, level);
                return this;
            }

            /**
             * Builds the immutable {@link Item}.
             *
             * @return the built item
             */
            public Item build() {
                return new Item(this);
            }
        }
    }
}
