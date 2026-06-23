package com.skyblock.core.npc;

import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import com.skyblock.core.menu.AbstractMenu;
import com.skyblock.core.npc.NpcManager.NpcDefinition;
import com.skyblock.core.util.HeadTextures;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;

/**
 * 54-slot shop GUI opened when a player right-clicks an NPC ArmorStand.
 * Each inner slot displays one {@link ShopEntry}; clicking it deducts coins
 * via {@link EconomyManager} and delivers the item to the player's inventory.
 */
public final class NpcShopMenu extends AbstractMenu {

    private static final int[] ITEM_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private final NpcDefinition npc;

    public NpcShopMenu(JavaPlugin plugin, Player player, NpcDefinition npc) {
        super(plugin, player, "§6" + npc.name(), 54);
        this.npc = npc;
    }

    @Override
    protected void populate() {
        ItemStack pane = SkyblockUtils.buildItem(Material.BLACK_STAINED_GLASS_PANE, "§r");
        SkyblockUtils.fillBorder(getRows(), this::setItem, pane);

        List<ShopEntry> entries = ShopManager.getInstance()
                .getShop(npc.shopId())
                .map(ShopManager.Shop::entries)
                .orElse(List.of());

        for (int i = 0; i < entries.size() && i < ITEM_SLOTS.length; i++) {
            ShopEntry entry = entries.get(i);
            Material mat = HeadTextures.itemMaterial(entry.itemId());
            int slot = ITEM_SLOTS[i];
            String sellLine = entry.sellPrice() > 0
                    ? "§7Sell Price: §6" + entry.sellPrice() + " coins"
                    : "§7Not sellable";
            ItemStack icon = ItemBuilder.forItem(entry.itemId())
                    .displayName("§e" + formatName(entry.itemId()))
                    .lore("§7Buy Price: §6" + entry.buyPrice() + " coins",
                            sellLine,
                            "",
                            "§eLeft-click to purchase!")
                    .build();
            setItem(slot, icon,
                    event -> {
                        event.setCancelled(true);
                        Player buyer = (Player) event.getWhoClicked();
                        if (!EconomyManager.getInstance().withdraw(buyer.getUniqueId(), entry.buyPrice())) {
                            buyer.sendMessage("§cYou don't have enough coins! ("
                                    + entry.buyPrice() + " required)");
                            return;
                        }
                        buyer.getInventory().addItem(new ItemStack(mat));
                        buyer.sendMessage("§aPurchased §e" + formatName(entry.itemId())
                                + "§a for §6" + entry.buyPrice() + " coins§a!");
                    });
        }

        setItem(49, SkyblockUtils.buildItem(Material.BOOK,
                "§6" + npc.name(),
                "§7Browse and purchase items",
                "§7from this merchant."));
    }

    private static String formatName(String itemId) {
        StringBuilder sb = new StringBuilder();
        for (String word : itemId.split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }
}
