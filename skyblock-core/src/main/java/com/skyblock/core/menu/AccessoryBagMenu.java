package com.skyblock.core.menu;

import com.skyblock.core.manager.AccessoryManager;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AccessoryBagMenu extends AbstractSkyBlockMenu {

    public AccessoryBagMenu(Player player) {
        super(player, "§6Accessory Bag", 5);
    }

    @Override
    protected void populate() {
        UUID id = player.getUniqueId();
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 45; slot++) setItem(slot, pane);

        Map<TalismanManager.TalismanType, AccessoryRarity> accessories =
                AccessoryManager.getInstance().getAccessories(id);
        List<TalismanManager.TalismanType> types = new ArrayList<>(accessories.keySet());
        for (int i = 0; i < 36 && i < types.size(); i++) {
            TalismanManager.TalismanType type = types.get(i);
            AccessoryRarity rarity = accessories.get(type);
            String color = rarityColor(rarity);
            setItem(i, new ItemBuilder(rarityMaterial(rarity))
                    .displayName(color + formatName(type.name()))
                    .lore("§7+" + type.bonus + " " + type.stat.getDisplayName(),
                          color + rarity.getDisplayName())
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static String rarityColor(AccessoryRarity rarity) {
        switch (rarity) {
            case UNCOMMON:    return "§a";
            case RARE:        return "§9";
            case EPIC:        return "§5";
            case LEGENDARY:   return "§6";
            case MYTHIC:      return "§d";
            case SPECIAL:     return "§c";
            case VERY_SPECIAL: return "§c";
            default:          return "§f";
        }
    }

    private static Material rarityMaterial(AccessoryRarity rarity) {
        switch (rarity) {
            case UNCOMMON:    return Material.GOLD_INGOT;
            case RARE:        return Material.DIAMOND;
            case EPIC:        return Material.AMETHYST_SHARD;
            case LEGENDARY:   return Material.NETHER_STAR;
            case MYTHIC:      return Material.TOTEM_OF_UNDYING;
            case SPECIAL:     return Material.BLAZE_ROD;
            case VERY_SPECIAL: return Material.BLAZE_POWDER;
            default:          return Material.IRON_INGOT;
        }
    }

    private static String formatName(String enumName) {
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase(java.util.Locale.ROOT));
        }
        return sb.toString();
    }
}
