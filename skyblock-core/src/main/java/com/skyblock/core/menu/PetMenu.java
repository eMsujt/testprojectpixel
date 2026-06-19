package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class PetMenu extends Menu {

    public static final Map<Rarity, Material> RARITY_WOOL;

    static {
        RARITY_WOOL = new EnumMap<>(Rarity.class);
        RARITY_WOOL.put(Rarity.COMMON,    Material.WHITE_WOOL);
        RARITY_WOOL.put(Rarity.UNCOMMON,  Material.LIME_WOOL);
        RARITY_WOOL.put(Rarity.RARE,      Material.BLUE_WOOL);
        RARITY_WOOL.put(Rarity.EPIC,      Material.PURPLE_WOOL);
        RARITY_WOOL.put(Rarity.LEGENDARY, Material.ORANGE_WOOL);
        RARITY_WOOL.put(Rarity.MYTHIC,    Material.PINK_WOOL);
        RARITY_WOOL.put(Rarity.DIVINE,    Material.CYAN_WOOL);
        RARITY_WOOL.put(Rarity.SPECIAL,   Material.RED_WOOL);
    }

    private final UUID playerId;

    public PetMenu(UUID playerId) {
        super("§dPets", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        PetType[] types = PetType.values();
        for (int i = 0; i < types.length && i < 36; i++) {
            PetType type = types[i];
            String color = colorCode(type.defaultRarity);
            setItem(9 + i, new ItemBuilder(spawnEggFor(type))
                    .displayName(color + type.getDisplayName())
                    .lore("§7Rarity: " + color + type.defaultRarity.getDisplayName(),
                          "§7Category: §e" + type.getCategory().getDisplayName())
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private static String colorCode(Rarity rarity) {
        switch (rarity) {
            case COMMON:    return "§f";
            case UNCOMMON:  return "§a";
            case RARE:      return "§9";
            case EPIC:      return "§5";
            case LEGENDARY: return "§6";
            default:        return "§f";
        }
    }

    private static Material spawnEggFor(PetType type) {
        switch (type) {
            case CHICKEN:         return Material.CHICKEN_SPAWN_EGG;
            case PIG:             return Material.PIG_SPAWN_EGG;
            case COW:             return Material.COW_SPAWN_EGG;
            case BAT:             return Material.BAT_SPAWN_EGG;
            case SHEEP:           return Material.SHEEP_SPAWN_EGG;
            case RABBIT:          return Material.RABBIT_SPAWN_EGG;
            case HORSE:           return Material.HORSE_SPAWN_EGG;
            case MULE:            return Material.MULE_SPAWN_EGG;
            case DONKEY:          return Material.DONKEY_SPAWN_EGG;
            case CAVE_SPIDER:     return Material.CAVE_SPIDER_SPAWN_EGG;
            case SILVERFISH:      return Material.SILVERFISH_SPAWN_EGG;
            case BEE:             return Material.BEE_SPAWN_EGG;
            case CAT:             return Material.CAT_SPAWN_EGG;
            case PARROT:          return Material.PARROT_SPAWN_EGG;
            case DOLPHIN:         return Material.DOLPHIN_SPAWN_EGG;
            case SQUID:           return Material.SQUID_SPAWN_EGG;
            case GUARDIAN:        return Material.GUARDIAN_SPAWN_EGG;
            case OCELOT:          return Material.OCELOT_SPAWN_EGG;
            case TURTLE:          return Material.TURTLE_SPAWN_EGG;
            case CREEPER:         return Material.CREEPER_SPAWN_EGG;
            case ZOMBIE:          return Material.ZOMBIE_SPAWN_EGG;
            case SKELETON:        return Material.SKELETON_SPAWN_EGG;
            case SPIDER:          return Material.SPIDER_SPAWN_EGG;
            case ENDERMITE:       return Material.ENDERMITE_SPAWN_EGG;
            case WOLF:            return Material.WOLF_SPAWN_EGG;
            case BLAZE:           return Material.BLAZE_SPAWN_EGG;
            case MAGMA_CUBE:      return Material.MAGMA_CUBE_SPAWN_EGG;
            case ENDERMAN:        return Material.ENDERMAN_SPAWN_EGG;
            case GHAST:           return Material.GHAST_SPAWN_EGG;
            case WITHER_SKELETON: return Material.WITHER_SKELETON_SPAWN_EGG;
            case SNOWMAN:         return Material.SNOW_GOLEM_SPAWN_EGG;
            case ENDER_DRAGON:    return Material.ENDER_DRAGON_SPAWN_EGG;
            case TARANTULA:       return Material.CAVE_SPIDER_SPAWN_EGG;
            case GHOUL:           return Material.ZOMBIE_SPAWN_EGG;
            case PHOENIX:         return Material.BLAZE_SPAWN_EGG;
            default:              return Material.BAT_SPAWN_EGG;
        }
    }
}
