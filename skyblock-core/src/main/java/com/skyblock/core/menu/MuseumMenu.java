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
 * The "Your Museum" hub, opened by {@code /museum}. Laid out 1:1 with Hypixel:
 * an overview head at slot 4, the eight donation categories at their documented
 * slots (Combat 20, Farming 21, Mining 22, Fishing 23, Foraging 24,
 * Dungeoneering 30, Hunting 31, Special Items 32), and Museum Milestones (48),
 * Search (50) and Appraisal Service (51) on the bottom row.
 */
public final class MuseumMenu extends Menu {

    private static final int SUMMARY_SLOT = 4;

    private static final Map<MuseumCategory, Integer> CATEGORY_SLOTS = new EnumMap<>(MuseumCategory.class);
    private static final Map<MuseumCategory, Material> CATEGORY_ICONS = new EnumMap<>(MuseumCategory.class);

    static {
        CATEGORY_SLOTS.put(MuseumCategory.COMBAT, 20);
        CATEGORY_SLOTS.put(MuseumCategory.FARMING, 21);
        CATEGORY_SLOTS.put(MuseumCategory.MINING, 22);
        CATEGORY_SLOTS.put(MuseumCategory.FISHING, 23);
        CATEGORY_SLOTS.put(MuseumCategory.FORAGING, 24);
        CATEGORY_SLOTS.put(MuseumCategory.DUNGEONEERING, 30);
        CATEGORY_SLOTS.put(MuseumCategory.HUNTING, 31);
        CATEGORY_SLOTS.put(MuseumCategory.SPECIAL, 32);

        CATEGORY_ICONS.put(MuseumCategory.COMBAT, Material.STONE_SWORD);
        CATEGORY_ICONS.put(MuseumCategory.FARMING, Material.GOLDEN_HOE);
        CATEGORY_ICONS.put(MuseumCategory.MINING, Material.STONE_PICKAXE);
        CATEGORY_ICONS.put(MuseumCategory.FISHING, Material.FISHING_ROD);
        CATEGORY_ICONS.put(MuseumCategory.FORAGING, Material.JUNGLE_SAPLING);
        CATEGORY_ICONS.put(MuseumCategory.DUNGEONEERING, Material.SKELETON_SKULL);
        CATEGORY_ICONS.put(MuseumCategory.HUNTING, Material.LEAD);
        CATEGORY_ICONS.put(MuseumCategory.SPECIAL, Material.CAKE);
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
        int total = manager.getTotalDonations(playerId);

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§aYour Museum")
                .lore(
                        "§7Items donated: §e" + total,
                        "§7Museum value: §6" + manager.getMuseumValue(playerId))
                .build(), e -> e.setCancelled(true));

        for (MuseumCategory category : MuseumCategory.values()) {
            Integer slot = CATEGORY_SLOTS.get(category);
            if (slot == null) continue;
            setItem(slot, buildCategoryTile(manager, category), e -> e.setCancelled(true));
        }

        DonationMilestone current = manager.getMilestone(playerId);
        DonationMilestone next = nextMilestone(current);
        List<String> mLore = new ArrayList<>();
        mLore.add("§7Milestone: §b" + current.name());
        if (next == null) {
            mLore.add("§aAll milestones unlocked!");
        } else {
            mLore.add("§7Next: §b" + next.name() + " §7(§e" + total + "§7/§e" + next.getThreshold() + "§7)");
        }
        setItem(48, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§aMuseum Milestones")
                .lore(mLore)
                .build(), e -> e.setCancelled(true));

        setItem(50, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aMuseum Search")
                .lore("§7Search for a museum item.")
                .build(), e -> e.setCancelled(true));
        setItem(51, new ItemBuilder(Material.DIAMOND)
                .displayName("§aMuseum Appraisal Service")
                .lore("§7Appraise your museum.")
                .build(), e -> e.setCancelled(true));
    }

    private ItemStack buildCategoryTile(MuseumManager manager, MuseumCategory category) {
        Set<String> donated = manager.getDonations(playerId, category);
        int size = manager.getCategorySize(category);
        int completion = size > 0 ? (int) Math.round((double) donated.size() / size * 100) : 0;

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
