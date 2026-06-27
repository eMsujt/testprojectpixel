package com.skyblock.core.menu;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment.*;

/**
 * The real Hypixel "Enchant Item" interface: place an item in the input slot
 * (slot 20) and the enchantments applicable to that item type appear as tiles;
 * clicking one applies/upgrades it on the item, written into the item's lore as
 * {@code §9<Enchant> <Roman>} (the format {@code CombatListener} reads, so the
 * combat enchants actually take effect). The bottom row carries Bookshelf Power
 * (49), the Enchantments Guide (51) and a Sort button (52).
 *
 * <p>Dupe-safety follows {@link ReforgeMenu}: the input slot is the only
 * interactive slot, it is cleared after the background fill in {@link #open}, and
 * {@link #onClose} returns any item left in it.</p>
 */
public final class EnchantingMenu extends AbstractSkyBlockMenu {

    static final int ITEM_SLOT = 20;          // 3,2 — input item (interactive)
    private static final int PROMPT_SLOT = 24; // 3,6 — Gray Dye "place an item" prompt
    private static final int TABLE_SLOT = 29;  // 4,2 — Enchantment Table label
    private static final int BOOKSHELF_SLOT = 49;
    private static final int GUIDE_SLOT = 51;
    private static final int SORT_SLOT = 52;

    /** Slots the applicable-enchant tiles fill (avoids the input column + frame). */
    private static final int[] ENCHANT_SLOTS = {
            12, 13, 14, 15, 16,
            21, 22, 23, 24, 25,
            30, 31, 32, 33, 34,
            39, 40, 41, 42, 43
    };

    // Which enchants apply to which item category (approximate SkyBlock grouping).
    private static final Set<SkyBlockEnchantment> WEAPON = EnumSet.of(
            SHARPNESS, CRITICAL, SMITE, BANE_OF_ARTHROPODS, FIRST_STRIKE, GIANT_KILLER, ENDER_SLAYER,
            CUBISM, DRAGON_HUNTER, THUNDERLORD, VAMPIRISM, LIFE_STEAL, LETHALITY, EXECUTE, PROSECUTE,
            OVERLOAD, LOOTING, FIRE_ASPECT, KNOCKBACK, LUCK, CHANCE, SCAVENGER, SOUL_EATER, VENOMOUS,
            VICIOUS, SHREDDER, TELEKINESIS);
    private static final Set<SkyBlockEnchantment> BOW = EnumSet.of(
            POWER, PUNCH, FLAME);
    private static final Set<SkyBlockEnchantment> ARMOR = EnumSet.of(
            PROTECTION, THORNS, GROWTH, FEATHER_FALLING, SUGAR_RUSH, REJUVENATE, TELEKINESIS);
    private static final Set<SkyBlockEnchantment> TOOL = EnumSet.of(
            EFFICIENCY, FORTUNE, SILK_TOUCH, SMELTING_TOUCH, MAGNET, SCAVENGER, TELEKINESIS);
    private static final Set<SkyBlockEnchantment> HOE = EnumSet.of(
            CULTIVATING, GREEN_THUMB, DEDICATION, REPLENISH, HARVESTING, TURBO_WHEAT, TURBO_COCO,
            TURBO_CACTUS, TURBO_MELON, TURBO_PUMPKIN, TURBO_WARTS, TURBO_MUSHROOMS, TURBO_POTATO,
            TURBO_CARROT, TURBO_SUGAR_CANE, TELEKINESIS);
    private static final Set<SkyBlockEnchantment> ROD = EnumSet.of(
            LUCK_OF_THE_SEA, ANGLER, FRAIL, EXPERTISE, MAGNET, TELEKINESIS);

    /** True once {@link #open} has created the live inventory; before that, only the maps exist. */
    private boolean live = false;

    public EnchantingMenu(Player player) {
        super(player, "Enchant Item", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }
        // ITEM_SLOT is interactive — leave it empty (also re-cleared in open()).
        setItem(ITEM_SLOT, null);

        setItem(TABLE_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§aEnchant Item")
                .lore("§7Add enchantments to the item", "§7in the slot above.")
                .build());

        setItem(BOOKSHELF_SLOT, new ItemBuilder(Material.BOOKSHELF)
                .displayName("§aBookshelf Power")
                .lore("§7Higher Bookshelf Power unlocks", "§7stronger enchantments.", "",
                        "§7Power: §a0")
                .build());

        setItem(GUIDE_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aEnchantments Guide")
                .lore("§7Browse every enchantment and", "§7what it does.")
                .build());

        setItem(SORT_SLOT, new ItemBuilder(Material.HOPPER)
                .displayName("§aSort")
                .lore("§7Sort: §eDefault")
                .build());

        renderEnchants();
    }

    @Override
    public void open(Player viewer) {
        super.open(viewer);
        live = true;
        getInventory().setItem(ITEM_SLOT, null);
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        return slot == ITEM_SLOT;
    }

    @Override
    public void onClose(Player viewer) {
        ItemStack item = getInventory().getItem(ITEM_SLOT);
        if (item != null && item.getType() != Material.AIR) {
            for (ItemStack overflow : viewer.getInventory().addItem(item).values()) {
                viewer.getWorld().dropItemNaturally(viewer.getLocation(), overflow);
            }
            getInventory().setItem(ITEM_SLOT, null);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getRawSlot() == ITEM_SLOT) {
            // Allow the place/pickup; re-render the applicable enchants a tick later.
            Bukkit.getScheduler().runTask(SkyBlockCore.getInstance(), this::renderEnchants);
            return;
        }
        event.setCancelled(true);
        super.handleClick(event);
    }

    /** Re-renders the applicable-enchant tiles for whatever item currently sits in the input slot. */
    private void renderEnchants() {
        ItemStack item = live ? getInventory().getItem(ITEM_SLOT) : null;
        boolean hasItem = item != null && item.getType() != Material.AIR
                && item.getType() != Material.BLACK_STAINED_GLASS_PANE;

        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot : ENCHANT_SLOTS) {
            place(slot, pane, null);
        }

        if (!hasItem) {
            place(PROMPT_SLOT, new ItemBuilder(Material.GRAY_DYE)
                    .displayName("§cPlace an item!")
                    .lore("§7Place an item in the open slot", "§7to enchant it!")
                    .build(), null);
            return;
        }

        EnchantingManager mgr = EnchantingManager.getInstance();
        List<SkyBlockEnchantment> applicable = applicableFor(item.getType());
        int i = 0;
        for (SkyBlockEnchantment ench : applicable) {
            if (i >= ENCHANT_SLOTS.length) {
                break;
            }
            int current = currentLevel(item, ench);
            int max = ench.getMaxLevel();
            boolean maxed = current >= max;
            int nextLevel = Math.min(max, current + 1);
            int cost = mgr.getEnchantCost(ench, nextLevel);
            boolean ultimate = mgr.isUltimate(ench);

            List<String> lore = new ArrayList<>();
            lore.add("§7On item: " + (current > 0 ? "§e" + ench.getDisplayName() + " " + toRoman(current) : "§8None"));
            lore.add("§7Max level: §e" + max);
            lore.add("");
            if (maxed) {
                lore.add("§aAlready at max level!");
            } else {
                lore.add("§7Apply " + ench.getDisplayName() + " " + toRoman(nextLevel));
                lore.add("§7Cost: §3" + cost + " XP");
                lore.add("");
                lore.add("§eClick to apply!");
            }

            final SkyBlockEnchantment applied = ench;
            place(ENCHANT_SLOTS[i], new ItemBuilder(ultimate ? Material.KNOWLEDGE_BOOK : Material.ENCHANTED_BOOK)
                            .displayName((ultimate ? "§d§l" : "§9") + ench.getDisplayName())
                            .lore(lore)
                            .build(),
                    maxed ? e -> e.setCancelled(true)
                          : e -> { e.setCancelled(true); applyEnchant(applied); });
            i++;
        }
    }

    /** Applies (or upgrades by one level) {@code ench} on the input item, deducting XP. */
    private void applyEnchant(SkyBlockEnchantment ench) {
        ItemStack item = getInventory().getItem(ITEM_SLOT);
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        int current = currentLevel(item, ench);
        if (current >= ench.getMaxLevel()) {
            player.sendMessage("§c" + ench.getDisplayName() + " is already at its maximum level.");
            return;
        }
        int nextLevel = current + 1;
        int cost = EnchantingManager.getInstance().getEnchantCost(ench, nextLevel);
        if (player.getTotalExperience() < cost) {
            player.sendMessage("§cYou need §3" + cost + " XP §cto apply " + ench.getDisplayName() + " " + toRoman(nextLevel) + ".");
            return;
        }
        player.giveExp(-cost);
        writeEnchantLore(item, ench, nextLevel);
        getInventory().setItem(ITEM_SLOT, item);
        player.sendMessage("§aApplied §9" + ench.getDisplayName() + " " + toRoman(nextLevel) + "§a!");
        renderEnchants();
    }

    /** Reads the current level of {@code ench} from the item's {@code §9<Name> <Roman>} lore, or 0. */
    private static int currentLevel(ItemStack item, SkyBlockEnchantment ench) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null) {
            return 0;
        }
        String target = ench.getDisplayName().toLowerCase();
        for (String raw : meta.getLore()) {
            String line = ChatColor.stripColor(raw).trim();
            String lower = line.toLowerCase();
            if (lower.startsWith(target + " ")) {
                return romanToInt(line.substring(target.length()).trim());
            }
        }
        return 0;
    }

    /** Inserts or updates the {@code §9<Name> <Roman>} enchant line at the top of the item lore. */
    private static void writeEnchantLore(ItemStack item, SkyBlockEnchantment ench, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String target = ench.getDisplayName().toLowerCase();
        String newLine = "§9" + ench.getDisplayName() + " " + toRoman(level);
        boolean replaced = false;
        for (int i = 0; i < lore.size(); i++) {
            String line = ChatColor.stripColor(lore.get(i)).trim().toLowerCase();
            if (line.startsWith(target + " ")) {
                lore.set(i, newLine);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            lore.add(0, newLine);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /** Returns the enchants applicable to the given item material's category. */
    private static List<SkyBlockEnchantment> applicableFor(Material mat) {
        Set<SkyBlockEnchantment> set;
        String n = mat.name();
        if (n.endsWith("_SWORD") || n.endsWith("_AXE")) {
            set = WEAPON;
        } else if (n.equals("BOW") || n.equals("CROSSBOW")) {
            set = BOW;
        } else if (n.endsWith("_HELMET") || n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS") || n.endsWith("_BOOTS")) {
            set = ARMOR;
        } else if (n.endsWith("_HOE")) {
            set = HOE;
        } else if (n.equals("FISHING_ROD")) {
            set = ROD;
        } else if (n.endsWith("_PICKAXE") || n.endsWith("_SHOVEL") || n.endsWith("_DRILL")) {
            set = TOOL;
        } else {
            set = WEAPON; // sensible default for custom SkyBlock weapons
        }
        List<SkyBlockEnchantment> out = new ArrayList<>();
        for (SkyBlockEnchantment e : SkyBlockEnchantment.values()) {
            if (set.contains(e)) {
                out.add(e);
            }
        }
        return out;
    }

    /** Updates both the items/handlers maps and the live inventory for a slot. */
    private void place(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItem(slot, item, handler);
        if (live) {
            getInventory().setItem(slot, item);
        }
    }

    private static String toRoman(int n) {
        if (n <= 0) {
            return "";
        }
        String[] te = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] on = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return te[(n / 10) % 10] + on[n % 10];
    }

    private static int romanToInt(String s) {
        switch (s.toUpperCase().trim()) {
            case "I": return 1;   case "II": return 2;  case "III": return 3; case "IV": return 4;
            case "V": return 5;   case "VI": return 6;  case "VII": return 7; case "VIII": return 8;
            case "IX": return 9;  case "X": return 10;
            default:
                try {
                    return Integer.parseInt(s.trim());
                } catch (NumberFormatException ex) {
                    return 1;
                }
        }
    }
}
