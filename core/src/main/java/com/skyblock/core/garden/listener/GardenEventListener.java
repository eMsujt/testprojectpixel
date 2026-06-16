package com.skyblock.core.garden.listener;

import com.skyblock.core.garden.manager.GardenManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumMap;
import java.util.Map;

public final class GardenEventListener implements Listener {

    private static final Map<Material, String> CROP_MATERIALS;

    static {
        Map<Material, String> map = new EnumMap<>(Material.class);
        map.put(Material.WHEAT,          "wheat");
        map.put(Material.CARROTS,        "carrot");
        map.put(Material.POTATOES,       "potato");
        map.put(Material.MELON,          "melon");
        map.put(Material.MELON_STEM,     "melon");
        map.put(Material.PUMPKIN,        "pumpkin");
        map.put(Material.PUMPKIN_STEM,   "pumpkin");
        map.put(Material.CACTUS,         "cactus");
        map.put(Material.SUGAR_CANE,     "sugarcane");
        map.put(Material.BROWN_MUSHROOM, "mushroom");
        map.put(Material.RED_MUSHROOM,   "mushroom");
        map.put(Material.COCOA,          "cocoa");
        map.put(Material.NETHER_WART,    "nether_wart");
        CROP_MATERIALS = Map.copyOf(map);
    }

    private final GardenManager manager;

    public GardenEventListener(GardenManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("manager must not be null");
        }
        this.manager = manager;
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getNewState().getBlock();
        String crop = CROP_MATERIALS.get(block.getType());
        if (crop == null) {
            crop = CROP_MATERIALS.get(event.getNewState().getType());
        }
        if (crop != null) {
            // Award crop level progress to all nearby players who might own this plot;
            // without a plot-ownership system we skip awarding to avoid false attribution.
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        String crop = CROP_MATERIALS.get(event.getClickedBlock().getType());
        if (crop == null) {
            return;
        }
        Player player = event.getPlayer();
        manager.incrementCropLevel(player.getUniqueId(), crop);
    }
}
