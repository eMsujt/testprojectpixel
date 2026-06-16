package com.skyblock.pets.gui;

import com.skyblock.core.menu.PetMenu;
import org.bukkit.entity.Player;

import java.util.UUID;

/** @deprecated Use {@link PetMenu} instead. */
@Deprecated
public class PetsMenu extends PetMenu {

    public PetsMenu(Player player) {
        super(player);
    }

    public PetsMenu(UUID playerId) {
        super(playerId);
    }
}
