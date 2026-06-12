package com.skyblock.core.pet;

import com.skyblock.core.pet.PetManager.PetDefinition;
import com.skyblock.core.pets.PetManager.PetData;
import com.skyblock.core.pets.PetManager.PetType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /pet} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /pet list}          — lists all pets with their stat bonuses</li>
 *   <li>{@code /pet info <type>}   — shows details for a specific pet type</li>
 *   <li>{@code /pet active}        — shows the player's active pet and its bonus</li>
 * </ul>
 * </p>
 */
public final class PetCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = List.of("list", "info", "active");

    private final PetManager petDefinitionManager;
    private final com.skyblock.core.pets.PetManager petManager;

    public PetCommand(PetManager petDefinitionManager,
                      com.skyblock.core.pets.PetManager petManager) {
        this.petDefinitionManager = petDefinitionManager;
        this.petManager = petManager;
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
            case "info" -> handleInfo(player, args);
            case "active" -> handleActive(player);
            default -> player.sendMessage(
                    "Unknown subcommand: " + args[0] + ". Use /pet list, info, or active.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
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

    private void handleList(Player player) {
        player.sendMessage("=== Pet Definitions ===");
        for (Map.Entry<PetType, PetDefinition> entry
                : petDefinitionManager.getAllDefinitions().entrySet()) {
            PetDefinition def = entry.getValue();
            player.sendMessage(String.format("%s — %s: +%.1f %s/level",
                    def.displayName(),
                    def.description(),
                    def.bonusPerLevel(),
                    capitalize(def.bonusStat().name())));
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /pet info <type>");
            return;
        }
        PetType type;
        try {
            type = PetType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown pet type: " + args[1] + ".");
            return;
        }
        PetDefinition def = petDefinitionManager.getDefinition(type);
        int level = petManager.getLevel(player.getUniqueId(), type);
        double bonus = petDefinitionManager.getBonus(type, level);
        player.sendMessage("=== " + def.displayName() + " ===");
        player.sendMessage("Description: " + def.description());
        player.sendMessage("Bonus stat: " + capitalize(def.bonusStat().name()));
        player.sendMessage(String.format("Bonus per level: +%.1f", def.bonusPerLevel()));
        player.sendMessage(String.format("Your level: %d  |  Current bonus: +%.1f", level, bonus));
    }

    private void handleActive(Player player) {
        PetData data = petManager.getActivePet(player.getUniqueId());
        if (data == null) {
            player.sendMessage("You have no active pet. Use /pets equip <type> to equip one.");
            return;
        }
        PetDefinition def = petDefinitionManager.getDefinition(data.type);
        double bonus = petDefinitionManager.getBonus(data.type, data.getLevel());
        player.sendMessage("=== Active Pet: " + def.displayName() + " ===");
        player.sendMessage("Rarity: " + capitalize(data.rarity.name()));
        player.sendMessage("Level: " + data.getLevel());
        player.sendMessage(String.format("Bonus: +%.1f %s",
                bonus, capitalize(def.bonusStat().name())));
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}
