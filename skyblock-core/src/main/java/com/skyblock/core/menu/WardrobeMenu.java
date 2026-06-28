package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * The Wardrobe, rebuilt 1:1 from the wiki {@code Wardrobe/UI}. Each of the 9
 * columns is one armor slot: rows 1–4 hold its helmet / chestplate / leggings /
 * boots, and row 5 is the slot's status indicator. Empty unlocked cells show the
 * slot's rainbow-coloured pane ("Place a … here"); locked slots show black panes
 * / a Rose-Red status ("Locked"). The bottom row is page navigation + Go Back.
 */
public final class WardrobeMenu extends AbstractSkyBlockMenu {

    private static final int SETS_PER_PAGE = 9;
    private static final int TOTAL_PAGES = 2;
    private static final String[] PIECE_LABELS = {"Helmet", "Chestplate", "Leggings", "Boots"};

    /** Per-slot pane colour (slot 1 = Red, slot 2 = Orange, …), like Hypixel's rainbow columns. */
    private static final Material[] SLOT_PANES = {
            Material.RED_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE
    };

    private final int page;

    public WardrobeMenu(Player player) {
        this(player, 0);
    }

    private WardrobeMenu(Player player, int page) {
        super(player, "Wardrobe", 6);
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
            int num = slot.getSlotNumber();
            boolean unlocked = mgr.isSlotUnlocked(player.getUniqueId(), slot);
            ItemStack[] armor = unlocked ? mgr.getOutfit(player.getUniqueId(), slot) : null;
            boolean isActive = slot.name().equals(active);
            boolean hasArmor = armor != null && hasAny(armor);
            Material pane = SLOT_PANES[(num - 1) % SLOT_PANES.length];

            Consumer<InventoryClickEvent> equip = e -> {
                e.setCancelled(true);
                if (hasArmor && !isActive) {
                    mgr.equip(player.getUniqueId(), slot);
                    player.sendMessage("§aEquipped §6" + slot.getDisplayName() + "§a.");
                    new WardrobeMenu(player, pageClamped).open(player);
                } else if (unlocked && !hasArmor) {
                    player.sendMessage("§7Save your worn armor here with §e/wardrobe save " + num + "§7.");
                }
            };

            // Rows 1–4: helmet / chestplate / leggings / boots.
            for (int a = 0; a < 4; a++) {
                int pieceSlot = a * 9 + col;
                if (!unlocked) {
                    setItem(pieceSlot, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                            .displayName("§7Slot " + num + ": §cLocked")
                            .lore("§7This wardrobe slot is locked and", "§7cannot be used.")
                            .build(), e -> e.setCancelled(true));
                } else if (hasArmor && armor[a] != null && armor[a].getType() != Material.AIR) {
                    setItem(pieceSlot, armor[a].clone(), equip);
                } else {
                    String piece = PIECE_LABELS[a];
                    setItem(pieceSlot, new ItemBuilder(pane)
                            .displayName("§aSlot " + num + " " + piece)
                            .lore("§7Place a " + piece.toLowerCase() + " here to add it", "§7to the armor set.")
                            .build(), equip);
                }
            }

            // Row 5: status indicator.
            int statusSlot = 36 + col;
            if (!unlocked) {
                setItem(statusSlot, new ItemBuilder(Material.RED_DYE)
                        .displayName("§7Slot " + num + ": §cLocked")
                        .lore("§7This wardrobe slot is locked and", "§7cannot be used.")
                        .build(), e -> e.setCancelled(true));
            } else if (isActive) {
                setItem(statusSlot, new ItemBuilder(Material.LIME_DYE)
                        .displayName("§7Slot " + num + ": §aEquipped")
                        .lore("§7This armor set is currently worn.", "", "§eClick to unequip!")
                        .build(), e -> {
                            e.setCancelled(true);
                            mgr.equip(player.getUniqueId(), slot); // toggle handled by manager/equip
                            new WardrobeMenu(player, pageClamped).open(player);
                        });
            } else if (hasArmor) {
                setItem(statusSlot, new ItemBuilder(Material.LIME_DYE)
                        .displayName("§7Slot " + num)
                        .lore("§7This wardrobe slot has an", "§7armor set saved.", "", "§eClick to equip!")
                        .build(), equip);
            } else {
                setItem(statusSlot, new ItemBuilder(Material.GRAY_DYE)
                        .displayName("§7Slot " + num + ": §cEmpty")
                        .lore("§7This wardrobe slot contains no", "§7armor.", "",
                                "§eClick to add the set you're", "§ewearing.")
                        .build(), equip);
            }
        }

        // Bottom row: page navigation + info + Go Back.
        int saved = mgr.getOutfitNames(player.getUniqueId()).size();
        setItem(49, new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .displayName("§aWardrobe")
                .lore("§7Saved Outfits: §e" + saved + " §7/ §e" + WardrobeManager.MAX_OUTFITS,
                        "§7Active Set: " + (active != null ? "§6" + active : "§cNone"),
                        "§7Page: §e" + (pageClamped + 1) + "§7/§e" + TOTAL_PAGES)
                .build(), e -> e.setCancelled(true));

        if (pageClamped > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§aPrevious Page")
                    .lore("§7Page " + pageClamped + "§7/§e" + TOTAL_PAGES)
                    .build(),
                    e -> { e.setCancelled(true); new WardrobeMenu(player, pageClamped - 1).open(player); });
        }
        if (pageClamped < TOTAL_PAGES - 1) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§aNext Page")
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

    private static boolean hasAny(ItemStack[] armor) {
        for (ItemStack piece : armor) {
            if (piece != null && piece.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }
}
