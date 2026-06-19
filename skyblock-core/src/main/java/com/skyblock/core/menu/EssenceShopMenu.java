package com.skyblock.core.menu;

import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 6-row GUI listing all {@link EssenceShopPerk} upgrades purchasable through
 * the Essence Shop. Each perk occupies a single slot in the inner area; clicking
 * attempts the purchase via {@link EssenceShopManager}.
 */
public final class EssenceShopMenu extends Menu {

    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;

    public EssenceShopMenu(Player player) {
        this(player.getUniqueId());
    }

    public EssenceShopMenu(UUID playerId) {
        super("§5Essence Shop", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            int row = slot / 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        EssenceShopManager shop = EssenceShopManager.getInstance();
        EssenceShopPerk[] perks = shop.getAvailablePerks();

        // Place perks starting at slot 10 (row 1, col 1), left-to-right.
        int innerStart = 10;
        for (int i = 0; i < perks.length; i++) {
            EssenceShopPerk perk = perks[i];
            int col = i % 7;
            int row = i / 7;
            int slot = innerStart + row * 9 + col;
            setItem(slot, buildPerkItem(shop, perk), e -> handlePurchase(e, perk));
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the Essence Shop.")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getSlot() == CLOSE_SLOT) {
            if (event.getWhoClicked() instanceof Player p) p.closeInventory();
            return;
        }
        super.handleClick(event);
    }

    private void handlePurchase(InventoryClickEvent event, EssenceShopPerk perk) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        EssenceShopManager shop = EssenceShopManager.getInstance();
        if (shop.purchasePerk(playerId, perk)) {
            int newLevel = shop.getPerkLevel(playerId, perk);
            player.sendMessage("§aUpgraded §5" + perk.getDisplayName()
                    + " §ato level §f" + newLevel + "§a.");
        } else if (shop.getPerkLevel(playerId, perk) >= perk.getMaxLevel()) {
            player.sendMessage("§e" + perk.getDisplayName() + " is already maxed.");
        } else {
            player.sendMessage("§cNot enough §5" + perk.getEssenceType().getDisplayName()
                    + " Essence §cto upgrade §5" + perk.getDisplayName() + "§c.");
        }
        open(player);
    }

    private ItemStack buildPerkItem(EssenceShopManager shop, EssenceShopPerk perk) {
        int level = shop.getPerkLevel(playerId, perk);
        boolean maxed = level >= perk.getMaxLevel();
        ItemBuilder builder = new ItemBuilder(Material.ENCHANTED_BOOK)
                .displayName((maxed ? "§a" : "§5") + perk.getDisplayName())
                .addLore("§7Level: §a" + level + " §7/ §a" + perk.getMaxLevel())
                .addLore("§7Essence: §5" + perk.getEssenceType().getDisplayName());
        if (maxed) {
            builder.addLore("§a§lMAXED");
        } else {
            int cost = perk.getUpgradeCost(level);
            boolean canAfford = shop.canAfford(playerId, perk);
            builder.addLore((canAfford ? "§7" : "§c") + "Upgrade cost: §5"
                    + String.format("%,d", cost) + " Essence");
            builder.addLore(canAfford ? "§eClick to upgrade" : "§cInsufficient Essence");
        }
        return builder.build();
    }
}
