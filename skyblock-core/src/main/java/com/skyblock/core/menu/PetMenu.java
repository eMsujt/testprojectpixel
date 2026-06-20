package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public PetMenu(Player player) {
        super(player, "§dPets", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        PetManager manager = PetManager.getInstance();
        UUID playerId = player.getUniqueId();
        List<Pet> pets = manager.getPets(playerId);
        UUID activePetId = manager.getActivePetId(playerId);

        for (int i = 0; i < pets.size() && i < 45; i++) {
            Pet pet = pets.get(i);
            boolean isActive = pet.id.equals(activePetId);
            int level = manager.getPetData(playerId, pet.type).getLevel();
            String color = SkyblockUtils.rarityColor(pet.rarity).toString();
            Material icon = RARITY_WOOL.getOrDefault(pet.rarity, Material.WHITE_WOOL);
            String activeTag = isActive ? " §a§l[ACTIVE]" : "";
            setItem(i, new ItemBuilder(icon)
                    .displayName(color + pet.type.getDisplayName() + activeTag)
                    .lore("§7Level: §e" + level,
                          "§7Rarity: " + color + pet.rarity.getDisplayName(),
                          "§7Category: §e" + pet.type.getCategory().getDisplayName(),
                          "",
                          isActive ? "§eClick to unequip" : "§eClick to equip")
                    .build(),
                    e -> {
                        if (isActive) {
                            manager.unequipPet(playerId);
                        } else {
                            manager.equipPet(playerId, pet.id);
                        }
                        open(player);
                    });
        }

        if (pets.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You have no pets yet.")
                    .build());
        }
    }
}
