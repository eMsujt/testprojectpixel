package com.skyblock.core.manager;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Registers the canonical set of Hypixel Hub NPC shops into {@link ShopManager}.
 *
 * <p>Not thread-safe; intended to be initialized once on plugin enable before
 * any player connections.</p>
 */
public final class NPCShopManager {

    private static final NPCShopManager INSTANCE = new NPCShopManager();

    private final Set<String> shopIds = new LinkedHashSet<>();

    private NPCShopManager() {}

    public static NPCShopManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers all static Hypixel NPC shops into {@link ShopManager}.
     * Safe to call multiple times — existing entries are overwritten.
     */
    public void registerDefaults() {
        ShopManager sm = ShopManager.getInstance();

        add(sm, "general_goods", "§aGeneral Goods Merchant", List.of(
                new ShopManager.ShopEntry("OAK_LOG",         8,   4),
                new ShopManager.ShopEntry("COBBLESTONE",     2,   1),
                new ShopManager.ShopEntry("SAND",            3,   1),
                new ShopManager.ShopEntry("GRAVEL",          3,   1),
                new ShopManager.ShopEntry("FLINT",           4,   2),
                new ShopManager.ShopEntry("STRING",          6,   3),
                new ShopManager.ShopEntry("FEATHER",         5,   2),
                new ShopManager.ShopEntry("LEATHER",        10,   5)));

        add(sm, "lumber_merchant", "§2Lumber Merchant", List.of(
                new ShopManager.ShopEntry("OAK_LOG",         8,   4),
                new ShopManager.ShopEntry("SPRUCE_LOG",      8,   4),
                new ShopManager.ShopEntry("BIRCH_LOG",       8,   4),
                new ShopManager.ShopEntry("JUNGLE_LOG",      8,   4),
                new ShopManager.ShopEntry("ACACIA_LOG",      8,   4),
                new ShopManager.ShopEntry("DARK_OAK_LOG",    8,   4)));

        add(sm, "mine_merchant", "§7Mine Merchant", List.of(
                new ShopManager.ShopEntry("IRON_PICKAXE",  200,   0),
                new ShopManager.ShopEntry("STONE_PICKAXE",  50,   0),
                new ShopManager.ShopEntry("TORCH",           2,   0),
                new ShopManager.ShopEntry("COAL",            5,   2),
                new ShopManager.ShopEntry("IRON_INGOT",     20,   8),
                new ShopManager.ShopEntry("GOLD_INGOT",     40,  15),
                new ShopManager.ShopEntry("REDSTONE",        3,   1),
                new ShopManager.ShopEntry("LAPIS_LAZULI",    3,   1)));

        add(sm, "adventurer", "§dAdventurer", List.of(
                new ShopManager.ShopEntry("MAP",            200,   0),
                new ShopManager.ShopEntry("COMPASS",        150,   0),
                new ShopManager.ShopEntry("BOOK",            50,  20),
                new ShopManager.ShopEntry("ENDER_PEARL",    100,  40),
                new ShopManager.ShopEntry("BLAZE_ROD",       80,  30),
                new ShopManager.ShopEntry("GHAST_TEAR",     500,   0)));

        add(sm, "fishmonger", "§9Fishmonger", List.of(
                new ShopManager.ShopEntry("FISHING_ROD",    100,   0),
                new ShopManager.ShopEntry("COD",              3,   1),
                new ShopManager.ShopEntry("SALMON",           5,   2),
                new ShopManager.ShopEntry("TROPICAL_FISH",   10,   4),
                new ShopManager.ShopEntry("PUFFERFISH",       8,   3),
                new ShopManager.ShopEntry("INK_SAC",          4,   1),
                new ShopManager.ShopEntry("LILY_PAD",         3,   1)));
    }

    /** Returns an unmodifiable view of the registered shop IDs, in registration order. */
    public Set<String> getShopIds() {
        return Collections.unmodifiableSet(shopIds);
    }

    private void add(ShopManager sm, String id, String title, List<ShopManager.ShopEntry> entries) {
        sm.registerShop(id, title, entries);
        shopIds.add(id);
    }
}
