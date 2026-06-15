package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.pets.PetManager;
import com.skyblock.plugin.pets.PetManager.PetEntry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PetsMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final Player player;
    private final int page;

    public PetsMenu(Player player) {
        this(player, 0);
    }

    private PetsMenu(Player player, int page) {
        super("§aPets", 6);
        this.player = player;
        this.page = page;
    }

    @Override
    protected void build() {
        fillBorder();

        PetManager petManager = PetManager.getInstance();
        List<PetEntry> owned = petManager.getPets(player.getUniqueId());
        PetEntry activePet = petManager.getActivePet(player.getUniqueId());

        int totalPages = Math.max(1, (int) Math.ceil((double) owned.size() / SLOTS_PER_PAGE));
        int start = page * SLOTS_PER_PAGE;

        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int idx = start + i;
            if (idx >= owned.size()) break;
            PetEntry pet = owned.get(idx);
            boolean equipped = activePet != null && pet.getId().equals(activePet.getId());
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName((equipped ? "§a" : "§f") + pet.getType().name())
                            .lore(
                                    "§7Rarity: §f" + pet.getRarity(),
                                    "§7Level: §a" + pet.getLevel(),
                                    "§7XP: §e" + pet.getXp(),
                                    equipped ? "§aCurrently equipped" : "§eClick to equip!")
                            .build(),
                    event -> {
                        if (equipped) {
                            petManager.setActivePet(player.getUniqueId(), null);
                        } else {
                            petManager.setActivePet(player.getUniqueId(), pet);
                        }
                        new PetsMenu(player, page).open(player);
                    });
        }

        if (owned.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You don't own any pets yet.")
                    .build());
        }

        setItem(49, new ItemBuilder(Material.BONE)
                .displayName("§aPets")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build(),
                    event -> new PetsMenu(player, prevPage).open(player));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build(),
                    event -> new PetsMenu(player, nextPage).open(player));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
