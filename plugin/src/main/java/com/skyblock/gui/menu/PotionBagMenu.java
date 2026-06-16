package com.skyblock.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PotionBagMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final Player player;

    public PotionBagMenu(Player player) {
        super("§5Potion Bag", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getPotionBagContents();
        if (contents == null) contents = new ItemStack[0];

        for (int i = 0; i < INNER_SLOTS.length && i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null) {
                setItem(INNER_SLOTS[i], item);
            }
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
