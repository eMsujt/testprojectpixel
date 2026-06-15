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

    private static final int[] WARDROBE_SLOTS = {10, 13, 16, 19, 22, 25, 28, 31, 34};

    private final UUID playerId;

    public WardrobeMenu(Player player) {
        super("§aWardrobe", 6);
        this.playerId = Objects.requireNonNull(player, "player").getUniqueId();
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getProfile(playerId);
        ItemStack[] contents = profile != null ? profile.getWardrobeContents() : null;

        for (int i = 0; i < WARDROBE_SLOTS.length; i++) {
            int setNumber = i + 1;
            int contentIndex = i * 4;
            boolean hasContent = contents != null
                    && contentIndex < contents.length
                    && contents[contentIndex] != null
                    && contents[contentIndex].getType() != Material.AIR;
            ItemStack icon = hasContent
                    ? new ItemBuilder(contents[contentIndex])
                            .displayName("§6Wardrobe Set " + setNumber)
                            .lore("§7Click to equip!")
                            .build()
                    : new ItemBuilder(Material.LEATHER_CHESTPLATE)
                            .displayName("§6Wardrobe Set " + setNumber)
                            .lore("§7Empty", "§eClick to equip!")
                            .build();
            setItem(WARDROBE_SLOTS[i], icon, event -> event.setCancelled(true));
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
