package com.skyblock.core.pet;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.menu.PetMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the {@code /pets} command, which opens the {@link PetMenu} GUI
 * listing every owned pet grouped by rarity.
 */
public final class PetsCommand implements CommandExecutor {

    private final PetManager petManager;

    public PetsCommand(PetManager petManager) {
        this.petManager = petManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        new PetMenu(player).open(player);
        return true;
    }
}
