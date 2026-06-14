package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * The top-level SkyBlock menu (opened via {@code /sbmenu}).
 *
 * <p>A 54-slot (6-row) menu laid out to match Hypixel's exact slot positions:
 * the player's profile head sits in the centre of the top region, with the
 * feature icons (Skills, Collection, Recipe Book, Trades, Fast Travel, Calendar
 * and Events) arranged across the two middle rows.</p>
 */
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
        super("SkyBlock Menu", 6);
    }

    @Override
    protected void build() {
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
}
