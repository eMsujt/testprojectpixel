package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PetsMenu extends Menu {

    private final Player player;

    public PetsMenu(Player player) {
        super("§aPets", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        String active = profile.getActivePet();
        List<String> owned = profile.getOwnedPets();

        int count = Math.min(owned.size(), 34);
        for (int i = 0; i < count; i++) {
            String pet = owned.get(i);
            boolean equipped = pet.equals(active);
            setItem(10 + i, new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName((equipped ? "§a" : "§f") + pet)
                            .lore(equipped ? "§aCurrently equipped" : "§eClick to equip!")
                            .build(),
                    event -> {
                        profile.setActivePet(pet);
                        open((Player) event.getWhoClicked());
                    });
        }
    }
}
