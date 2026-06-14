package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.PetsManager;
import com.skyblock.plugin.managers.PetsManager.Pet;
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

        if (args.length == 0) {
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "active" -> handleActive(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        PetsManager manager = PetsManager.getInstance();
        List<Pet> pets = manager.getPets(id);
        Pet activePet = manager.getActivePet(id);
        UUID activeId = activePet != null ? activePet.getId() : null;
        player.sendMessage("=== Pets ===");
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
        } else {
            for (Pet pet : pets) {
                String marker = (activeId != null && pet.getId().equals(activeId)) ? " [ACTIVE]" : "";
                player.sendMessage(pet.getName() + " [" + pet.getRarity() + "] Lv" + pet.getLevel() + marker);
            }
        }
    }

    private void handleActive(Player player) {
        UUID id = player.getUniqueId();
        PetsManager manager = PetsManager.getInstance();
        Pet pet = manager.getActivePet(id);
        player.sendMessage("=== Active Pet ===");
        if (pet == null) {
            player.sendMessage("You have no active pet.");
        } else {
            player.sendMessage("Name: " + pet.getName());
            player.sendMessage("Rarity: " + pet.getRarity());
            player.sendMessage("Level: " + pet.getLevel());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Pets Commands ===");
        player.sendMessage("/pets          — list your active pet");
        player.sendMessage("/pets active   — show active pet details");
    }
}
