package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BankMenu extends Menu {

    private final Player player;

    public BankMenu(Player player) {
        super("§6Bank", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        setItem(13, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Balance")
                .lore(
                        "§7Balance: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins",
                        "",
                        "§eClick to withdraw all coins!")
                .build(), event -> {
            long amount = profile.getBank();
            if (amount > 0) {
                profile.setBank(0);
                profile.setPurse(profile.getPurse() + amount);
                open((Player) event.getWhoClicked());
            }
        });

        setItem(20, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore(
                        "Purse: §6" + String.format("%,.0f", (double) profile.getPurse()) + " Coins",
                        "",
                        "§eClick to deposit all coins!")
                .build(), event -> {
            long amount = profile.getPurse();
            if (amount > 0) {
                profile.setPurse(0);
                profile.setBank(profile.getBank() + amount);
                open((Player) event.getWhoClicked());
            }
        });
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
}
