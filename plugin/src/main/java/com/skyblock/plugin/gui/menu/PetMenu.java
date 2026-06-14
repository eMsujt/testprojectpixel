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
 * The Pet menu.
 *
 * <p>A 54-slot (6-row) chest GUI titled {@code §6Pets} with a gray glass-pane
 * border. Owned pets are placed in the 28 inner content slots as
 * {@code PLAYER_HEAD} items; clicking one equips it and refreshes the view,
 * matching Hypixel's layout.</p>
 */
public class PetMenu extends Menu {

    /** Inner content slots across rows 2–5 (columns 2–8), one per owned pet. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final UUID playerId;

    public PetMenu(UUID playerId) {
        super("§6Pets", 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
    }

    @Override
    protected void build() {
        fillBorder();

        PetManager pets = PetManager.getInstance();
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

    /** Fills the outer edge with gray glass panes, matching Hypixel. */
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
