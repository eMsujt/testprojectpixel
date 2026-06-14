package com.skyblock.plugin.menu;

import com.skyblock.plugin.economy.BankManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BankMenu extends Menu {

    private final UUID playerId;

    public BankMenu(UUID playerId) {
        super("§6Bank", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        long purse = BankManager.getInstance().getPurse(playerId);
        setItem(4, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§ePurse")
                .lore("§7Coins: §6" + purse)
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
