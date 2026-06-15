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

    private static final int[] WARDROBE_SLOTS = {10, 13, 16, 19, 22, 25, 28, 31, 34};

    private final Player player;

    public WardrobeMenu(Player player) {
        super("§6Wardrobe", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack[]> sets = profile.getWardrobeSlots();

        for (int i = 0; i < WARDROBE_SLOTS.length; i++) {
            int setNumber = i + 1;
            ItemStack[] set = i < sets.size() ? sets.get(i) : null;
            ItemStack chestplate = set != null && set.length > 1 ? set[1] : null;
            boolean hasContent = chestplate != null && chestplate.getType() != Material.AIR;
            ItemStack icon = hasContent
                    ? new ItemBuilder(chestplate)
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
