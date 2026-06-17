package com.skyblock.core.menu;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Bestiary hub menu. A 54-slot (6-row) chest titled
 * {@code §6Bestiary} showing one icon per {@link BestiaryCategory}, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Each category icon displays the
 * player's total kills for that category; clicking opens a
 * {@link BestiaryCategoryMenu} listing every mob in the category.
 *
 * <p>A summary book at slot 4 shows the player's overall milestone level, the
 * number of completed families, and the bonus health earned. All figures are
 * read directly from the canonical {@link BestiaryManager}.</p>
 */
public final class BestiaryMenu extends Menu {

    private static final int[] CATEGORY_SLOTS = {19, 20, 21, 22, 23, 24};

    private static final Material[] CATEGORY_ICONS = {
        Material.IRON_SWORD,     // COMBAT
        Material.DIAMOND_SWORD,  // SLAYER
        Material.DRAGON_HEAD,    // BOSS
        Material.NETHERRACK,     // NETHER
        Material.PRISMARINE,     // OCEAN
        Material.IRON_PICKAXE,   // MINING
    };

    private final UUID playerId;

    public BestiaryMenu(UUID playerId) {
        super("§6Bestiary", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        BestiaryManager manager = BestiaryManager.getInstance();

        setItem(4, new ItemBuilder(Material.BOOK)
                .displayName("§aBestiary Milestones")
                .lore(
                        "§7Milestone level: §e" + manager.getMilestoneLevel(playerId),
                        "§7Families completed: §e" + manager.getCompletedFamilyCount(playerId),
                        "§7Bonus health: §c+" + (int) (double) manager.getMilestoneStats(playerId)
                                .getOrDefault(com.skyblock.core.model.Stat.HEALTH, 0.0) + "❤")
                .build());

        BestiaryCategory[] categories = BestiaryCategory.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            BestiaryCategory category = categories[i];
            int total = manager.getKillsForCategory(playerId, category);
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .displayName("§a" + category.displayName)
                    .lore(
                            "§7Total kills: §e" + total,
                            "§7View your " + category.displayName.toLowerCase() + " bestiary.")
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        new BestiaryCategoryMenu(playerId, category).open((Player) event.getWhoClicked());
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
