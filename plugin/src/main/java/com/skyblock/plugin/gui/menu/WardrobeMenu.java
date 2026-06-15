package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WardrobeMenu extends Menu {

    private static final Material[] ARMOR_PIECES = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    };

    private final Player player;

    public WardrobeMenu(Player player) {
        super("§eWardrobe", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack[]> sets = profile.getWardrobeSlots();

        for (int col = 0; col < 9; col++) {
            int slotNumber = col + 1;
            ItemStack[] set = col < sets.size() ? sets.get(col) : null;

            for (int row = 0; row < 4; row++) {
                ItemStack piece = set != null && row < set.length ? set[row] : null;
                boolean hasItem = piece != null && piece.getType() != Material.AIR;
                ItemStack icon = hasItem
                        ? new ItemBuilder(piece)
                                .displayName("§5Outfit " + slotNumber)
                                .lore("§7Click to equip!")
                                .build()
                        : new ItemBuilder(ARMOR_PIECES[row])
                                .displayName("§5Outfit " + slotNumber)
                                .lore("§7Empty", "§eClick to equip!")
                                .build();
                setItem((row + 1) * 9 + col, icon, event -> event.setCancelled(true));
            }
        }
    }
}
