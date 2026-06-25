package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 6-row GUI titled {@code §6Wardrobe}, laid out 1:1 with Hypixel: 18 armor sets
 * across 2 pages of 9, where each set is a vertical column showing its real
 * helmet/chestplate/leggings/boots (rows 1–4) with a "Slot N" header (row 0).
 * Clicking any piece — or the header — equips that set. The bottom row holds the
 * page navigation and a Go Back arrow.
 */
public final class WardrobeMenu extends AbstractSkyBlockMenu {

    private static final int SETS_PER_PAGE = 9;
    private static final int TOTAL_PAGES = 2;
    private static final String[] PIECE_LABELS = {"Helmet", "Chestplate", "Leggings", "Boots"};

    private final int page;

    public WardrobeMenu(Player player) {
        this(player, 0);
    }

    private WardrobeMenu(Player player, int page) {
        super(player, "§6Wardrobe", 6);
        this.page = page;
    }

    @Override
    protected void populate() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        WardrobeSlot[] slots = WardrobeSlot.values();
        String active = mgr.getActiveArmorSet(player.getUniqueId());
        int pageClamped = Math.max(0, Math.min(page, TOTAL_PAGES - 1));

        for (int col = 0; col < SETS_PER_PAGE; col++) {
            int index = pageClamped * SETS_PER_PAGE + col;
            if (index >= slots.length) {
                break;
            }
            WardrobeSlot slot = slots[index];
            boolean unlocked = mgr.isSlotUnlocked(player.getUniqueId(), slot);
            ItemStack[] armor = unlocked ? mgr.getOutfit(player.getUniqueId(), slot) : null;
            boolean isActive = slot.name().equals(active);
            boolean hasArmor = armor != null;

            // The whole column shares one action: equip this set (if it holds armor and isn't active).
            Consumer<InventoryClickEvent> equip = e -> {
                e.setCancelled(true);
                if (hasArmor && !isActive) {
                    mgr.equip(player.getUniqueId(), slot);
                    player.sendMessage("§aEquipped §6" + slot.getDisplayName() + "§a.");
                    new WardrobeMenu(player, pageClamped).open(player);
                }
            };

            // Header (row 0): slot number + equipped / empty / locked status.
            List<String> headLore = new ArrayList<>();
            if (!unlocked) {
                headLore.add("§7This slot is locked.");
            } else if (isActive) {
                headLore.add("§a§lEQUIPPED");
            } else if (hasArmor) {
                headLore.add("§eClick to equip this set.");
            } else {
                headLore.add("§7No outfit saved.");
                headLore.add("§8Use §7/wardrobe save " + slot.getSlotNumber() + "§8.");
            }
            Material headIcon = !unlocked ? Material.RED_STAINED_GLASS_PANE
                    : isActive ? Material.LIME_STAINED_GLASS_PANE
                    : hasArmor ? Material.YELLOW_STAINED_GLASS_PANE
                    : Material.GRAY_STAINED_GLASS_PANE;
            setItem(col, new ItemBuilder(headIcon)
                    .displayName((isActive ? "§a" : "§e") + slot.getDisplayName())
                    .lore(headLore.toArray(new String[0]))
                    .build(), equip);

            // Pieces (rows 1–4): the real helmet/chestplate/leggings/boots.
            for (int a = 0; a < 4; a++) {
                int pieceSlot = 9 + a * 9 + col;
                if (!unlocked) {
                    setItem(pieceSlot, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                            .displayName("§cLocked").build(), e -> e.setCancelled(true));
                } else if (hasArmor && armor[a] != null && armor[a].getType() != Material.AIR) {
                    setItem(pieceSlot, armor[a].clone(), equip);
                } else {
                    setItem(pieceSlot, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                            .displayName("§7Empty " + PIECE_LABELS[a] + " Slot").build(),
                            e -> e.setCancelled(true));
                }
            }
        }

        // Bottom row: page navigation + info + go back.
        int saved = mgr.getOutfitNames(player.getUniqueId()).size();
        setItem(49, new ItemBuilder(Material.ARMOR_STAND)
                .displayName("§6Wardrobe")
                .lore("§7Saved Outfits: §e" + saved + " §7/ §e" + WardrobeManager.MAX_OUTFITS,
                      "§7Active Set: " + (active != null ? "§6" + active : "§cNone"),
                      "§7Page: §e" + (pageClamped + 1) + "§7/§e" + TOTAL_PAGES)
                .build(), e -> e.setCancelled(true));

        if (pageClamped > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + pageClamped + "§7/§e" + TOTAL_PAGES)
                    .build(),
                    e -> { e.setCancelled(true); new WardrobeMenu(player, pageClamped - 1).open(player); });
        }
        if (pageClamped < TOTAL_PAGES - 1) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (pageClamped + 2) + "§7/§e" + TOTAL_PAGES)
                    .build(),
                    e -> { e.setCancelled(true); new WardrobeMenu(player, pageClamped + 1).open(player); });
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }
}
