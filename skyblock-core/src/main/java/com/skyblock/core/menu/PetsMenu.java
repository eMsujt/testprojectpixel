package com.skyblock.core.menu;

import com.skyblock.core.manager.PetsManager.PetType;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class PetsMenu extends Menu {

    public PetsMenu() {
        super("§aPets", 6);
    }

    @Override
    protected void build() {
        PetType[] types = PetType.values();
        for (int i = 0; i < types.length && i < 54; i++) {
            PetType type = types[i];
            ItemStack item = new ItemBuilder(Material.BONE)
                    .displayName("§a" + type.getDisplayName())
                    .lore("§7Rarity: §" + rarityCode(type) + capitalize(type.getDefaultRarity().name()))
                    .build();
            setItem(i, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static char rarityCode(PetType type) {
        switch (type.getDefaultRarity()) {
            case COMMON:    return 'f';
            case UNCOMMON:  return 'a';
            case RARE:      return '9';
            case EPIC:      return '5';
            case LEGENDARY: return '6';
            default:        return 'f';
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
