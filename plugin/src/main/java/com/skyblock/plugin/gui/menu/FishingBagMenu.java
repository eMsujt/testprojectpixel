package com.skyblock.plugin.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FishingBagMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final Player player;
    private final int page;

    public FishingBagMenu(Player player) {
        this(player, 0);
    }

    private FishingBagMenu(Player player, int page) {
        super("§3Fishing Bag", 5);
        this.player = player;
        this.page = page;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getFishingBagContents();

        int totalPages = Math.max(1, (int) Math.ceil((double) contents.size() / SLOTS_PER_PAGE));
        int start = page * SLOTS_PER_PAGE;

        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int contentIndex = start + i;
            if (contentIndex >= contents.size()) break;
            ItemStack item = contents.get(contentIndex);
            if (item != null) {
                setItem(INNER_SLOTS[i], item);
            }
        }

        if (contents.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cFishing Bag Empty")
                    .lore("§7Add fishing items to your bag.")
                    .build());
        }

        setItem(40, new ItemBuilder(Material.FISHING_ROD)
                .displayName("§3Fishing Bag")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            setItem(36, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build(),
                    event -> new FishingBagMenu(player, prevPage).open(player));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            setItem(44, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build(),
                    event -> new FishingBagMenu(player, nextPage).open(player));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 36; slot < 45; slot++) {
            setItem(slot, pane);
        }
    }
}
