package com.skyblock.pets.gui;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.menu.Menu;
import com.skyblock.items.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Canonical paginated Pets menu. 54-slot chest; gray-pane border top and bottom rows;
 * up to 28 pet icons per page with equip/unequip toggle; page-navigation arrows at
 * slots 45/53; page info bone at slot 49.
 *
 * <p>All other PetsMenu/PetMenu classes in the project are deprecated stubs that
 * delegate here.</p>
 */
public class PetsMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final UUID playerId;
    private final int page;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public PetsMenu(Player player) {
        this(player.getUniqueId(), 0);
    }

    public PetsMenu(UUID playerId) {
        this(playerId, 0);
    }

    private PetsMenu(UUID playerId, int page) {
        this.playerId = playerId;
        this.page = page;
    }

    @Override
    public void open(Player player) {
        handlers.clear();

        PetManager petManager = PetManager.getInstance();
        List<Pet> owned = petManager.getPets(playerId);
        Pet activePet = petManager.getActivePet(playerId);
        UUID activeId = activePet != null ? activePet.id : null;

        int totalPages = Math.max(1, (int) Math.ceil((double) owned.size() / SLOTS_PER_PAGE));
        inventory = Bukkit.createInventory(this, 54, "§dPets");

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) inventory.setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) inventory.setItem(slot, pane);

        int start = page * SLOTS_PER_PAGE;
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int idx = start + i;
            if (idx >= owned.size()) break;
            Pet pet = owned.get(idx);
            boolean equipped = pet.id.equals(activeId);
            int level = petManager.getLevel(playerId, pet.type);
            long xp = petManager.getExperience(playerId, pet.type);
            ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName((equipped ? "§a" : "§f") + pet.type.getDisplayName())
                    .lore(
                            "§7Rarity: §f" + pet.rarity.getDisplayName(),
                            "§7Level: §a" + level,
                            "§7XP: §e" + xp,
                            equipped ? "§aCurrently equipped" : "§eClick to equip!")
                    .build();
            int slot = INNER_SLOTS[i];
            inventory.setItem(slot, item);
            UUID petId = pet.id;
            handlers.put(slot, event -> {
                if (equipped) {
                    petManager.unequipPet(playerId);
                } else {
                    petManager.equipPet(playerId, petId);
                }
                new PetsMenu(playerId, page).open((Player) event.getWhoClicked());
            });
        }

        if (owned.isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You don't own any pets yet.")
                    .build());
        }

        inventory.setItem(49, new ItemBuilder(Material.BONE)
                .displayName("§aPets")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            inventory.setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build());
            handlers.put(45, event -> new PetsMenu(playerId, prevPage).open((Player) event.getWhoClicked()));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            inventory.setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build());
            handlers.put(53, event -> new PetsMenu(playerId, nextPage).open((Player) event.getWhoClicked()));
        }

        player.openInventory(inventory);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
