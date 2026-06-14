package com.skyblock.plugin.pets;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * 54-slot §aPets chest. Rows 0–4 (slots 0–44) render one PLAYER_HEAD per
 * owned pet from {@link PetManager}; row 5 is a purple-glass-pane footer.
 */
public class PetsMenu extends Menu {

    private static final int PET_SLOTS = 45; // rows 0–4
    private static final int[] FOOTER = {45, 46, 47, 48, 49, 50, 51, 52, 53};

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("§aPets", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot : FOOTER) {
            setItem(slot, pane);
        }

        PetManager manager = PetManager.getInstance();
        PetManager.PetEntry active = manager.getActivePet(playerId);
        List<PetManager.PetEntry> owned = manager.getPets(playerId);
        for (int i = 0; i < owned.size() && i < PET_SLOTS; i++) {
            PetManager.PetEntry pet = owned.get(i);
            boolean isActive = active != null && active.getId().equals(pet.getId());
            String name = pet.getType().name().replace('_', ' ');
            setItem(i, new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§a" + name + " §7Lvl " + pet.getLevel())
                    .lore(
                            "§7Rarity: §6" + pet.getRarity(),
                            "§7XP: §e" + pet.getXp(),
                            isActive ? "§aCurrently active" : "§7Click to equip")
                    .build());
        }
    }
}
