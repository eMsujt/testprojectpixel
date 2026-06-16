package com.skyblock.core.menu;

import org.bukkit.entity.Player;

import java.util.UUID;

/** @deprecated Use {@link com.skyblock.core.pets.gui.PetsMenu} instead. */
@Deprecated
public final class PetsMenu extends com.skyblock.core.pets.gui.PetsMenu {
    public PetsMenu(Player player) {
        super(player);
    }

    public PetsMenu(UUID playerId) {
        super(playerId);
    }
}
