package com.skyblock.core.item;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.RuneManager.RuneType;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Rune <em>items</em>: the placeable runes a player drops into the Runic
 * Pedestal's sacrifice slot, and the rune stamped onto a target item once
 * applied. Both are stored in the item's {@link PersistentDataContainer} (so
 * they survive trades/storage), mirroring {@code ReforgeManager}'s reforge
 * stamping. A rune item is a consumable; applying it writes the rune onto a
 * target item's PDC + lore.
 */
public final class RuneItem {

    private RuneItem() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey(SkyBlockCore.getInstance(), name);
    }

    // A rune item (consumable) carries these:
    private static NamespacedKey runeTypeKey()    { return key("rune_item_type"); }
    private static NamespacedKey runeLevelKey()   { return key("rune_item_level"); }
    // A target item that has a rune applied carries these:
    private static NamespacedKey appliedTypeKey()  { return key("applied_rune_type"); }
    private static NamespacedKey appliedLevelKey() { return key("applied_rune_level"); }

    /** An immutable (rune type, level) reference. */
    public record RuneRef(RuneType type, int level) {
    }

    // -- Rune items (the consumable) -----------------------------------------

    /** Builds a rune item for {@code type} at {@code level}, clamped to the rune's max. */
    public static ItemStack createRuneItem(RuneType type, int level) {
        int lvl = Math.max(1, Math.min(level, type.getMaxLevel()));
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a" + type.getDisplayName() + " Rune " + SkyblockUtils.toRoman(lvl));
            meta.setLore(Arrays.asList(
                    "§7Apply on the Runic Pedestal to give",
                    "§7an item a cosmetic effect:",
                    "§8" + type.getVisual(),
                    "",
                    "§7Two matching runes can be fused",
                    "§7into the next level.",
                    "",
                    "§9§lRUNE"));
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(runeTypeKey(), PersistentDataType.STRING, type.name());
            pdc.set(runeLevelKey(), PersistentDataType.INTEGER, lvl);
            item.setItemMeta(meta);
        }
        return item;
    }

    /** Reads a rune item's (type, level), or {@code null} if {@code item} is not a rune item. */
    public static RuneRef readRuneItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String typeName = pdc.get(runeTypeKey(), PersistentDataType.STRING);
        if (typeName == null) {
            return null;
        }
        Integer level = pdc.get(runeLevelKey(), PersistentDataType.INTEGER);
        try {
            return new RuneRef(RuneType.valueOf(typeName), level == null ? 1 : level);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Whether {@code item} is a rune item. */
    public static boolean isRuneItem(ItemStack item) {
        return readRuneItem(item) != null;
    }

    // -- Runes applied to a target item --------------------------------------

    /** Stamps {@code type} at {@code level} onto {@code target} (PDC + lore), replacing any existing rune. */
    public static void applyRuneToItem(ItemStack target, RuneType type, int level) {
        if (target == null) {
            return;
        }
        ItemMeta meta = target.getItemMeta();
        if (meta == null) {
            return;
        }
        int lvl = Math.max(1, Math.min(level, type.getMaxLevel()));
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(appliedTypeKey(), PersistentDataType.STRING, type.name());
        pdc.set(appliedLevelKey(), PersistentDataType.INTEGER, lvl);
        setRuneLore(meta, "§7Rune: §d" + type.getDisplayName() + " " + SkyblockUtils.toRoman(lvl));
        target.setItemMeta(meta);
    }

    /** Returns the rune applied to {@code target}, or {@code null} if none. */
    public static RuneRef getItemRune(ItemStack target) {
        if (target == null) {
            return null;
        }
        ItemMeta meta = target.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String typeName = pdc.get(appliedTypeKey(), PersistentDataType.STRING);
        if (typeName == null) {
            return null;
        }
        Integer level = pdc.get(appliedLevelKey(), PersistentDataType.INTEGER);
        try {
            return new RuneRef(RuneType.valueOf(typeName), level == null ? 1 : level);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Removes any rune applied to {@code target} (PDC + lore). Returns the removed rune, or null. */
    public static RuneRef removeItemRune(ItemStack target) {
        RuneRef existing = getItemRune(target);
        if (existing == null) {
            return null;
        }
        ItemMeta meta = target.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(appliedTypeKey());
        pdc.remove(appliedLevelKey());
        setRuneLore(meta, null);
        target.setItemMeta(meta);
        return existing;
    }

    /** Replaces the single {@code §7Rune: …} lore line (removing it when {@code line} is null). */
    private static void setRuneLore(ItemMeta meta, String line) {
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(l -> ChatColor.stripColor(l).startsWith("Rune: "));
        if (line != null) {
            lore.add(line);
        }
        meta.setLore(lore);
    }
}
