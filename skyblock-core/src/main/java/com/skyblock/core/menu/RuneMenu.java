package com.skyblock.core.menu;

import com.skyblock.core.manager.RuneManager;
import com.skyblock.core.manager.RuneManager.RuneType;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class RuneMenu extends AbstractMenu {

    public RuneMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§5§lRUNES", 45);
    }

    @Override
    protected void populate() {
        ItemStack pane = SkyblockUtils.buildItem(Material.BLACK_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 45; slot++) setItem(slot, pane);

        RuneType[] types = RuneType.values();
        int slot = 10;
        for (RuneType type : types) {
            if (slot >= 44) break;
            setItem(slot, SkyblockUtils.buildItem(Material.MAGMA_CREAM,
                    "§5" + type.getDisplayName(),
                    "§7Max Level: §d" + type.getMaxLevel(),
                    "§7Effect: §f" + type.getVisual()));
            slot++;
            if ((slot % 9) == 8) slot += 2;
        }

        setItem(4, SkyblockUtils.buildItem(Material.NETHER_STAR,
                "§5§lRunes",
                "§7Apply runes to your items",
                "§7to gain cosmetic effects.",
                "§7Total Runes: §d" + RuneManager.getInstance().getRegistry().size()));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
