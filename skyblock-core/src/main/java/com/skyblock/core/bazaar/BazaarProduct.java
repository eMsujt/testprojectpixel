package com.skyblock.core.bazaar;

import java.util.Objects;

/**
 * Describes a tradeable product in the bazaar.
 *
 * @param itemId      unique identifier used as the key in order books
 * @param displayName human-readable name shown in command output
 * @param category    broad grouping (e.g. "FARMING", "MINING", "COMBAT")
 */
public record BazaarProduct(String itemId, String displayName, String category) {

    public BazaarProduct {
        Objects.requireNonNull(itemId, "itemId");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(category, "category");
        if (itemId.isBlank()) throw new IllegalArgumentException("itemId must not be blank");
    }
}
