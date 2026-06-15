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
        super("§6Bank Account", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        setItem(1, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Bank Account")
                .lore(
                        "§7Balance: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins",
                        "",
                        "§eClick to deposit or withdraw!")
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
