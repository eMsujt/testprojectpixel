package com.skyblock.core.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton managing durability-based coin repair costs for common tool/armor materials.
 */
public final class RepairManager {

    private static final RepairManager INSTANCE = new RepairManager();

    /** Base coin cost per durability point restored, keyed by material tier. */
    private static final Map<String, Integer> COST_PER_POINT = new HashMap<>();

    static {
        // Wood / leather
        COST_PER_POINT.put("WOODEN", 1);
        COST_PER_POINT.put("LEATHER", 1);
        // Stone / chain
        COST_PER_POINT.put("STONE", 2);
        COST_PER_POINT.put("CHAINMAIL", 2);
        // Iron / golden
        COST_PER_POINT.put("IRON", 4);
        COST_PER_POINT.put("GOLDEN", 3);
        // Diamond
        COST_PER_POINT.put("DIAMOND", 10);
        // Netherite
        COST_PER_POINT.put("NETHERITE", 20);
    }

    private RepairManager() {}

    public static RepairManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the coin cost to fully repair {@code item}, or -1 if the item cannot be repaired.
     */
    public int getRepairCost(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return -1;
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return -1;
        }
        int damage = damageable.getDamage();
        if (damage <= 0) {
            return 0;
        }
        int costPerPoint = resolveRate(item.getType());
        if (costPerPoint < 0) {
            return -1;
        }
        return damage * costPerPoint;
    }

    /**
     * Fully repairs {@code item} in place (sets damage to 0).
     *
     * @return {@code true} if the item was repaired
     */
    public boolean repair(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return false;
        }
        if (damageable.getDamage() <= 0) {
            return false;
        }
        damageable.setDamage(0);
        item.setItemMeta(meta);
        return true;
    }

    /**
     * Returns whether {@code player} has at least {@code cost} coins.
     * Delegates to the player's experience as a simple coin proxy if no
     * economy hook is present; implementations should override via the
     * Economy API.
     */
    public boolean hasCoins(Player player, int cost) {
        // Treat XP level * 100 as coin balance when no economy plugin is wired.
        return player.getLevel() * 100 >= cost;
    }

    /**
     * Deducts {@code cost} coins from {@code player}.
     */
    public void deductCoins(Player player, int cost) {
        // Simple XP-level proxy — replace with Economy hook as needed.
        int levels = (int) Math.ceil(cost / 100.0);
        player.setLevel(Math.max(0, player.getLevel() - levels));
    }

    // ------------------------------------------------------------------ //

    private int resolveRate(Material material) {
        String name = material.name();
        for (Map.Entry<String, Integer> entry : COST_PER_POINT.entrySet()) {
            if (name.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return -1;
    }
}
