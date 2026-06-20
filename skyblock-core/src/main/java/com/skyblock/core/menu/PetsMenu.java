package com.skyblock.core.menu;

import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.manager.PetsManager.PetRarity;
import com.skyblock.core.manager.PetsManager.PetType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class PetsMenu extends AbstractSkyBlockMenu {

    public PetsMenu(Player player) {
        super(player, "§aPets", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        PetsManager mgr = PetsManager.getInstance();
        List<PetData> pets = mgr.getPets(player.getUniqueId());
        PetData active = mgr.getActivePet(player.getUniqueId());

        for (int i = 0; i < pets.size() && i < 36; i++) {
            PetData pet = pets.get(i);
            boolean isActive = active != null && active.id.equals(pet.id);
            String activeLine = isActive ? "§a§lACTIVE" : "§7Click to equip";
            setItem(9 + i, new ItemBuilder(materialFor(pet.type))
                    .displayName(pet.getDisplayName())
                    .lore("§7Rarity: " + pet.rarity.getDisplayName(),
                          "§7Level: §e" + pet.getLevel() + "§7/§e" + PetsManager.MAX_LEVEL,
                          "§7XP: §e" + String.format("%,d", pet.getExperience()),
                          activeLine)
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (active != null && active.id.equals(pet.id)) {
                            mgr.setActivePet(player.getUniqueId(), null);
                            player.sendMessage("§eDeactivated pet §f" + pet.type.getDisplayName() + "§e.");
                        } else {
                            mgr.setActivePet(player.getUniqueId(), pet.id);
                            player.sendMessage("§aEquipped pet §f" + pet.type.getDisplayName() + "§a.");
                        }
                        open(player);
                    });
        }

        if (pets.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You have no pets yet.")
                    .build());
        }
    }

    private static Material materialFor(PetType type) {
        switch (type) {
            case BEE:            return Material.BEE_SPAWN_EGG;
            case BLAZE:          return Material.BLAZE_SPAWN_EGG;
            case CAT:            return Material.CAT_SPAWN_EGG;
            case CHICKEN:        return Material.CHICKEN_SPAWN_EGG;
            case ENDERMAN:       return Material.ENDERMAN_SPAWN_EGG;
            case ENDER_DRAGON:   return Material.ENDER_DRAGON_SPAWN_EGG;
            case GOLEM:          return Material.IRON_GOLEM_SPAWN_EGG;
            case GUARDIAN:       return Material.GUARDIAN_SPAWN_EGG;
            case HORSE:          return Material.HORSE_SPAWN_EGG;
            case MAGMA_CUBE:     return Material.MAGMA_CUBE_SPAWN_EGG;
            case MONKEY:         return Material.PANDA_SPAWN_EGG;
            case OCELOT:         return Material.OCELOT_SPAWN_EGG;
            case PIG:            return Material.PIG_SPAWN_EGG;
            case PHOENIX:        return Material.BLAZE_SPAWN_EGG;
            case RABBIT:         return Material.RABBIT_SPAWN_EGG;
            case SHEEP:          return Material.SHEEP_SPAWN_EGG;
            case SILVERFISH:     return Material.SILVERFISH_SPAWN_EGG;
            case SKELETON:       return Material.SKELETON_SPAWN_EGG;
            case SKELETON_HORSE: return Material.SKELETON_HORSE_SPAWN_EGG;
            case SNOWMAN:        return Material.SNOW_GOLEM_SPAWN_EGG;
            case SPIDER:         return Material.SPIDER_SPAWN_EGG;
            case SQUID:          return Material.SQUID_SPAWN_EGG;
            case TURTLE:         return Material.TURTLE_SPAWN_EGG;
            case WITHER_SKELETON: return Material.WITHER_SKELETON_SPAWN_EGG;
            case WOLF:           return Material.WOLF_SPAWN_EGG;
            case ZOMBIE:         return Material.ZOMBIE_SPAWN_EGG;
            default:             return Material.BAT_SPAWN_EGG;
        }
    }
}
