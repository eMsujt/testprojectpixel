package com.skyblock.core.menu;

import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager;
import com.skyblock.core.talisman.manager.TalismanManager.TalismanType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public final class TalismanMenu extends AbstractSkyBlockMenu {

    public TalismanMenu(Player player) {
        super(player, "Accessory Bag", 54);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        TalismanManager mgr = TalismanManager.getInstance();
        Set<TalismanType> equipped = mgr.getEquipped(player.getUniqueId());
        TalismanType[] all = TalismanType.values();

        for (int i = 0; i < all.length && i < contentCapacity(); i++) {
            TalismanType type = all[i];
            boolean isEquipped = equipped.contains(type);
            String statLine = "§7" + type.stat.name() + ": §e+" + type.bonus;
            String rarityLine = "§7Rarity: " + colorFor(type.rarity) + type.rarity.getDisplayName();
            String actionLine = isEquipped ? "§a§lEQUIPPED §7(click to unequip)" : "§7Click to equip";
            setItem(contentSlot(i), new ItemBuilder(materialFor(type.rarity))
                    .displayName(colorFor(type.rarity) + formatName(type))
                    .lore(rarityLine, statLine, actionLine)
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (mgr.hasEquipped(player.getUniqueId(), type)) {
                            mgr.unequip(player.getUniqueId(), type);
                            player.sendMessage("§eUnequipped §f" + formatName(type) + "§e.");
                        } else {
                            mgr.equip(player.getUniqueId(), type);
                            player.sendMessage("§aEquipped §f" + formatName(type) + "§a.");
                        }
                        open(player);
                    });
        }

        if (equipped.isEmpty() && all.length == 0) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Accessories")
                    .lore("§7You have no accessories yet.")
                    .build());
        }
    }

    private static String formatName(TalismanType type) {
        String raw = type.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : raw.split(" ")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private static String colorFor(AccessoryRarity rarity) {
        switch (rarity) {
            case COMMON:       return "§f";
            case UNCOMMON:     return "§a";
            case RARE:         return "§9";
            case EPIC:         return "§5";
            case LEGENDARY:    return "§6";
            case MYTHIC:       return "§d";
            case SPECIAL:      return "§c";
            case VERY_SPECIAL: return "§c";
            default:           return "§7";
        }
    }

    private static Material materialFor(AccessoryRarity rarity) {
        switch (rarity) {
            case COMMON:       return Material.GOLD_NUGGET;
            case UNCOMMON:     return Material.IRON_INGOT;
            case RARE:         return Material.GOLD_INGOT;
            case EPIC:         return Material.DIAMOND;
            case LEGENDARY:    return Material.EMERALD;
            case MYTHIC:       return Material.NETHER_STAR;
            case SPECIAL:      return Material.ENDER_EYE;
            case VERY_SPECIAL: return Material.DRAGON_BREATH;
            default:           return Material.GOLD_NUGGET;
        }
    }
}
