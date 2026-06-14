package com.skyblock.plugin.bazaar;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * YAML-driven registry of Bazaar products loaded from {@code bazaar.yml}.
 *
 * <p>Each key under the {@code products} section defines one product by its
 * product-id, carrying the {@link Material} shown in the menu, a display name and
 * its instant buy/sell prices (the sell price is normally slightly lower,
 * modelling the spread). The bundled default is copied out of the jar on first
 * run. Loaded products are held in memory in definition order and looked up by
 * id; {@link #openBazaar} builds a {@link BazaarMenu} on demand and shows it.</p>
 */
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    /**
     * A single loaded bazaar product.
     *
     * @param id          the product-id
     * @param material    the item shown in the menu
     * @param displayName the menu display name (supports colour codes)
     * @param buyPrice    the instant-buy price in coins
     * @param sellPrice   the instant-sell price in coins
     */
    public record Product(String id, Material material, String displayName, double buyPrice, double sellPrice) {
        public Product {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(material, "material");
            Objects.requireNonNull(displayName, "displayName");
        }
    }

    private final Map<String, Product> products = new LinkedHashMap<>();

    private BazaarManager() {
    }

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code bazaar.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every defined product.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "bazaar.yml");
        if (!file.exists() && plugin.getResource("bazaar.yml") != null) {
            plugin.saveResource("bazaar.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("products")
                ? cfg.getConfigurationSection("products")
                : cfg;
        products.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            Product product = parse(plugin, id, root.getConfigurationSection(id));
            if (product != null) {
                products.put(id, product);
            }
        }
        plugin.getLogger().info("Loaded " + products.size() + " bazaar products.");
    }

    /** Parses a single product section, or returns {@code null} if it is invalid. */
    private Product parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", id));
        if (material == null) {
            plugin.getLogger().warning("Skipping bazaar product '" + id + "': unknown material.");
            return null;
        }
        String displayName = section.getString("name", id);
        double buyPrice = section.getDouble("buy");
        double sellPrice = section.getDouble("sell");
        return new Product(id, material, displayName, buyPrice, sellPrice);
    }

    /** Returns the loaded product with the given id, or {@code null} if absent. */
    public Product getProduct(String id) {
        return products.get(id);
    }

    /** Returns an unmodifiable view of all loaded products in definition order. */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }

    /** Opens the Bazaar menu listing every loaded product for a player. */
    public void openBazaar(Player player) {
        Objects.requireNonNull(player, "player");
        new BazaarMenu(getProducts()).open(player);
    }
}
