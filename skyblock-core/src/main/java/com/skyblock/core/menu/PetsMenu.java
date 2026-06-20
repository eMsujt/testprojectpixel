package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public final class PetsMenu extends AbstractSkyBlockMenu {

    public PetsMenu(Player player) {
        super(player, "§dPets", 6);
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
            Material icon = PetMenu.TYPE_ICON.getOrDefault(pet.type, Material.SLIME_BALL);
            setItem(9 + i, new ItemBuilder(icon)
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
