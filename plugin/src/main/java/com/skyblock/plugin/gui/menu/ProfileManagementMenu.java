package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ProfileManagementMenu extends Menu {

    private final Player player;

    public ProfileManagementMenu(Player player) {
        super("§bProfile Management", 3);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName("§b" + player.getName());
            meta.setLore(java.util.Arrays.asList(
                    "§7Purse: §6" + String.format("%,.0f", (double) profile.getPurse()) + " Coins",
                    "§7Bank: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins",
                    "",
                    "§eClick to manage!"
            ));
            skull.setItemMeta(meta);
        }
        setItem(13, skull, e -> e.setCancelled(true));
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 18; slot < 27; slot++) {
            setItem(slot, pane);
        }
    }
}
