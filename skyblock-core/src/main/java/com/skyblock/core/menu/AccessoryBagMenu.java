package com.skyblock.core.menu;

import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 27-slot Accessory Bag overview menu. One slot per {@link AccessoryRarity}
 * value shows the count of accessories the player owns at that rarity.
 * A summary head at slot 4 reports slot usage and total magic power.
 */
public final class AccessoryBagMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    /** One slot per {@link AccessoryRarity}, left-to-right across row 1. */
    static final int[] RARITY_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16};

    private final UUID playerId;

    public AccessoryBagMenu(Player player) {
        this(player.getUniqueId());
    }

    public AccessoryBagMenu(UUID playerId) {
        super("§5Accessory Bag", 3);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 27; slot++) setItem(slot, pane);

        AccessoryBagManager manager = AccessoryBagManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§dAccessory Bag")
                .lore(
                        "§7Slots: §a" + manager.getSize(playerId),
                        "§7Magic Power: §d" + manager.getTotalMagicPower(playerId))
                .build());

        AccessoryRarity[] rarities = AccessoryRarity.values();
        for (int i = 0; i < RARITY_SLOTS.length && i < rarities.length; i++) {
            AccessoryRarity rarity = rarities[i];
            int count = manager.getContentsByRarity(playerId, rarity).size();
            String color = ItemBuilder.rarityColor(rarity.name()).toString();
            setItem(RARITY_SLOTS[i], new ItemBuilder(Material.IRON_INGOT)
                    .displayName(color + rarity.getDisplayName())
                    .lore("§7Count: §a" + count)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
