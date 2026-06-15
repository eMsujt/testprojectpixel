package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.menu.PetsMenu} instead.
 */
@Deprecated
public class PetsMenu extends Menu {

    private final UUID playerId;

    public PetsMenu(UUID playerId) {
        super("§dPets", 6);
        this.playerId = playerId;
    }

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.PetsMenu(playerId).open(player);
    }

    @Override
    protected void build() {}
}
