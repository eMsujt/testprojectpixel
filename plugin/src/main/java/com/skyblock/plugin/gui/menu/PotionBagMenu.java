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
 * The Potion Bag menu.
 *
 * <p>A 36-slot (4-row) chest titled {@code §dPotion Bag} with a gray glass-pane
 * border. The 14 inner slots (10–16 and 19–25) are populated from the owning
 * {@link SkyBlockProfile}'s potion bag contents, one stack per slot.</p>
 */
public class PotionBagMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
    };

    private final Player player;

    public PotionBagMenu(Player player) {
        super("§dPotion Bag", 4);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getPotionBagContents();

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
        for (int slot = 0; slot < 36; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 27 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
