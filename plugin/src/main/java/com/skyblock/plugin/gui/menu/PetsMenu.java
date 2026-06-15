package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.pets.PetManager;
import com.skyblock.plugin.pets.PetManager.PetEntry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PetsMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("§aPets", 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
    }

    @Override
    protected void build() {
        fillBorder();

        PetManager pets = PetManager.getInstance();
        PetEntry active = pets.getActivePet(playerId);
        List<PetEntry> owned = pets.getPets(playerId);

        int count = Math.min(owned.size(), INNER_SLOTS.length);
        for (int i = 0; i < count; i++) {
            PetEntry pet = owned.get(i);
            boolean equipped = active != null && pet.getId().equals(active.getId());
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName((equipped ? "§a" : "§f") + pet.getType().name())
                            .lore(
                                    "§7Rarity: §f" + pet.getRarity(),
                                    "§7Level: §a" + pet.getLevel(),
                                    equipped ? "§aCurrently equipped" : "§eClick to equip!")
                            .build(),
                    event -> {
                        pets.setActivePet(playerId, pet);
                        open((Player) event.getWhoClicked());
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }
}
