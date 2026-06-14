package com.skyblock.plugin.command.pets;

import com.skyblock.core.pets.PetsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class PetsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        PetsManager manager = PetsManager.getInstance();

        PetsManager.Pet active = manager.getActivePet(id);
        if (active != null) {
            player.sendMessage("Active pet: " + active.type.name() + " [" + active.rarity.name() + "]");
        } else {
            player.sendMessage("Active pet: None");
        }

        List<PetsManager.Pet> pets = manager.getPets(id);
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
            return true;
        }

        player.sendMessage("=== Your Pets ===");
        for (PetsManager.Pet pet : pets) {
            String marker = pet.id.equals(PetsManager.getInstance().getActivePetId(id)) ? " *" : "";
            player.sendMessage("  " + pet.type.name() + " [" + pet.rarity.name() + "]" + marker);
        }
        return true;
    }
}
