package com.skyblock.plugin.gui.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead.
 */
@Deprecated
public class SkyBlockMenu extends Menu {

    /** Centre slot holding the player's SkyBlock profile head. */
    private static final int PROFILE_SLOT = 13;

    /** Slots for the feature icons, matching Hypixel's layout. */
    private static final int SKILLS_SLOT = 20;
    private static final int COLLECTION_SLOT = 22;
    private static final int RECIPE_BOOK_SLOT = 24;
    private static final int TRADES_SLOT = 29;
    private static final int FAST_TRAVEL_SLOT = 31;
    private static final int CALENDAR_SLOT = 33;

    public SkyBlockMenu() {
        super("§6SkyBlock Menu", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(PROFILE_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§aYour SkyBlock Profile")
                .lore("§7View your profile statistics.")
                .build());

        setItem(SKILLS_SLOT, new ItemBuilder(Material.DIAMOND_SWORD)
                .displayName("§aYour Skills")
                .lore("§7View your skill progress.")
                .build(),
                event -> new SkillsMenu(event.getWhoClicked().getUniqueId())
                        .open((Player) event.getWhoClicked()));

        setItem(COLLECTION_SLOT, new ItemBuilder(Material.CHEST)
                        .displayName("§aCollection")
                        .lore("§7View your collections.")
                        .build(),
                event -> new CollectionsMenu().open((Player) event.getWhoClicked()));

        setItem(RECIPE_BOOK_SLOT, new ItemBuilder(Material.CRAFTING_TABLE)
                .displayName("§aRecipe Book")
                .lore("§7Browse craftable recipes.")
                .build());

        setItem(TRADES_SLOT, new ItemBuilder(Material.EMERALD)
                .displayName("§aTrades")
                .lore("§7Trade with NPCs.")
                .build());

        setItem(FAST_TRAVEL_SLOT, new ItemBuilder(Material.ENDER_PEARL)
                .displayName("§aFast Travel")
                .lore("§7Warp around the world.")
                .build(),
                event -> new FastTravelMenu().open((Player) event.getWhoClicked()));

        setItem(CALENDAR_SLOT, new ItemBuilder(Material.CLOCK)
                .displayName("§aCalendar and Events")
                .lore("§7View upcoming events.")
                .build());
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
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
