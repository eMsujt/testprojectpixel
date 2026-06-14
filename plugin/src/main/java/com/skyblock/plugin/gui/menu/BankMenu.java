package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Bank Account menu.
 *
 * <p>A 54-slot (6-row) chest GUI with a gray glass-pane border. Slot 13 holds
 * a gold block displaying the player's current bank balance. Gold nuggets fill
 * the remaining inner content area as placeholders, matching Hypixel's layout.</p>
 */
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
        long balance = profile.getBank();

        setItem(13, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Bank Account")
                .lore(
                        "§7Balance: §6" + String.format("%,.0f", (double) balance) + " Coins",
                        "",
                        "§eClick to deposit or withdraw!")
                .build());

        ItemStack nugget = new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§r")
                .build();
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                int slot = row * 9 + col;
                if (slot != 13) {
                    setItem(slot, nugget);
                }
            }
        }
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
