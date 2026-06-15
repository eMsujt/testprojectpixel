package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.menu.QuestsMenu} instead.
 */
@Deprecated
public class QuestMenu extends Menu {

    /** @deprecated Use {@link com.skyblock.core.menu.QuestsMenu} instead. */
    @Deprecated
    public QuestMenu(List<String> active) {
        super("§eQuests & Objectives", 6);
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.QuestsMenu(player).open(player);
    }
}
