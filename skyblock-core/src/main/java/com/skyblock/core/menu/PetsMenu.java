package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PetsMenu extends AbstractSkyBlockMenu {

    private static final Map<Rarity, Color> RARITY_COLOR;

    static {
        Map<Rarity, Color> m = new EnumMap<>(Rarity.class);
        m.put(Rarity.COMMON,    Color.WHITE);
        m.put(Rarity.UNCOMMON,  Color.LIME);
        m.put(Rarity.RARE,      Color.BLUE);
        m.put(Rarity.EPIC,      Color.PURPLE);
        m.put(Rarity.LEGENDARY, Color.ORANGE);
        m.put(Rarity.MYTHIC,    Color.FUCHSIA);
        m.put(Rarity.DIVINE,    Color.AQUA);
        m.put(Rarity.SPECIAL,   Color.RED);
        RARITY_COLOR = Collections.unmodifiableMap(m);
    }

    public PetsMenu(Player player) {
        super(player, "§5Pets", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        PetManager manager = PetManager.getInstance();
        UUID playerId = player.getUniqueId();
        List<Pet> pets = manager.getPets(playerId);
        UUID activePetId = manager.getActivePetId(playerId);

        for (int i = 0; i < pets.size() && i < 36; i++) {
            Pet pet = pets.get(i);
            boolean isActive = pet.id.equals(activePetId);
            int level = manager.getPetData(playerId, pet.type).getLevel();
            String color = SkyblockUtils.rarityColor(pet.rarity).toString();
            Color dyeColor = RARITY_COLOR.getOrDefault(pet.rarity, Color.WHITE);
            setItem(9 + i, new ItemBuilder(Material.LEATHER_HELMET)
                    .leatherColor(dyeColor)
                    .displayName(color + pet.type.getDisplayName() + (isActive ? " §a§l[ACTIVE]" : ""))
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
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You have no pets yet.")
                    .build());
        }
    }
}
