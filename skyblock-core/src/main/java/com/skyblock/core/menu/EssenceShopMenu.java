package com.skyblock.core.menu;

import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * A single essence's perk shop (e.g. "Undead Essence Shop"), opened from the
 * {@link EssenceMenu} hub. Lists the {@link EssenceShopPerk}s for {@link #type}
 * (or all perks when {@code type} is null); clicking one purchases the next
 * level via {@link EssenceShopManager}. A Go Back arrow returns to the hub.
 */
public final class EssenceShopMenu extends Menu {

    private static final int BACK_SLOT = 48;
    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;
    private final EssenceType type;

    public EssenceShopMenu(Player player) {
        this(player.getUniqueId(), null);
    }

    public EssenceShopMenu(UUID playerId) {
        this(playerId, null);
    }

    public EssenceShopMenu(Player player, EssenceType type) {
        this(player.getUniqueId(), type);
    }

    public EssenceShopMenu(UUID playerId, EssenceType type) {
        super(type == null ? "Essence Shop" : type.getDisplayName() + " Essence Shop", 6);
        this.playerId = playerId;
        this.type = type;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            int row = slot / 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        EssenceShopManager shop = EssenceShopManager.getInstance();

        // Place this essence's perks starting at slot 10, left-to-right.
        int innerStart = 10;
        int i = 0;
        for (EssenceShopPerk perk : shop.getAvailablePerks()) {
            if (type != null && perk.getEssenceType() != type) {
                continue;
            }
            int col = i % 7;
            int row = i / 7;
            int slot = innerStart + row * 9 + col;
            setItem(slot, buildPerkItem(shop, perk), e -> handlePurchase(e, perk));
            i++;
        }

        if (type != null) {
            setItem(BACK_SLOT, new ItemBuilder(Material.ARROW)
                    .displayName("§aGo Back")
                    .lore("§7To Essence Shop")
                    .build());
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
        if (event.getSlot() == BACK_SLOT && type != null) {
            if (event.getWhoClicked() instanceof Player p) {
                new EssenceMenu(p).open(p);
            }
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
