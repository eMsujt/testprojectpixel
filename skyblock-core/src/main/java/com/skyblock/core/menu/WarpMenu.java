package com.skyblock.core.menu;

import com.skyblock.core.manager.Warp;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.manager.WarpManager.WarpLocation;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 54-slot warp menu opened by {@code /warp}. Renders one ender-pearl item per
 * {@link WarpLocation}, starting at slot 9 below a light-blue-pane top border,
 * showing each destination's display name and whether it is currently registered
 * in the {@link WarpManager}.
 */
public final class WarpMenu extends AbstractMenu {

    public WarpMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§bFast Travel", 54);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        WarpManager manager = WarpManager.getInstance();

        int slot = 9;
        for (WarpLocation location : WarpLocation.values()) {
            Optional<Warp> warp = manager.getWarp(location);
            boolean registered = warp.isPresent();
            setItem(slot, new ItemBuilder(Material.ENDER_PEARL)
                    .displayName("§d" + location.getDisplayName())
                    .lore(
                            "§7Warp to " + location.getDisplayName() + ".",
                            "",
                            registered ? "§eClick to warp!" : "§cNot available")
                    .build(),
                    event -> {
                        if (registered) {
                            player.closeInventory();
                            player.teleport(warp.get().toLocation());
                            player.sendMessage("§aWarped to §d" + location.getDisplayName() + "§a.");
                        } else {
                            player.sendMessage("§cThat warp is not available yet.");
                        }
                    });
            slot++;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        super.handleClick(event);
    }
}
