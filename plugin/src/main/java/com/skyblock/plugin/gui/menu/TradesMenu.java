package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradesMenu extends Menu {

    private static final Trade[] TRADES = {
            new Trade(Material.WHEAT,        "§aFarmer John",    32, 40),
            new Trade(Material.COBBLESTONE,  "§7Miner Pete",     64, 96),
            new Trade(Material.OAK_LOG,      "§2Lumberjack Sam", 48, 60),
            new Trade(Material.COD,          "§bFisher Will",    16, 24),
            new Trade(Material.ROTTEN_FLESH, "§cHunter Greg",    80, 50),
    };

    private static final int[] TRADE_SLOTS = {20, 21, 22, 23, 24};

    public TradesMenu(Player player) {
        super("§6Trades", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < TRADES.length; i++) {
            Trade trade = TRADES[i];
            setItem(TRADE_SLOTS[i], new ItemBuilder(trade.material())
                    .displayName(trade.npc())
                    .lore("§7Gives: §f" + trade.give() + "x " + trade.material().name(),
                            "§7Receives: §6" + trade.receive() + " coins",
                            "",
                            "§eClick to trade!")
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }

    private record Trade(Material material, String npc, int give, int receive) {}
}
