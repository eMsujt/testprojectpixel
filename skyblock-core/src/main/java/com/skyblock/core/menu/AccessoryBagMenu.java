package com.skyblock.core.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.util.SkyblockUtils;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Accessory Bag overview menu. One slot per {@link AccessoryRarity}
 * value shows the count of accessories the player owns at that rarity.
 * A summary head at slot 4 reports slot usage and total magic power.
 */
public final class AccessoryBagMenu extends AbstractMenu {

    static final int SUMMARY_SLOT = 4;
    /** One slot per {@link AccessoryRarity}, left-to-right across row 1. */
    static final int[] RARITY_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16};

    public AccessoryBagMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§5Accessory Bag", 54);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ItemStack pane = SkyblockUtils.buildItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) setItem(slot, pane);

        AccessoryBagManager manager = AccessoryBagManager.getInstance();

        setItem(SUMMARY_SLOT, SkyblockUtils.buildItem(Material.ENDER_CHEST,
                "§dAccessory Bag",
                "§7Slots: §a" + manager.getSize(playerId),
                "§7Magic Power: §d" + manager.getTotalMagicPower(playerId)));

        AccessoryRarity[] rarities = AccessoryRarity.values();
        for (int i = 0; i < RARITY_SLOTS.length && i < rarities.length; i++) {
            AccessoryRarity rarity = rarities[i];
            int count = manager.getContentsByRarity(playerId, rarity).size();
            String color = ItemBuilder.rarityColor(rarity.name()).toString();
            setItem(RARITY_SLOTS[i], SkyblockUtils.buildItem(Material.IRON_INGOT,
                    color + rarity.getDisplayName(),
                    "§7Count: §a" + count));
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
