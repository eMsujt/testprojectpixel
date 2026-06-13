package com.skyblock.core.pets;

import com.skyblock.core.pets.PetsManager.Pet;
import com.skyblock.core.pets.PetsManager.PetType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PetsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "equip", "unequip", "level");

    private final PetsManager manager;

    public PetsCommand(PetsManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "equip"   -> handleEquip(player, args);
            case "unequip" -> handleUnequip(player);
            case "level"   -> handleLevel(player, args);
            default        -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("equip") || args[0].equalsIgnoreCase("level"))) {
            if (!(sender instanceof Player player)) return Collections.emptyList();
            String prefix = args[1].toLowerCase();
            return manager.getPets(player.getUniqueId()).stream()
                    .map(p -> p.id.toString())
                    .filter(id -> id.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<Pet> pets = manager.getPets(player.getUniqueId());
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
            return;
        }
        Pet active = manager.getActivePet(player.getUniqueId());
        player.sendMessage("=== Your Pets ===");
        for (Pet pet : pets) {
            boolean isActive = active != null && active.id.equals(pet.id);
            String suffix = isActive ? " [ACTIVE]" : "";
            player.sendMessage("  " + pet.type.name() + " (Lvl " + pet.level + ") — " + pet.id + suffix);
        }
    }

    private void handleEquip(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /pets equip <petId>");
            return;
        }
        UUID petId;
        try {
            petId = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid pet ID: " + args[1] + ".");
            return;
        }
        if (manager.equipPet(player.getUniqueId(), petId)) {
            player.sendMessage("Pet equipped.");
        } else {
            player.sendMessage("No pet with that ID found in your collection.");
        }
    }

    private void handleUnequip(Player player) {
        if (manager.unequipPet(player.getUniqueId())) {
            player.sendMessage("Pet unequipped.");
        } else {
            player.sendMessage("You have no active pet.");
        }
    }

    private void handleLevel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /pets level <petId>");
            return;
        }
        UUID petId;
        try {
            petId = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid pet ID: " + args[1] + ".");
            return;
        }
        if (manager.levelUpPet(player.getUniqueId(), petId)) {
            player.sendMessage("Pet leveled up.");
        } else {
            player.sendMessage("No pet with that ID found in your collection.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Pets Commands ===");
        player.sendMessage("/pets list             — list all owned pets");
        player.sendMessage("/pets equip <petId>   — equip a pet by ID");
        player.sendMessage("/pets unequip         — unequip active pet");
        player.sendMessage("/pets level <petId>   — level up a pet by ID");
    }
}
