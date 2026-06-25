package com.skyblock.core.menu;

import com.skyblock.core.collections.gui.CollectionCategoryMenu;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Collections" menu, opened from the SkyBlock Menu. Laid out 1:1 with Hypixel:
 * a Collection summary (Painting) at slot 4, the Farming/Mining/Combat/Foraging/
 * Fishing categories at slots 20–24 with their vanilla icons, the Boss and Rift
 * categories at 31–32, and a Go Back arrow at slot 48. Clicking a category opens
 * its {@link CollectionCategoryMenu}.
 */
public final class CollectionsMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§eCollections";

    public CollectionsMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, pane);

        CollectionManager manager = CollectionManager.getInstance();
        UUID uuid = player.getUniqueId();

        // Summary (Painting) at slot 4 (wiki 1,5).
        int unlocked = 0;
        int totalColls = 0;
        for (CollectionCategory cat : CollectionCategory.values()) {
            for (Collection c : cat.getCollections()) {
                totalColls++;
                if (manager.getTier(uuid, c) > 0) unlocked++;
            }
        }
        int pct = totalColls > 0 ? unlocked * 100 / totalColls : 0;
        setItem(4, new ItemBuilder(Material.PAINTING)
                .displayName("§aCollection")
                .lore(
                        "§7View all of the items you have",
                        "§7collected in SkyBlock.",
                        "",
                        "§7Collections Unlocked: §e" + pct + "§6%",
                        bar(pct) + " §e" + unlocked + "§6/§e" + totalColls)
                .build(), e -> e.setCancelled(true));

        // Real categories at their Hypixel slots, each opening its category menu.
        for (CollectionCategory cat : CollectionCategory.values()) {
            int catTotal = 0;
            int catUnlocked = 0;
            for (Collection c : cat.getCollections()) {
                catTotal++;
                if (manager.getTier(uuid, c) > 0) catUnlocked++;
            }
            int catPct = catTotal > 0 ? catUnlocked * 100 / catTotal : 0;
            setItem(slotFor(cat), new ItemBuilder(iconFor(cat))
                    .displayName("§a" + cat.getDisplayName() + " Collections")
                    .lore(
                            "§7View your " + cat.getDisplayName() + " Collections!",
                            "",
                            "§7Collections Unlocked: §e" + catPct + "§6%",
                            bar(catPct) + " §e" + catUnlocked + "§6/§e" + catTotal,
                            "",
                            "§eClick to view!")
                    .build(),
                    e -> { e.setCancelled(true); new CollectionCategoryMenu(uuid, cat).open(player); });
        }

        // Boss + Rift sit at their Hypixel slots; no backing collections yet.
        setItem(31, new ItemBuilder(Material.WITHER_SKELETON_SKULL)
                .displayName("§5Boss Collections")
                .lore("§7Track items from SkyBlock bosses.", "", "§cComing soon.").build(),
                e -> e.setCancelled(true));
        setItem(32, new ItemBuilder(Material.MYCELIUM)
                .displayName("§aRift Collections")
                .lore("§7Track your Rift Collections.", "", "§cComing soon.").build(),
                e -> e.setCancelled(true));

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    private static String bar(int pct) {
        // Hypixel-style dashed bar: filled portion green, remainder white.
        int filled = Math.round(pct / 100f * 20);
        return "§a" + "-".repeat(filled) + "§f" + "-".repeat(20 - filled);
    }

    private static int slotFor(CollectionCategory cat) {
        switch (cat) {
            case FARMING:  return 20;
            case MINING:   return 21;
            case COMBAT:   return 22;
            case FORAGING: return 23;
            case FISHING:  return 24;
            default:       return 25;
        }
    }

    private static Material iconFor(CollectionCategory cat) {
        switch (cat) {
            case FARMING:  return Material.GOLDEN_HOE;
            case MINING:   return Material.STONE_PICKAXE;
            case COMBAT:   return Material.STONE_SWORD;
            case FORAGING: return Material.JUNGLE_SAPLING;
            case FISHING:  return Material.FISHING_ROD;
            default:       return Material.PAPER;
        }
    }
}
