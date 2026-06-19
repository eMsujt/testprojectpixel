package com.skyblock.core.menu;

import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.Pet;
import com.skyblock.core.manager.PetsManager.PetRarity;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PetsMenu extends Menu {

    private static final Map<PetRarity, Color> RARITY_DYE = new EnumMap<>(PetRarity.class);
    private static final Map<PetRarity, String> RARITY_CHAT = new EnumMap<>(PetRarity.class);

    static {
        RARITY_DYE.put(PetRarity.COMMON,    Color.WHITE);
        RARITY_DYE.put(PetRarity.UNCOMMON,  Color.fromRGB(0x55, 0xFF, 0x55));
        RARITY_DYE.put(PetRarity.RARE,      Color.fromRGB(0x55, 0x55, 0xFF));
        RARITY_DYE.put(PetRarity.EPIC,      Color.fromRGB(0xAA, 0x00, 0xAA));
        RARITY_DYE.put(PetRarity.LEGENDARY, Color.fromRGB(0xFF, 0xAA, 0x00));

        RARITY_CHAT.put(PetRarity.COMMON,    "§f");
        RARITY_CHAT.put(PetRarity.UNCOMMON,  "§a");
        RARITY_CHAT.put(PetRarity.RARE,      "§9");
        RARITY_CHAT.put(PetRarity.EPIC,      "§5");
        RARITY_CHAT.put(PetRarity.LEGENDARY, "§6");
    }

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("Pets", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        List<Pet> pets = PetsManager.getInstance().getPets(playerId);

        if (pets.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You don't own any pets yet.")
                    .build());
            return;
        }

        for (int i = 0; i < INNER_SLOTS.length && i < pets.size(); i++) {
            Pet pet = pets.get(i);
            PetRarity rarity = pet.rarity();
            String chatColor = RARITY_CHAT.getOrDefault(rarity, "§f");
            Color dyeColor = RARITY_DYE.getOrDefault(rarity, Color.WHITE);
            String rarityName = rarity.name().charAt(0) + rarity.name().substring(1).toLowerCase();

            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
            if (meta != null) {
                meta.setColor(dyeColor);
                meta.setDisplayName(chatColor + pet.name());
                meta.setLore(Arrays.asList("§7Rarity: " + chatColor + rarityName));
                helm.setItemMeta(meta);
            }
            setItem(INNER_SLOTS[i], helm);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
