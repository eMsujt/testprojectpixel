package com.skyblock.core.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarOrder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * GUI menu opened by {@code /bazaar}. Renders each of the viewing player's
 * standing {@link BazaarOrder}s (from {@link BazaarManager#getOrdersForPlayer})
 * as a paper item showing its type, item, quantity and unit price. A border of
 * gray panes frames the top and bottom rows; an empty-state barrier appears
 * when the player has no orders.
 */
public final class BazaarMenu extends AbstractMenu {

    /** Order slots filling the middle four rows (10–43, edges excluded). */
    static final int[] ORDER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public BazaarMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§6§lBazaar", 54);
    }

    @Override
    protected void populate() {
        ItemStack pane = SkyblockUtils.buildItem(Material.YELLOW_STAINED_GLASS_PANE, "§r");
        SkyblockUtils.fillBorder(getRows(), this::setItem, pane);

        List<BazaarOrder> orders = BazaarManager.getInstance().getOrdersForPlayer(player.getUniqueId());

        for (int i = 0; i < orders.size() && i < ORDER_SLOTS.length; i++) {
            BazaarOrder order = orders.get(i);
            setItem(ORDER_SLOTS[i], SkyblockUtils.buildItem(Material.PAPER,
                    "§e" + order.type().getDisplayName(),
                    "§7Item: §f" + order.itemId(),
                    "§7Quantity: §e" + order.quantity(),
                    "§7Price each: §6" + (long) order.priceEach() + " coins"));
        }

        if (orders.isEmpty()) {
            setItem(22, SkyblockUtils.buildItem(Material.BARRIER,
                    "§cNo Orders",
                    "§7You have no standing bazaar orders."));
        }
    }
}
