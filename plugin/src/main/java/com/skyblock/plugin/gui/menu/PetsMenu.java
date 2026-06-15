package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PetsMenu extends Menu {

    private static final int ACTIVE_SLOT = 4;

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("§dPets", 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getProfile(playerId);
        if (profile == null) {
            return;
        }

        String active = profile.getActivePet();
        if (active != null) {
            setItem(ACTIVE_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§aActive Pet: §f" + active)
                    .lore("§eClick a pet below to change it.")
                    .build());
        } else {
            setItem(ACTIVE_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Active Pet")
                    .lore("§7Click a pet below to equip it.")
                    .build());
        }

        List<String> owned = profile.getOwnedPets();
        int count = Math.min(owned.size(), SLOTS.length);
        for (int i = 0; i < count; i++) {
            String pet = owned.get(i);
            boolean equipped = pet.equals(active);
            setItem(SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName((equipped ? "§a" : "§f") + pet)
                            .lore(equipped ? "§aCurrently equipped" : "§eClick to equip!")
                            .build(),
                    event -> {
                        profile.setActivePet(pet);
                        open((Player) event.getWhoClicked());
                    });
        }
    }

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
