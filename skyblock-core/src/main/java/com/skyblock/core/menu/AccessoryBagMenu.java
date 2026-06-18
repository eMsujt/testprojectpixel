package com.skyblock.core.menu;

import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.manager.AccessoryBagManager.AccessoryTier;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager.TalismanType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * GUI menu opened by {@code /accessories}. Renders the accessories in a player's
 * {@link AccessoryBagManager bag}, grouped by rarity from COMMON up to SPECIAL.
 * Each rarity's accessories occupy a row of inner slots; the summary head at the
 * top reports slot usage and total magic power.
 */
public final class AccessoryBagMenu extends Menu {

    static final int SUMMARY_SLOT = 4;

    /** Inner slot columns (1–7) for each of the four rows below the summary header. */
    private static final int[] ROW_STARTS = {10, 19, 28, 37};
    private static final int COLUMNS = 7;

    private final UUID playerId;

    public AccessoryBagMenu(Player player) {
        this(player.getUniqueId());
    }

    public AccessoryBagMenu(UUID playerId) {
        super("§5Accessory Bag", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        AccessoryBagManager manager = AccessoryBagManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§dAccessory Bag")
                .lore(
                        "§7Slots: §a" + manager.getSize(playerId) + "§7/§a" + manager.getUnlockedSlots(playerId),
                        "§7Magic Power: §d" + manager.getTotalMagicPower(playerId))
                .build());

        int row = 0;
        for (AccessoryRarity rarity : AccessoryRarity.values()) {
            if (row >= ROW_STARTS.length) break;
            List<TalismanType> owned = new ArrayList<>(manager.getContentsByRarity(playerId, rarity));
            if (owned.isEmpty()) continue;

            String color = ItemBuilder.rarityColor(rarity.name()).toString();
            int rowStart = ROW_STARTS[row];
            for (int col = 0; col < COLUMNS && col < owned.size(); col++) {
                TalismanType type = owned.get(col);
                setItem(rowStart + col, new ItemBuilder(Material.NETHER_STAR)
                        .displayName(color + type.name())
                        .lore(
                                "§7Rarity: " + color + rarity.getDisplayName(),
                                "§7Bonus: §a+" + (int) type.bonus + " " + type.stat.getDisplayName(),
                                "§7Magic Power: §d" + magicPower(rarity))
                        .build());
            }
            row++;
        }

        if (manager.getSize(playerId) == 0) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cAccessory Bag Empty")
                    .lore("§7Add accessories to your bag.")
                    .build());
        }
    }

    private static int magicPower(AccessoryRarity rarity) {
        AccessoryTier tier = AccessoryTier.fromRarity(rarity);
        return tier == null ? 0 : tier.magicPower;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
