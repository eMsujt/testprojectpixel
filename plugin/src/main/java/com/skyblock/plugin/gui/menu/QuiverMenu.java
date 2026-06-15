package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuiverMenu extends Menu {

    private final Player player;

    public QuiverMenu(Player player) {
        super("§eQuiver", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getQuiverContents();

        int itemIndex = 0;
        for (int slot = 9; slot < 45 && itemIndex < contents.size(); slot++) {
            ItemStack item = contents.get(itemIndex++);
            if (item != null) {
                setItem(slot, item);
            }
        }

        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§eQuiver")
                .lore("§7Stores your arrows.")
                .build());
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
