package com.skyblock.core.pets.command;

import com.skyblock.core.pets.PetsManager;
import com.skyblock.core.pets.PetsManager.Pet;
import com.skyblock.core.pets.PetsManager.PetRarity;
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

/**
 * Handles the {@code /pets} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /pets list}                    — list all owned pets</li>
 *   <li>{@code /pets add <type> [rarity]}     — add a pet to the collection</li>
 *   <li>{@code /pets equip <petId>}           — equip a pet by its UUID</li>
 *   <li>{@code /pets unequip}                 — unequip the active pet</li>
 *   <li>{@code /pets active}                  — show the currently active pet</li>
 *   <li>{@code /pets reset}                   — clear all pet data</li>
 * </ul>
 * </p>
 */
public final class PetsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "add", "equip", "unequip", "active", "reset");

    private final PetsManager petsManager;

    public PetsCommand(PetsManager petsManager) {
        this.petsManager = petsManager;
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
            case "add"      -> handleAdd(player, args);
            case "equip"    -> handleEquip(player, args);
            case "unequip"  -> handleUnequip(player);
            case "active"   -> handleActive(player);
            case "reset"    -> handleReset(player);
            default         -> sendHelp(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(PetType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            String prefix = args[2].toLowerCase();
            return Arrays.stream(PetRarity.values())
                    .map(r -> r.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<Pet> pets = petsManager.getPets(player.getUniqueId());
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
            return;
        }
        Pet active = petsManager.getActivePet(player.getUniqueId());
        player.sendMessage("=== Your Pets ===");
        for (Pet pet : pets) {
            boolean isActive = active != null && active.id.equals(pet.id);
            String suffix = isActive ? " [ACTIVE]" : "";
            player.sendMessage("  " + pet.type.name() + " (" + pet.rarity.name() + ") — " + pet.id + suffix);
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /pets add <type> [rarity]");
            return;
        }
        PetType type;
        try {
            type = PetType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown pet type: " + args[1] + ".");
            return;
        }
        PetRarity rarity = PetRarity.COMMON;
        if (args.length >= 3) {
            try {
                rarity = PetRarity.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown rarity: " + args[2] + ". Defaulting to COMMON.");
            }
        }
        Pet pet = petsManager.addPet(player.getUniqueId(), type, rarity);
        player.sendMessage("Added " + type.name() + " (" + rarity.name() + ") to your collection. ID: " + pet.id);
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
        if (petsManager.equipPet(player.getUniqueId(), petId)) {
            player.sendMessage("Pet equipped.");
        } else {
            player.sendMessage("No pet with that ID found in your collection.");
        }
    }

    private void handleUnequip(Player player) {
        if (petsManager.unequipPet(player.getUniqueId())) {
            player.sendMessage("Pet unequipped.");
        } else {
            player.sendMessage("You have no active pet.");
        }
    }

    private void handleActive(Player player) {
        Pet active = petsManager.getActivePet(player.getUniqueId());
        if (active == null) {
            player.sendMessage("You have no active pet. Use /pets equip <petId> to equip one.");
            return;
        }
        player.sendMessage("=== Active Pet ===");
        player.sendMessage("Type: " + active.type.name());
        player.sendMessage("Rarity: " + active.rarity.name());
        player.sendMessage("ID: " + active.id);
    }

    private void handleReset(Player player) {
        petsManager.reset(player.getUniqueId());
        player.sendMessage("All pet data has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Pets Commands ===");
        player.sendMessage("/pets list                    — list all owned pets");
        player.sendMessage("/pets add <type> [rarity]    — add a pet to your collection");
        player.sendMessage("/pets equip <petId>          — equip a pet by ID");
        player.sendMessage("/pets unequip                — unequip active pet");
        player.sendMessage("/pets active                 — show active pet");
        player.sendMessage("/pets reset                  — clear all pet data");
    }
}
