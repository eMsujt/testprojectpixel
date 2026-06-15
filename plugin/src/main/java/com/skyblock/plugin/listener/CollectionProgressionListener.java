package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

/**
 * Records collection progress through {@link CollectionsManager} whenever a
 * player breaks a tracked block, mapping the broken {@link Material} to a single
 * unit of that collection.
 *
 * <p>Crops are only counted once mature: {@link Ageable} crops must have reached
 * their maximum age, while all other collectible blocks always count.</p>
 */
public final class CollectionProgressionListener implements Listener {

    /** Blocks whose breaking contributes to a player's collections. */
    private static final Set<Material> TRACKED = Set.of(
            Material.WHEAT,
            Material.POTATOES,
            Material.CARROTS,
            Material.BEETROOTS,
            Material.NETHER_WART,
            Material.PUMPKIN,
            Material.MELON,
            Material.SUGAR_CANE,
            Material.CACTUS,
            Material.COCOA,
            Material.STONE,
            Material.COBBLESTONE,
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.SPRUCE_LOG,
            Material.JUNGLE_LOG,
            Material.ACACIA_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.CHERRY_LOG
    );

    private final CollectionManager collectionManager = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if (!TRACKED.contains(type)) return;
        if (!isMature(block)) return;
        collectionManager.addItems(event.getPlayer().getUniqueId(), type.name(), 1);
    }

    /**
     * Returns whether the block has finished growing. {@link Ageable} crops are
     * mature only at their maximum age; all other blocks are always mature.
     */
    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }
}
