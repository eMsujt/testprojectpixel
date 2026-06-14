package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.PetsManager;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

/**
 * The Pets hub menu.
 *
 * <p>A 54-slot (6-row) menu listing the viewing player's owned pets (from
 * {@link PetsManager}), one {@code PLAYER_HEAD} icon per pet showing its
 * rarity and level. The currently active pet is highlighted in the lore.</p>
 */
public class PetsMenu extends Menu {

    /** Centred slots across two rows, one per pet. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("Pets", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        PetsManager pets = PetsManager.getInstance();
        PetsManager.Pet active = pets.getActivePet(playerId);
        List<PetsManager.Pet> owned = pets.getPets(playerId);
        for (int i = 0; i < owned.size() && i < SLOTS.length; i++) {
            PetsManager.Pet pet = owned.get(i);
            boolean isActive = active != null && active.getId().equals(pet.getId());
            setItem(SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§a" + pet.getName() + " §7Lvl " + pet.getLevel())
                    .lore(
                            "§7Rarity: §6" + pet.getRarity(),
                            "§7Level: §a" + pet.getLevel(),
                            isActive ? "§aCurrently active" : "§7Click to view")
                    .build());
        }
    }
}
