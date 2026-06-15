package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.PetsMenu} instead.
 */
@Deprecated
public class PetMenu extends Menu {

    public PetMenu(Player player) {
        super("§dPets", 6);
    }

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.PetsMenu(player).open(player);
    }

    @Override
    protected void build() {}
}
