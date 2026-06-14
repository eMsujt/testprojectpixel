package com.skyblock.plugin.menus;

import com.skyblock.plugin.economy.BankManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BankMenu extends Menu {

    private static final int PURSE_SLOT = 22;
    private static final int BANK_SLOT  = 31;

    private final Player player;

    public BankMenu(Player player) {
        super("§6Bank & Purse", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        BankManager bank = BankManager.getInstance();

        setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore("§7Coins: §6" + bank.getPurse(id))
                .build());

        setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Bank")
                .lore("§7Bank Balance: §6" + bank.getBank(id))
                .build());
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
