package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.pet.PetManager;
import com.skyblock.plugin.pet.PetManager.ActivePet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The Pets menu.
 *
 * <p>A 54-slot (6-row) menu listing the viewing player's pets (from
 * {@link PetManager}). The active pet is shown at slot 4; each owned pet is
 * placed in a content slot as a {@code PLAYER_HEAD} that, when clicked, equips
 * that pet and refreshes the menu, matching Hypixel's layout.</p>
 */
public class PetsMenu extends Menu {

    /** Slot holding the active-pet summary icon. */
    private static final int ACTIVE_SLOT = 4;

    /** Centred content slots across the middle rows, one per owned pet. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("§8Pets", 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
    }

    @Override
    protected void build() {
        fillBorder();

        PetManager pets = PetManager.getInstance();

        ActivePet active = pets.getActivePet(playerId);
        if (active != null) {
            setItem(ACTIVE_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§aActive Pet: §f" + active.getName())
                    .lore(
                            "§7Rarity: §f" + active.getRarity(),
                            "§7Level: §a" + active.getLevel())
                    .build());
        } else {
            setItem(ACTIVE_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Active Pet")
                    .lore("§7Click a pet below to equip it.")
                    .build());
        }

        UUID activeId = pets.getActivePetId(playerId);
        List<ActivePet> owned = pets.getPets(playerId);
        int count = Math.min(owned.size(), SLOTS.length);
        for (int i = 0; i < count; i++) {
            ActivePet pet = owned.get(i);
            boolean equipped = pet.getId().equals(activeId);
            setItem(SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName((equipped ? "§a" : "§f") + pet.getName())
                            .lore(
                                    "§7Rarity: §f" + pet.getRarity(),
                                    "§7Level: §a" + pet.getLevel(),
                                    equipped ? "§aCurrently equipped" : "§eClick to equip!")
                            .build(),
                    event -> {
                        pets.equip(playerId, pet.getId());
                        open((Player) event.getWhoClicked());
                    });
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
