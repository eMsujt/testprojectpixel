package com.skyblock.plugin.commands;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
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
        PetManager manager = PetManager.getInstance();
        Pet active = manager.getActivePet(id);
        List<Pet> pets = manager.getPets(id);
        player.sendMessage("=== Pets ===");
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
            return;
        }
        for (Pet pet : pets) {
            int level = manager.getLevel(id, pet.type);
            String marker = (active != null && pet.id.equals(active.id)) ? " (active)" : "";
            player.sendMessage("  " + pet.type.getDisplayName() + " [" + pet.rarity.name() + "] Lv" + level + marker);
        }
    }

    private void handleActive(Player player) {
        UUID id = player.getUniqueId();
        PetManager manager = PetManager.getInstance();
        Pet pet = manager.getActivePet(id);
        player.sendMessage("=== Active Pet ===");
        if (pet == null) {
            player.sendMessage("You have no active pet.");
        } else {
            int level = manager.getLevel(id, pet.type);
            player.sendMessage("Name: " + pet.type.getDisplayName());
            player.sendMessage("Rarity: " + pet.rarity.name());
            player.sendMessage("Level: " + level);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Pets Commands ===");
        player.sendMessage("/pets          — list your active pet");
        player.sendMessage("/pets active   — show active pet details");
    }
}
