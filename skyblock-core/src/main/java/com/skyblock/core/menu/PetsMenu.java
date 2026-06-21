package com.skyblock.core.menu;

import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 6-row chest GUI titled '§dPets'. Shows the player's active pet summary at
 * slot 4 and one tile per owned {@link PetData} across the interior rows.
 */
public final class PetsMenu extends AbstractSkyBlockMenu {

    private static final String TITLE        = "§dPets";
    private static final int    SUMMARY_SLOT = 4;

    private static final int[] PET_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public PetsMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        fillBorder();

        UUID id = player.getUniqueId();
        PetsManager pets = PetsManager.getInstance();

        List<String> summaryLore = new ArrayList<>();
        PetData active = pets.getActivePet(id);
        if (active == null) {
            summaryLore.add("§7No active pet.");
        } else {
            summaryLore.add("§7Active: " + active.getDisplayName());
            summaryLore.add("§7Rarity: " + active.rarity.getDisplayName());
            summaryLore.add("§7Level: §e" + active.getLevel());
            summaryLore.add("§7XP: §e" + String.format("%,d", active.getExperience()));
        }
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.BONE)
                .displayName("§d" + player.getName() + "'s Pets")
                .lore(summaryLore)
                .build(),
                e -> e.setCancelled(true));

        List<PetData> petList = pets.getPets(id);
        if (petList.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You don't own any pets yet.")
                    .build(),
                    e -> e.setCancelled(true));
        }
        for (int i = 0; i < petList.size() && i < PET_SLOTS.length; i++) {
            PetData pet = petList.get(i);
            List<String> lore = new ArrayList<>();
            lore.add("§7Rarity: " + pet.rarity.getDisplayName());
            lore.add("§7Level: §e" + pet.getLevel());
            lore.add("§7XP: §e" + String.format("%,d", pet.getExperience()));
            lore.add("");
            boolean isActive = pet.id.equals(active == null ? null : active.id);
            lore.add(isActive ? "§aCurrently Active" : "§eClick to activate");
            setItem(PET_SLOTS[i], new ItemBuilder(Material.SPAWNER)
                    .displayName(pet.getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        PetData cur = pets.getActivePet(player.getUniqueId());
                        if (cur == null || !cur.id.equals(pet.id)) {
                            pets.setActivePet(player.getUniqueId(), pet.id);
                            player.sendMessage("§dPet §f" + pet.type.getDisplayName() + " §dis now active.");
                            open(player);
                        }
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
