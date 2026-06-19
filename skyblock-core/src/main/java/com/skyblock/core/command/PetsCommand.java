package com.skyblock.core.command;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.menu.PetMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class PetsCommand extends PlayerCommand {

    private final PetManager petManager;

    public PetsCommand(PetManager petManager) {
        this.petManager = petManager;
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new PetMenu(player).open(player);
        return true;
    }
}
