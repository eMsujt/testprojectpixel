package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.pet.PetManager;
import com.skyblock.plugin.pet.PetManager.ActivePet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PetMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;
    private static final int MAX_PETS = SLOTS_PER_PAGE;

    private final Player player;
    private final int page;

    public PetMenu(Player player) {
        this(player, 0);
    }

    private PetMenu(Player player, int page) {
        super(buildTitle(PetManager.getInstance().getPets(player.getUniqueId()).size()), 6);
        this.player = player;
        this.page = page;
    }

    private static String buildTitle(int count) {
        return "§dPets §8(§7" + count + "§8/§7" + MAX_PETS + "§8)";
    }

    @Override
    protected void build() {
        fillBorder();

        PetManager pets = PetManager.getInstance();
        java.util.UUID playerId = player.getUniqueId();
        java.util.UUID activeId = pets.getActivePetId(playerId);
        List<ActivePet> owned = pets.getPets(playerId);

        int totalPages = Math.max(1, (int) Math.ceil((double) owned.size() / SLOTS_PER_PAGE));
        int start = page * SLOTS_PER_PAGE;

        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int contentIndex = start + i;
            if (contentIndex >= owned.size()) break;
            ActivePet pet = owned.get(contentIndex);
            boolean equipped = pet.getId().equals(activeId);
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName((equipped ? "§a" : "§f") + pet.getName())
                            .lore(
                                    "§7Rarity: §f" + pet.getRarity(),
                                    "§7Level: §a" + pet.getLevel(),
                                    equipped ? "§aCurrently equipped" : "§eClick to equip!")
                            .build(),
                    event -> {
                        pets.equip(playerId, pet.getId());
                        new PetMenu(player, page).open(player);
                    });
        }

        if (owned.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You don't own any pets yet.")
                    .build());
        }

        setItem(49, new ItemBuilder(Material.BONE)
                .displayName("§5Pets")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build(),
                    event -> new PetMenu(player, prevPage).open(player));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build(),
                    event -> new PetMenu(player, nextPage).open(player));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE)
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
