package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Accessory Bag menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §5Accessory Bag} with a gray glass-pane
 * border on rows 0 and 5. The 28 inner slots (10–16, 19–25, 28–34 and 37–43) are
 * populated from the owning {@link SkyBlockProfile}'s accessory bag contents.</p>
 */
public class AccessoryBagMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final Player player;

    public AccessoryBagMenu(Player player) {
        super("§5Accessory Bag", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getAccessoryBagContents();

        for (int i = 0; i < INNER_SLOTS.length && i < contents.size(); i++) {
            ItemStack item = contents.get(i);
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
