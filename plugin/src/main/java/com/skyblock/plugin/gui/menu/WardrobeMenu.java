package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class WardrobeMenu extends Menu {

    private static final Material[] ARMOR_PIECES = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    };

    private static final String[] PIECE_NAMES = {
            "Helmet", "Chestplate", "Leggings", "Boots"
    };

    private final UUID playerId;

    public WardrobeMenu(Player player) {
        super("§6Wardrobe", 6);
        this.playerId = Objects.requireNonNull(player, "player").getUniqueId();
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

        PlayerProfile profile = ProfileManager.getInstance().getProfile(playerId);
        ItemStack[] contents = profile != null ? profile.getWardrobeContents() : null;

        for (int col = 1; col < 8; col++) {
            int setNumber = col;
            for (int row = 0; row < 4; row++) {
                int slot = (row + 1) * 9 + col;
                int contentIndex = (col - 1) * 4 + row;
                ItemStack stored = contents != null && contentIndex < contents.length
                        ? contents[contentIndex] : null;
                if (stored != null && stored.getType() != Material.AIR) {
                    setItem(slot, new ItemBuilder(stored)
                                    .build(),
                            event -> event.setCancelled(true));
                } else {
                    setItem(slot, new ItemBuilder(ARMOR_PIECES[row])
                                    .displayName("§aWardrobe Slot " + setNumber + " §7- " + PIECE_NAMES[row])
                                    .lore("§7Empty", "§eClick to equip!")
                                    .build(),
                            event -> event.setCancelled(true));
                }
            }
        }
    }
}
