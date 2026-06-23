package com.skyblock.core.menu;

import com.skyblock.core.manager.MuseumManager;
import com.skyblock.core.manager.MuseumManager.DonationMilestone;
import com.skyblock.core.manager.MuseumManager.MuseumCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical Museum hub menu opened by {@code /museum}. A 54-slot (6-row) chest
 * titled {@code §6Museum} rendering one tile per {@link MuseumCategory}
 * (Weapons/Armor/Rarities/Special) showing the player's donation grid — donated
 * count, completion, and the list of donated items — alongside a milestone
 * summary tile tracking reward progress toward the next {@link DonationMilestone}.
 */
public final class MuseumMenu extends Menu {

    private static final int SUMMARY_SLOT = 4;

    private static final int[] CATEGORY_SLOTS = {20, 21, 23, 24};

    private static final Map<MuseumCategory, Material> CATEGORY_ICONS =
            new EnumMap<>(MuseumCategory.class);

    static {
        CATEGORY_ICONS.put(MuseumCategory.WEAPONS,  Material.DIAMOND_SWORD);
        CATEGORY_ICONS.put(MuseumCategory.ARMOR,    Material.DIAMOND_CHESTPLATE);
        CATEGORY_ICONS.put(MuseumCategory.RARITIES, Material.NETHER_STAR);
        CATEGORY_ICONS.put(MuseumCategory.SPECIAL,  Material.DRAGON_EGG);
    }

    private final UUID playerId;

    public MuseumMenu(UUID playerId) {
        super("§6Your Museum", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        MuseumManager manager = MuseumManager.getInstance();
        setItem(SUMMARY_SLOT, buildSummary(manager), event -> event.setCancelled(true));

        MuseumCategory[] categories = MuseumCategory.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            MuseumCategory category = categories[i];
            setItem(CATEGORY_SLOTS[i],
                    buildCategoryTile(manager, category),
                    event -> event.setCancelled(true));
        }
    }

    private ItemStack buildSummary(MuseumManager manager) {
        int total = manager.getTotalDonations(playerId);
        DonationMilestone current = manager.getMilestone(playerId);
        DonationMilestone next = nextMilestone(current);

        List<String> lore = new ArrayList<>();
        lore.add("§7Items donated: §e" + total);
        lore.add("§7Museum value: §6" + manager.getMuseumValue(playerId));
        lore.add("§7Milestone: §b" + current.name());
        if (next == null) {
            lore.add("§aAll milestones unlocked!");
        } else {
            lore.add("§7Next: §b" + next.name() + " §7(§e" + total + "§7/§e"
                    + next.getThreshold() + "§7)");
        }
        return new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName("§aMuseum Milestones")
                .lore(lore)
                .build();
    }

    private ItemStack buildCategoryTile(MuseumManager manager, MuseumCategory category) {
        Set<String> donated = manager.getDonations(playerId, category);
        int size = manager.getCategorySize(category);
        int completion = (int) Math.round(manager.getCategoryCompletion(playerId, category) * 100);

        List<String> lore = new ArrayList<>();
        lore.add("§7Donated: §e" + donated.size() + "§7/§e" + size);
        lore.add("§7Completion: §a" + completion + "%");
        lore.add("");
        if (donated.isEmpty()) {
            lore.add("§8No items donated yet.");
        } else {
            for (String item : donated) {
                lore.add("§8 • §f" + item);
            }
        }
        return new ItemBuilder(CATEGORY_ICONS.getOrDefault(category, Material.BOOK))
                .displayName("§a" + category.getDisplayName())
                .lore(lore)
                .build();
    }

    private static DonationMilestone nextMilestone(DonationMilestone current) {
        DonationMilestone[] values = DonationMilestone.values();
        int index = current.ordinal() + 1;
        return index < values.length ? values[index] : null;
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
