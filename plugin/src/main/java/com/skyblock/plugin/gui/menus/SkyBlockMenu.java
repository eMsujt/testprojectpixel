package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead.
 */
@Deprecated
public class SkyBlockMenu extends Menu {

    public SkyBlockMenu() {
        super("§aSkyBlock Menu", 6);
    }

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.SkyBlockMainMenu(player).open(player);
    }

    @Override
    protected void build() {}
}
