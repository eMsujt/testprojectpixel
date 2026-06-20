package com.skyblock.core.menu;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract base for menus that also act as self-contained event listeners.
 * Subclasses implement {@link #populate()} and may be registered with Bukkit's
 * plugin manager as a {@link Listener} to handle their own inventory events.
 */
public abstract class BaseMenu extends AbstractSkyBlockMenu implements Listener {

    protected BaseMenu(Player player, String title, int rows) {
        super(player, title, rows);
    }

    protected ItemStack createItem(Material material, String displayName, String... lore) {
        return new ItemBuilder(material).displayName(displayName).lore(lore).build();
    }

    protected ItemStack fillerPane() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
    }
}
