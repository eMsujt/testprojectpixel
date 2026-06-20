package com.skyblock.core.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.manager.MiningManager.OreType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public final class MiningListener implements Listener {

    private static final MiningListener INSTANCE = new MiningListener();

    private static final Map<Material, Long> STONE_XP = Map.of(
            Material.STONE,      1L,
            Material.COBBLESTONE, 1L,
            Material.OBSIDIAN,   5L
    );

    private final MiningManager miningManager         = MiningManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    private MiningListener() {}

    public static MiningListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Material type = event.getBlock().getType();

        OreType ore = MiningManager.MATERIAL_TO_ORE.get(type);
        if (ore != null) {
            int before = miningManager.getLevel(uuid);
            miningManager.addXp(uuid, ore.getXp());
            int after = miningManager.getLevel(uuid);
            if (after > before) {
                player.sendTitle("§aSkill Level Up!", "§eMining §a→ §eLVL " + after, 10, 60, 20);
            }
            collectionManager.addCollection(uuid, type, 1);
            return;
        }

        Long stoneXp = STONE_XP.get(type);
        if (stoneXp != null) {
            miningManager.addXp(uuid, stoneXp);
            collectionManager.addCollection(uuid, type, 1);
        }
    }
}
