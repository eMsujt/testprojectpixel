package com.skyblock.core.menu;

import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class AccessoryBagMenu extends AbstractSkyBlockMenu {

    public static final int SUMMARY_SLOT = 4;
    public static final int[] RARITY_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16};

    private static final int[] ACCESSORY_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final int page;

    public AccessoryBagMenu(Player player) {
        this(player, 0);
    }

    private AccessoryBagMenu(Player player, int page) {
        super(player, "Accessory Bag", 6);
        this.page = page;
    }

    @Override
    protected void populate() {
        UUID id = player.getUniqueId();
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, pane);

        AccessoryBagManager mgr = AccessoryBagManager.getInstance();

        int power = mgr.getTotalMagicPower(id);
        int size = mgr.getSize(id);
        int unlocked = mgr.getUnlockedSlots(id);
        // Hypixel: the stat is "Magical Power", and 10 Magical Power = 1 Tuning Point.
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§6Accessory Bag")
                .lore("§7Accessories: §e" + size + "§7/§e" + unlocked,
                      "§7Magical Power: §d" + power,
                      "§7Tuning Points: §d" + (power / 10))
                .build());

        List<TalismanManager.TalismanType> contents = new ArrayList<>(mgr.getContents(id));
        int pageSize = ACCESSORY_SLOTS.length;
        int totalPages = Math.max(1, (contents.size() + pageSize - 1) / pageSize);
        int pageClamped = Math.max(0, Math.min(page, totalPages - 1));
        int start = pageClamped * pageSize;

        for (int i = start; i < contents.size() && i < start + pageSize; i++) {
            TalismanManager.TalismanType type = contents.get(i);
            String rarityColor = rarityColor(type.rarity);
            setItem(ACCESSORY_SLOTS[i - start], new ItemBuilder(Material.EMERALD)
                    // Hypixel colours the accessory name by its rarity and ends the lore
                    // with the bold rarity line in the same colour.
                    .displayName(rarityColor + formatName(type.name()))
                    .lore("§7Grants §a+" + trimStat(type.bonus) + " " + type.stat.getDisplayName() + "§7.",
                          "",
                          rarityColor + "§l" + type.rarity.getDisplayName().toUpperCase(Locale.ROOT) + " ACCESSORY")
                    .build());
        }

        if (pageClamped > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + pageClamped + "§7/§e" + totalPages)
                    .build(),
                    e -> { e.setCancelled(true); new AccessoryBagMenu(player, pageClamped - 1).open(player); });
        }
        if (pageClamped < totalPages - 1) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (pageClamped + 2) + "§7/§e" + totalPages)
                    .build(),
                    e -> { e.setCancelled(true); new AccessoryBagMenu(player, pageClamped + 1).open(player); });
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To Your Bags")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        super.handleClick(event);
    }

    private static String formatName(String enumName) {
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }

    /** Hypixel's rarity colour scheme for accessory names + the bottom rarity line. */
    private static String rarityColor(AccessoryRarity rarity) {
        switch (rarity) {
            case UNCOMMON:     return "§a";
            case RARE:         return "§9";
            case EPIC:         return "§5";
            case LEGENDARY:    return "§6";
            case MYTHIC:       return "§d";
            case SPECIAL:
            case VERY_SPECIAL: return "§c";
            case COMMON:
            default:           return "§f";
        }
    }

    private static String trimStat(double v) {
        return v == Math.floor(v) ? Long.toString((long) v) : String.format("%.1f", v);
    }
}
