package com.skyblock.core.menu;

import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class PetsMenu extends AbstractSkyBlockMenu {

    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public PetsMenu(Player player) {
        super(player, "§aPets", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        PetsManager manager = PetsManager.getInstance();
        PetData activePet = manager.getActivePet(player.getUniqueId());
        List<PetData> pets = manager.getPets(player.getUniqueId());

        for (int i = 0; i < pets.size() && i < CONTENT_SLOTS.length; i++) {
            PetData pet = pets.get(i);
            boolean isActive = activePet != null && activePet.id.equals(pet.id);
            setItem(CONTENT_SLOTS[i], new ItemBuilder(Material.RABBIT_SPAWN_EGG)
                    .displayName(pet.getDisplayName())
                    .lore("§7Rarity: " + pet.rarity.getDisplayName(),
                          "§7Level: §e" + pet.getLevel(),
                          isActive ? "§aCurrently Active" : "§eClick to equip")
                    .build());
        }

        if (pets.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You have no pets yet.")
                    .build());
        }
    }
}
