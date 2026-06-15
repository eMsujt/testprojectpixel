package com.skyblock.core.command;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.PetRarity;
import com.skyblock.core.manager.PetManager.PetType;
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
 * <p>Usage:
 * <ul>
 *   <li>{@code /pets list}                    — shows all pets and their levels</li>
 *   <li>{@code /pets equip <pet> [rarity]}    — equips a pet as the active pet</li>
 *   <li>{@code /pets unequip}                 — removes the currently active pet</li>
 *   <li>{@code /pets info [pet]}              — shows details for active or specified pet</li>
 * </ul>
 * </p>
 */
public final class PetsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = List.of("list", "equip", "unequip", "info");

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

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            sendPetList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "equip" -> handleEquip(player, args);
            case "unequip" -> handleUnequip(player);
            case "info" -> handleInfo(player, args);
            default -> player.sendMessage("Unknown subcommand: " + args[0] + ". Use /pets list, equip, unequip, or info.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("equip")) {
            return Arrays.stream(PetType.values())
                    .map(p -> p.name().toLowerCase())
                    .filter(name -> name.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("equip")) {
            return Arrays.stream(PetRarity.values())
                    .map(r -> r.name().toLowerCase())
                    .filter(name -> name.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return Arrays.stream(PetType.values())
                    .map(p -> p.name().toLowerCase())
                    .filter(name -> name.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendPetList(Player player) {
        UUID id = player.getUniqueId();
        PetManager.Pet active = petManager.getActivePet(id);
        player.sendMessage("=== Your Pets ===");
        for (PetType type : PetType.values()) {
            int level = petManager.getLevel(id, type);
            boolean isActive = active != null && active.type == type;
            String suffix = isActive ? " [ACTIVE]" : "";
            player.sendMessage(capitalize(type.name()) + ": Level " + level + "/" + PetManager.MAX_LEVEL + suffix);
        }
    }

    private void handleEquip(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /pets equip <pet> [rarity]");
            return;
        }
        PetType type;
        try {
            type = PetType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown pet: " + args[1] + ".");
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
        PetManager.Pet p = petManager.addPet(player.getUniqueId(), type, rarity);
        petManager.equipPet(player.getUniqueId(), p.id);
        player.sendMessage("Equipped " + capitalize(type.name()) + " (" + capitalize(rarity.name()) + ").");
    }

    private void handleUnequip(Player player) {
        boolean had = petManager.unequipPet(player.getUniqueId());
        if (had) {
            player.sendMessage("Pet unequipped.");
        } else {
            player.sendMessage("You have no active pet.");
        }
    }

    private void handleInfo(Player player, String[] args) {
        UUID id = player.getUniqueId();
        if (args.length >= 2) {
            PetType type;
            try {
                type = PetType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown pet: " + args[1] + ".");
                return;
            }
            long xp = petManager.getExperience(id, type);
            int level = petManager.getLevel(id, type);
            player.sendMessage("=== " + capitalize(type.name()) + " ===");
            player.sendMessage("Level: " + level + "/" + PetManager.MAX_LEVEL);
            player.sendMessage("Total XP: " + xp);
        } else {
            PetManager.Pet data = petManager.getActivePet(id);
            if (data == null) {
                player.sendMessage("You have no active pet. Use /pets equip <pet> to equip one.");
                return;
            }
            player.sendMessage("=== Active Pet: " + capitalize(data.type.name()) + " ===");
            player.sendMessage("Rarity: " + capitalize(data.rarity.name()));
            player.sendMessage("Level: " + petManager.getLevel(id, data.type) + "/" + PetManager.MAX_LEVEL);
            player.sendMessage("Total XP: " + petManager.getExperience(id, data.type));
        }
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}
