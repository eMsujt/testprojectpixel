package com.skyblock.core.menu;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Bank overview menu opened by {@code /bank} with no arguments.
 *
 * <p>Slot 11 is a gold-block "Deposit" button showing the player's purse
 * balance; slot 13 shows the personal bank balance as a gold-ingot item;
 * slot 15 is a chest "Withdraw" button. Top and bottom rows are gray-pane
 * borders.</p>
 */
public final class BankingMenu extends Menu {

    static final int DEPOSIT_SLOT = 11;
    static final int BALANCE_SLOT = 13;
    static final int WITHDRAW_SLOT = 15;

    private final UUID playerId;

    public BankingMenu(UUID playerId) {
        super("§6Bank", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        BankManager manager = BankManager.getInstance();
        double balance = manager.getBalance(playerId);
        long purse = manager.getPurseBalance(playerId);
        BankTier tier = manager.getTier(playerId);
        BankType type = manager.getBankType(playerId);

        setItem(DEPOSIT_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§aDeposit")
                .lore(
                        "§7Purse: §6" + purse + " coins",
                        "§7Click to deposit coins into your bank.")
                .build());

        setItem(BALANCE_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Personal Bank")
                .lore(
                        "§7Balance: §6" + String.format("%.2f", balance) + " coins",
                        "§7Tier: §e" + tier.getDisplayName(),
                        "§7Interest rate: §a" + tier.getInterestRate() + "%",
                        "§7Type: §b" + type.getDisplayName())
                .build());

        setItem(WITHDRAW_SLOT, new ItemBuilder(Material.CHEST)
                .displayName("§cWithdraw")
                .lore(
                        "§7Balance: §6" + String.format("%.2f", balance) + " coins",
                        "§7Click to withdraw coins into your purse.")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
