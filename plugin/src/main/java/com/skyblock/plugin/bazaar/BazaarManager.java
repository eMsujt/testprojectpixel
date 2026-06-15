package com.skyblock.plugin.bazaar;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @deprecated Use {@link com.skyblock.core.manager.BazaarManager} instead.
 */
@Deprecated
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    /**
     * The Hypixel Bazaar product categories.
     * @deprecated Use {@link com.skyblock.core.manager.BazaarManager.BazaarProduct} categories.
     */
    @Deprecated
    public enum Category {
        FARMING_SUPPLIES("§eFarming Supplies", Material.GOLDEN_HOE),
        MINING_SUPPLIES("§eMining Supplies", Material.STONE_PICKAXE),
        COMBAT_SUPPLIES("§eCombat Supplies", Material.IRON_SWORD),
        WINTER("§eWinter", Material.SNOWBALL),
        WOODS_AND_FISHES("§eWoods & Fishes", Material.FISHING_ROD),
        ODDITIES("§eOddities", Material.ENDER_PEARL);

        private final String displayName;
        private final Material icon;

        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        public String displayName() { return displayName; }
        public Material icon() { return icon; }
    }

    /**
     * A single loaded bazaar product.
     * @deprecated Use {@link com.skyblock.core.manager.BazaarManager.BazaarProduct}.
     */
    @Deprecated
    public record Product(String id, Material material, String displayName, Category category,
                          double buyPrice, double sellPrice) {
        public Product {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(material, "material");
            Objects.requireNonNull(displayName, "displayName");
            Objects.requireNonNull(category, "category");
        }
    }

    private BazaarManager() {}

    /** @deprecated Use {@link com.skyblock.core.manager.BazaarManager#getInstance()} */
    @Deprecated
    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    /** @deprecated Use {@link com.skyblock.core.menu.BazaarMenu} directly. */
    @Deprecated
    public void openBazaar(Player player) {
        new com.skyblock.core.menu.BazaarMenu(player).open(player);
    }
}
