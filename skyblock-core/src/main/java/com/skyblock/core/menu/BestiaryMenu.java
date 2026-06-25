package com.skyblock.core.menu;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.BestiaryManager.BestiaryMob;
import com.skyblock.core.util.SkyblockUtils;
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

    /** Slots for the island category tiles on the overview (3×3 grid). */
    private static final int[] CATEGORY_SLOTS = {11, 13, 15, 20, 22, 24, 29, 31, 33};

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
        CATEGORY_ICONS.put(BestiaryCategory.PRIVATE_ISLAND,  Material.GRASS_BLOCK);
        CATEGORY_ICONS.put(BestiaryCategory.HUB,             Material.POPPY);
        CATEGORY_ICONS.put(BestiaryCategory.SPIDERS_DEN,     Material.COBWEB);
        CATEGORY_ICONS.put(BestiaryCategory.THE_END,         Material.END_STONE);
        CATEGORY_ICONS.put(BestiaryCategory.CRIMSON_ISLE,    Material.NETHERRACK);
        CATEGORY_ICONS.put(BestiaryCategory.DWARVEN_MINES,   Material.IRON_INGOT);
        CATEGORY_ICONS.put(BestiaryCategory.CRYSTAL_HOLLOWS, Material.AMETHYST_CLUSTER);
        CATEGORY_ICONS.put(BestiaryCategory.CATACOMBS,       Material.WITHER_SKELETON_SKULL);
        CATEGORY_ICONS.put(BestiaryCategory.THE_OCEAN,       Material.PRISMARINE);
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
        super(category == null ? "Bestiary" : "Bestiary ➜ " + category.displayName, 6);
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

        ItemStack pane = SkyblockUtils.buildItem(Material.BLACK_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 9; slot++) inventory.setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) inventory.setItem(slot, pane);

        if (category == null) {
            buildOverview();
        } else {
            buildCategory();
        }

        inventory.setItem(CLOSE_SLOT, SkyblockUtils.buildItem(Material.BARRIER,
                "§cClose",
                "§7Close the bestiary."));
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        // This menu manages its own inventory, so it must fill the empty interior slots
        // itself (the base Menu does this for every other menu) — otherwise the middle is holey.
        for (int slot = 0; slot < 54; slot++) {
            if (inventory.getItem(slot) == null) inventory.setItem(slot, pane);
        }

        player.openInventory(inventory);
    }

    private void buildOverview() {
        BestiaryManager manager = BestiaryManager.getInstance();

        int totalFamilies = BestiaryFamily.values().length;
        int completed = manager.getCompletedFamilyCount(playerId);
        int completedPct = totalFamilies > 0 ? completed * 100 / totalFamilies : 0;
        inventory.setItem(SUMMARY_SLOT, SkyblockUtils.buildItem(Material.WRITABLE_BOOK,
                "§3Bestiary",
                "§7The Bestiary is a compendium of",
                "§7mobs in SkyBlock. It contains",
                "§7detailed information on loot",
                "§7drops, your mob stats, and more!",
                "",
                "§7Kill mobs within §aFamilies §7to",
                "§7progress and earn §arewards§7.",
                "",
                "§7Milestone level: §e" + manager.getMilestoneLevel(playerId),
                "§7Families Completed: §e" + completedPct + "%",
                bestiaryBar(completedPct) + " §b" + completed + "§3/§b" + totalFamilies));

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
            ItemStack tile = SkyblockUtils.buildItem(CATEGORY_ICONS.getOrDefault(cat, Material.BOOK),
                    "§a" + cat.displayName,
                    lore.toArray(new String[0]));
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
            inventory.setItem(INNER_SLOTS[i], SkyblockUtils.buildItem(Material.BOOK,
                    (tier > 0 ? "§a" : "§f") + name,
                    "§7Kills: §e" + kills,
                    "§7Tier: §6" + tier + "§7/§6" + BestiaryManager.MAX_TIER,
                    tier >= BestiaryManager.MAX_TIER
                            ? "§aMaxed out!"
                            : "§7Next tier in §e" + toNext + " §7kills"));
        }

        inventory.setItem(BACK_SLOT, SkyblockUtils.buildItem(Material.ARROW,
                "§eBack",
                "§7Return to the category overview."));
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

    /** Hypixel-style dashed progress bar: filled portion aqua, remainder white. */
    private static String bestiaryBar(int pct) {
        int filled = Math.round(pct / 100f * 20);
        return "§b" + "-".repeat(filled) + "§f" + "-".repeat(20 - filled);
    }
}
