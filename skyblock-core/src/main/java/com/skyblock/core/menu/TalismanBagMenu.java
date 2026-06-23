package com.skyblock.core.menu;

import com.skyblock.core.talisman.manager.TalismanBagManager;
import com.skyblock.core.talisman.manager.TalismanManager;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TalismanBagMenu extends AbstractMenu {

    public TalismanBagMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§5Talisman Bag", 45);
    }

    @Override
    protected void populate() {
        ItemStack pane = SkyblockUtils.buildItem(Material.BLACK_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 45; slot++) setItem(slot, pane);

        TalismanBagManager manager = TalismanBagManager.getInstance();
        List<TalismanManager.TalismanType> contents = manager.getContents(player.getUniqueId());

        setItem(4, SkyblockUtils.buildItem(Material.ENDER_CHEST,
                "§5Talisman Bag",
                "§7Talismans: §a" + contents.size() + " §7/ §a" + TalismanBagManager.DEFAULT_CAPACITY));

        int i = 0;
        for (TalismanManager.TalismanType type : contents) {
            if (i >= contentCapacity()) break;
            setItem(contentSlot(i++), SkyblockUtils.buildItem(Material.GOLD_NUGGET,
                    "§6" + type.name(),
                    "§7Rarity: " + type.rarity.getDisplayName(),
                    "§7+" + type.bonus + " " + type.stat.name()));
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
