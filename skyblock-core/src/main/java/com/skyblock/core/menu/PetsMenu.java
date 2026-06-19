package com.skyblock.core.menu;

import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.Pet;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class PetsMenu extends Menu {

    private final Player player;

    public PetsMenu(Player player) {
        super("§aPets", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        List<Pet> pets = PetsManager.getInstance().getPets(player.getUniqueId());
        for (int i = 0; i < pets.size() && i < 54; i++) {
            Pet pet = pets.get(i);
            ItemStack item = new ItemBuilder(Material.BONE)
                    .displayName("§a" + pet.name())
                    .lore("§7Rarity: §" + rarityCode(pet) + capitalize(pet.rarity().name()))
                    .build();
            setItem(i, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static char rarityCode(Pet pet) {
        switch (pet.rarity()) {
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
