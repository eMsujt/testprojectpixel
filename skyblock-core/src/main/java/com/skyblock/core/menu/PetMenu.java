package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class PetMenu extends AbstractSkyBlockMenu {

    public static final Map<Rarity, Material> RARITY_WOOL;

    static {
        Map<Rarity, Material> m = new EnumMap<>(Rarity.class);
        m.put(Rarity.COMMON,    Material.WHITE_WOOL);
        m.put(Rarity.UNCOMMON,  Material.LIME_WOOL);
        m.put(Rarity.RARE,      Material.BLUE_WOOL);
        m.put(Rarity.EPIC,      Material.PURPLE_WOOL);
        m.put(Rarity.LEGENDARY, Material.ORANGE_WOOL);
        m.put(Rarity.MYTHIC,    Material.PINK_WOOL);
        m.put(Rarity.DIVINE,    Material.CYAN_WOOL);
        m.put(Rarity.SPECIAL,   Material.RED_WOOL);
        RARITY_WOOL = Collections.unmodifiableMap(m);
    }

    private static final Map<PetType, Material> TYPE_ICON;

    static {
        Map<PetType, Material> m = new EnumMap<>(PetType.class);
        m.put(PetType.WOLF,           Material.BONE);
        m.put(PetType.GRANDMA_WOLF,   Material.BONE);
        m.put(PetType.HOUND,          Material.BONE);
        m.put(PetType.DOG,            Material.BONE);
        m.put(PetType.CHICKEN,        Material.EGG);
        m.put(PetType.PIG,            Material.PORKCHOP);
        m.put(PetType.COW,            Material.LEATHER);
        m.put(PetType.MOOSHROOM_COW,  Material.RED_MUSHROOM);
        m.put(PetType.RABBIT,         Material.RABBIT_FOOT);
        m.put(PetType.SHEEP,          Material.WHITE_WOOL);
        m.put(PetType.CAT,            Material.COD);
        m.put(PetType.BLACK_CAT,      Material.COD);
        m.put(PetType.BEE,            Material.HONEYCOMB);
        m.put(PetType.HORSE,          Material.SADDLE);
        m.put(PetType.MULE,           Material.SADDLE);
        m.put(PetType.DONKEY,         Material.SADDLE);
        m.put(PetType.SKELETON,       Material.BONE);
        m.put(PetType.WITHER_SKELETON,Material.WITHER_SKELETON_SKULL);
        m.put(PetType.ZOMBIE,         Material.ROTTEN_FLESH);
        m.put(PetType.GHOUL,          Material.ROTTEN_FLESH);
        m.put(PetType.SPIDER,         Material.SPIDER_EYE);
        m.put(PetType.CAVE_SPIDER,    Material.SPIDER_EYE);
        m.put(PetType.TARANTULA,      Material.SPIDER_EYE);
        m.put(PetType.CREEPER,        Material.GUNPOWDER);
        m.put(PetType.ENDERMAN,       Material.ENDER_PEARL);
        m.put(PetType.ENDERMITE,      Material.ENDER_PEARL);
        m.put(PetType.ENDER_DRAGON,   Material.DRAGON_BREATH);
        m.put(PetType.BLAZE,          Material.BLAZE_ROD);
        m.put(PetType.PHOENIX,        Material.BLAZE_POWDER);
        m.put(PetType.MAGMA_CUBE,     Material.MAGMA_CREAM);
        m.put(PetType.GHAST,          Material.GHAST_TEAR);
        m.put(PetType.PIGMAN,         Material.GOLD_NUGGET);
        m.put(PetType.PARROT,         Material.FEATHER);
        m.put(PetType.DOLPHIN,        Material.TROPICAL_FISH);
        m.put(PetType.SQUID,          Material.INK_SAC);
        m.put(PetType.FLYING_FISH,    Material.TROPICAL_FISH);
        m.put(PetType.TURTLE,         Material.SLIME_BALL);
        m.put(PetType.GUARDIAN,       Material.PRISMARINE_SHARD);
        m.put(PetType.JELLYFISH,      Material.SLIME_BALL);
        m.put(PetType.PENGUIN,        Material.PACKED_ICE);
        m.put(PetType.BLUE_WHALE,     Material.HEART_OF_THE_SEA);
        m.put(PetType.BLUE_SHARK,     Material.PRISMARINE_SHARD);
        m.put(PetType.AMMONITE,       Material.NAUTILUS_SHELL);
        m.put(PetType.WORM,           Material.STRING);
        m.put(PetType.SNAIL,          Material.SLIME_BALL);
        m.put(PetType.SLUG,           Material.SLIME_BALL);
        m.put(PetType.MOSQUITO,       Material.FEATHER);
        m.put(PetType.BAT,            Material.LEATHER);
        m.put(PetType.GOAT,           Material.WHITE_WOOL);
        m.put(PetType.SILVERFISH,     Material.STONE);
        m.put(PetType.MONKEY,         Material.OAK_LOG);
        m.put(PetType.OCELOT,         Material.COD);
        m.put(PetType.ELEPHANT,       Material.IRON_INGOT);
        m.put(PetType.GIRAFFE,        Material.OAK_LOG);
        m.put(PetType.LION,           Material.GOLD_INGOT);
        m.put(PetType.ROCK,           Material.STONE);
        m.put(PetType.SCARECROW,      Material.HAY_BLOCK);
        m.put(PetType.MITHRIL_GOLEM,  Material.IRON_BLOCK);
        m.put(PetType.GOLEM,          Material.IRON_BLOCK);
        m.put(PetType.BABY_YETI,      Material.SNOWBALL);
        m.put(PetType.TIGER,          Material.FEATHER);
        m.put(PetType.WISP,           Material.GLOWSTONE_DUST);
        m.put(PetType.DROPLET_WISP,   Material.WATER_BUCKET);
        m.put(PetType.SNOWMAN,        Material.SNOWBALL);
        m.put(PetType.ARMADILLO,      Material.IRON_CHESTPLATE);
        m.put(PetType.SPINOCLAW,      Material.IRON_SWORD);
        m.put(PetType.HEDGEHOG,       Material.OAK_SAPLING);
        m.put(PetType.SUMO,           Material.IRON_CHESTPLATE);
        m.put(PetType.GRIFFIN,        Material.FEATHER);
        m.put(PetType.GOLDEN_DRAGON,  Material.GOLD_INGOT);
        m.put(PetType.JERRY,          Material.SNOWBALL);
        TYPE_ICON = Collections.unmodifiableMap(m);
    }

    public PetMenu(Player player) {
        super(player, "§dPets", 54);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        PetType[] types = PetType.values();
        for (int i = 0; i < types.length && i < 36; i++) {
            PetType type = types[i];
            String color = SkyblockUtils.rarityColor(type.defaultRarity).toString();
            Material icon = TYPE_ICON.getOrDefault(type, Material.SLIME_BALL);
            setItem(9 + i, new ItemBuilder(icon)
                    .displayName(color + type.getDisplayName())
                    .lore("§7Rarity: " + color + type.defaultRarity.getDisplayName(),
                          "§7Category: §e" + type.getCategory().getDisplayName())
                    .build(),
                    e -> e.setCancelled(true));
        }
    }
}
