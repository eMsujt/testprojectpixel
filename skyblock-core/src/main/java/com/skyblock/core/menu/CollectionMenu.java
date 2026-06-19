package com.skyblock.core.menu;

import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CollectionMenu extends Menu {

    private final Player player;

    public CollectionMenu(Player player) {
        super("§aCollections", 6);
        this.player = Objects.requireNonNull(player, "player");
    }

    @Override
    protected void build() {
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
