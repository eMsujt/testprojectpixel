package com.skyblock.core.listener;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import com.skyblock.core.minion.gui.MinionMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Handles minion placement and opening. Right-clicking a block while holding a
 * minion item places that minion; right-clicking an already-placed minion opens
 * its management menu.
 */
public final class MinionListener implements Listener {

    private static final MinionListener INSTANCE = new MinionListener();

    private final MinionManager manager = MinionManager.getInstance();

    private MinionListener() {}

    public static MinionListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        // Opening an existing minion takes precedence over placing a new one.
        MinionData existing = manager.getMinionAtLocation(locationKey(clicked.getLocation()));
        if (existing != null) {
            event.setCancelled(true);
            new MinionMenu(existing).open(event.getPlayer());
            return;
        }

        MinionType type = heldMinionType(event.getItem());
        if (type == null) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        Location loc = clicked.getRelative(event.getBlockFace()).getLocation();
        String locationKey = locationKey(loc);

        MinionData data = manager.placeMinion(player.getUniqueId(), type, MinionTier.TIER_1);
        manager.setPlacement(player.getUniqueId(), locationKey, type);
        manager.setMinionLocation(data.id, locationKey);
    }

    private static String locationKey(Location loc) {
        return loc.getWorld().getName()
                + "," + loc.getBlockX()
                + "," + loc.getBlockY()
                + "," + loc.getBlockZ();
    }

    /**
     * Returns the {@link MinionType} denoted by the held item's display name
     * (ignoring colour codes), or {@code null} if it is not a known minion item.
     */
    private static MinionType heldMinionType(ItemStack item) {
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return null;
        }
        String stripped = meta.getDisplayName().replaceAll("§.", "");
        if (!stripped.endsWith("Minion")) {
            return null;
        }
        for (MinionType type : MinionType.values()) {
            if (type.getDisplayName().equals(stripped)) {
                return type;
            }
        }
        return null;
    }
}
