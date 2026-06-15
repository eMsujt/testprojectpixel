package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Bank menu.
 *
 * <p>A 36-slot (4-row) chest GUI titled {@code §6Bank} with a gray glass-pane
 * border. Slot 11 shows the player's bank balance (gold ingot) and slot 15
 * shows their purse (gold nugget).</p>
 */
public class BankMenu extends Menu {

    private final Player player;

    public BankMenu(Player player) {
        super("§6Bank", 4);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        setItem(11, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Bank Account")
                .lore(
                        "§7Balance: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins",
                        "",
                        "§eClick to deposit or withdraw!")
                .build());

        setItem(15, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§ePurse")
                .lore(
                        "§7Coins: §6" + String.format("%,.0f", (double) profile.getPurse()),
                        "",
                        "§7The coins you carry with you.")
                .build());
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 36; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 27 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
