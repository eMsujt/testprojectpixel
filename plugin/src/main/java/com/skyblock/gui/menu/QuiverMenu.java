package com.skyblock.gui.menu;

import com.skyblock.items.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuiverMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final Player player;
    private final int page;

    public QuiverMenu(Player player) {
        this(player, 0);
    }

    private QuiverMenu(Player player, int page) {
        super("§6Quiver", 6);
        this.player = player;
        this.page = page;
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getQuiverContents();
        if (contents == null) contents = new ItemStack[0];

        int totalPages = Math.max(1, (int) Math.ceil((double) contents.length / SLOTS_PER_PAGE));
        int start = page * SLOTS_PER_PAGE;

        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int contentIndex = start + i;
            if (contentIndex >= contents.length) break;
            ItemStack item = contents[contentIndex];
            if (item != null) {
                setItem(INNER_SLOTS[i], item);
            }
        }

        if (contents.length == 0) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cQuiver Empty")
                    .lore("§7Add arrows to your quiver.")
                    .build());
        }

        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§6Quiver")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build(),
                    event -> new QuiverMenu(player, prevPage).open(player));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build(),
                    event -> new QuiverMenu(player, nextPage).open(player));
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
