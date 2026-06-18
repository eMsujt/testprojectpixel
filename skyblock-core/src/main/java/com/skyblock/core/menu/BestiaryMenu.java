package com.skyblock.core.menu;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.BestiaryManager.BestiaryMob;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /bestiary}. Renders the player's bestiary progress
 * directly from {@link BestiaryManager}: a category overview showing aggregate
 * kill counts plus an overall milestone/family summary, and a per-category
 * detail view listing each mob's kill count, unlocked tier, and kills to the
 * next tier.
 */
public final class BestiaryMenu extends Menu {

    /** Inner content slots for the per-category detail view (mirrors {@link PetMenu}). */
    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    /** Slots for the six category tiles on the overview. */
    private static final int[] CATEGORY_SLOTS = {11, 13, 15, 29, 31, 33};

    private static final int SUMMARY_SLOT = 4;
    private static final int CLOSE_SLOT   = 49;
    private static final int BACK_SLOT    = 45;

    /** Lower-case mob key -> display name, for entries that map to a known {@link BestiaryMob}. */
    private static final Map<String, String> MOB_NAMES = new HashMap<>();

    public static final Map<BestiaryCategory, Material> CATEGORY_ICONS = new EnumMap<>(BestiaryCategory.class);

    static {
        for (BestiaryMob mob : BestiaryMob.values()) {
            MOB_NAMES.put(mob.mobKey, mob.displayName);
        }
        CATEGORY_ICONS.put(BestiaryCategory.COMBAT, Material.IRON_SWORD);
        CATEGORY_ICONS.put(BestiaryCategory.SLAYER, Material.DIAMOND_SWORD);
        CATEGORY_ICONS.put(BestiaryCategory.BOSS,   Material.NETHER_STAR);
        CATEGORY_ICONS.put(BestiaryCategory.NETHER, Material.NETHERRACK);
        CATEGORY_ICONS.put(BestiaryCategory.OCEAN,  Material.PRISMARINE_SHARD);
        CATEGORY_ICONS.put(BestiaryCategory.MINING, Material.IRON_PICKAXE);
    }

    private final UUID playerId;
    private final BestiaryCategory category;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public BestiaryMenu(Player player) {
        this(player.getUniqueId(), null);
    }

    public BestiaryMenu(UUID playerId) {
        this(playerId, null);
    }

    private BestiaryMenu(UUID playerId, BestiaryCategory category) {
        super(category == null ? "§2Bestiary" : "§2Bestiary §8» §a" + category.displayName, 6);
        this.playerId = playerId;
        this.category = category;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();
        inventory = Bukkit.createInventory(this, 54, getTitle());

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) inventory.setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) inventory.setItem(slot, pane);

        if (category == null) {
            buildOverview();
        } else {
            buildCategory();
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the bestiary.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    private void buildOverview() {
        BestiaryManager manager = BestiaryManager.getInstance();

        inventory.setItem(SUMMARY_SLOT, new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName("§aBestiary Milestones")
                .lore(
                        "§7Milestone level: §e" + manager.getMilestoneLevel(playerId),
                        "§7Families completed: §e" + manager.getCompletedFamilyCount(playerId)
                                + "§7/§e" + BestiaryFamily.values().length,
                        "§7Bonus Health: §a+" + String.format("%,.0f",
                                manager.getMilestoneStats(playerId).getOrDefault(
                                        com.skyblock.core.model.Stat.HEALTH, 0.0)) + " §c❤")
                .build());

        BestiaryCategory[] categories = BestiaryCategory.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            BestiaryCategory cat = categories[i];
            List<String> lore = new ArrayList<>();
            lore.add("§7Total kills: §e" + manager.getKillsForCategory(playerId, cat));
            for (BestiaryFamily family : cat.families) {
                lore.add("§8 • §7" + family.getDisplayName() + ": §f"
                        + manager.getKillsForFamily(playerId, family));
            }
            lore.add("");
            lore.add("§eClick to view mobs!");
            ItemStack tile = new ItemBuilder(CATEGORY_ICONS.getOrDefault(cat, Material.BOOK))
                    .displayName("§a" + cat.displayName)
                    .lore(lore)
                    .build();
            int slot = CATEGORY_SLOTS[i];
            inventory.setItem(slot, tile);
            BestiaryCategory clicked = cat;
            handlers.put(slot, e -> new BestiaryMenu(playerId, clicked).open((Player) e.getWhoClicked()));
        }
    }

    private void buildCategory() {
        BestiaryManager manager = BestiaryManager.getInstance();

        // Collect the distinct mob keys belonging to this category's families.
        List<String> mobKeys = new ArrayList<>();
        for (BestiaryFamily family : category.families) {
            for (String mobKey : family.mobTypes) {
                if (!mobKeys.contains(mobKey)) {
                    mobKeys.add(mobKey);
                }
            }
        }

        for (int i = 0; i < INNER_SLOTS.length && i < mobKeys.size(); i++) {
            String mobKey = mobKeys.get(i);
            int kills = manager.getKills(playerId, mobKey);
            int tier = manager.getTier(playerId, mobKey);
            int toNext = manager.getKillsToNextTier(playerId, mobKey);
            String name = MOB_NAMES.getOrDefault(mobKey, mobKey);
            inventory.setItem(INNER_SLOTS[i], new ItemBuilder(Material.BOOK)
                    .displayName((tier > 0 ? "§a" : "§f") + name)
                    .lore(
                            "§7Kills: §e" + kills,
                            "§7Tier: §6" + tier + "§7/§6" + BestiaryManager.MAX_TIER,
                            tier >= BestiaryManager.MAX_TIER
                                    ? "§aMaxed out!"
                                    : "§7Next tier in §e" + toNext + " §7kills")
                    .build());
        }

        inventory.setItem(BACK_SLOT, new ItemBuilder(Material.ARROW)
                .displayName("§eBack")
                .lore("§7Return to the category overview.")
                .build());
        handlers.put(BACK_SLOT, e -> new BestiaryMenu(playerId).open((Player) e.getWhoClicked()));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
