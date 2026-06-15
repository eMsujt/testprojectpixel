package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ProfileManagementMenu extends Menu {

    private static final int MAX_PROFILES = 5;
    private static final int[] PROFILE_SLOTS = {20, 21, 22, 23, 24};

    private final Player player;

    public ProfileManagementMenu(Player player) {
        super("§aProfile Management", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        // Slot 0: active profile (player head)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName("§a" + player.getName());
            meta.setLore(Arrays.asList(
                    "§7Purse: §6" + String.format("%,.0f", (double) profile.getPurse()) + " Coins",
                    "§7Bank: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins",
                    "",
                    "§aCurrently selected"
            ));
            skull.setItemMeta(meta);
        }
        setItem(PROFILE_SLOTS[0], skull, e -> e.setCancelled(true));

        // Slots 1-4: empty profile slots
        ItemStack empty = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .displayName("§7[Empty]")
                .lore("§eClick to create a new profile!")
                .build();
        for (int i = 1; i < MAX_PROFILES; i++) {
            setItem(PROFILE_SLOTS[i], empty, e -> e.setCancelled(true));
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
}
