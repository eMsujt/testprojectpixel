package com.skyblock.plugin.menus;

import com.skyblock.plugin.economy.ShopManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 *
 * The Shop menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Shop}, backed by {@link ShopManager}.
 * Each loaded shop is laid out as a clickable icon within a
 * {@code GRAY_STAINED_GLASS_PANE} border; clicking one opens that shop for the
 * player via {@link ShopManager#openShop(String, Player)}.</p>
 */
@Deprecated
public class ShopMenu extends Menu {

    public ShopMenu() {
        super("§6Shop", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        ShopManager shops = ShopManager.getInstance();
        int slot = 10;
        for (ShopManager.Shop shop : shops.getShops().values()) {
            while (isBorder(slot)) {
                slot++;
            }
            if (slot >= 45) {
                break;
            }
            setItem(slot, new ItemBuilder(Material.CHEST)
                            .displayName(shop.title())
                            .lore("§eClick to browse!")
                            .build(),
                    event -> shops.openShop(shop.id(), (Player) event.getWhoClicked()));
            slot++;
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            if (isBorder(slot)) {
                setItem(slot, pane);
            }
        }
    }

    /** Returns whether the given slot lies on the menu's outer edge. */
    private boolean isBorder(int slot) {
        int column = slot % 9;
        return slot < 9 || slot >= 45 || column == 0 || column == 8;
    }
}
