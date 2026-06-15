package com.skyblock.plugin.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ProfileStatsMenu extends Menu {

    private final Player player;

    public ProfileStatsMenu(Player player) {
        super("§aYour SkyBlock Profile", 6);
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
            meta.setDisplayName("§a" + player.getName());
            meta.setLore(java.util.Arrays.asList(
                    "§7Purse: §6" + String.format("%,.0f", (double) profile.getPurse()) + " Coins",
                    "§7Bank: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins"
            ));
            skull.setItemMeta(meta);
        }
        setItem(13, skull, e -> e.setCancelled(true));
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
