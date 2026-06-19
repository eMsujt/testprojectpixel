package com.skyblock.core.menu;

import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.manager.AccessoryBagManager.AccessoryTier;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager.TalismanType;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * GUI menu opened by {@code /accessories}. Renders all {@value AccessoryBagManager#MAX_SLOTS}
 * accessory slots as iron ingots labelled with the accessory's
 * {@link AccessoryRarity rarity}; unlocked-but-empty and still-locked slots show
 * placeholders. The summary head at the top reports slot usage and magic power.
 */
public final class AccessoryBagMenu extends Menu {

    static final int SUMMARY_SLOT = 4;

    /** First inner slot; the 45 accessory slots fill rows 2–6 (slots 9–53). */
    private static final int FIRST_SLOT = 9;

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

        AccessoryBagManager manager = AccessoryBagManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§dAccessory Bag")
                .lore(
                        "§7Slots: §a" + manager.getSize(playerId) + "§7/§a" + manager.getUnlockedSlots(playerId),
                        "§7Magic Power: §d" + manager.getTotalMagicPower(playerId))
                .build());

        List<TalismanType> owned = new ArrayList<>(manager.getContents(playerId));
        int unlocked = manager.getUnlockedSlots(playerId);

        for (int i = 0; i < AccessoryBagManager.MAX_SLOTS; i++) {
            int slot = FIRST_SLOT + i;
            if (i >= unlocked) {
                setItem(slot, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .displayName("§cLocked Slot")
                        .lore("§7Unlock more slots through", "§7SkyBlock progression.")
                        .build());
            } else if (i < owned.size()) {
                TalismanType type = owned.get(i);
                String color = ItemBuilder.rarityColor(type.rarity.name()).toString();
                setItem(slot, new ItemBuilder(Material.IRON_INGOT)
                        .displayName(color + type.name())
                        .lore(
                                "§7Rarity: " + color + type.rarity.getDisplayName(),
                                "§7Bonus: §a+" + (int) type.bonus + " " + type.stat.getDisplayName(),
                                "§7Magic Power: §d" + magicPower(type.rarity))
                        .build());
            } else {
                setItem(slot, new ItemBuilder(Material.IRON_INGOT)
                        .displayName("§7Empty Slot")
                        .lore("§7Add an accessory to your bag.")
                        .build());
            }
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
