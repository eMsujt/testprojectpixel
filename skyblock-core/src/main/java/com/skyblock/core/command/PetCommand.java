package com.skyblock.core.command;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.menu.PetMenu;
import com.skyblock.core.model.Rarity;
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
 * Handles the {@code /pet} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /pet list}                  — list all pets with their levels</li>
 *   <li>{@code /pet equip <type> [rarity]} — equip a pet as the active pet</li>
 *   <li>{@code /pet unequip}               — unequip the active pet</li>
 *   <li>{@code /pet info [type]}           — show details for active or named pet</li>
 * </ul>
 * </p>
 */
public final class PetCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "equip", "unequip", "info");

    private final PetManager petManager;

    public PetCommand(PetManager petManager) {
        this.petManager = petManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new PetMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "equip"   -> handleEquip(player, args);
            case "unequip" -> handleUnequip(player);
            case "info"    -> handleInfo(player, args);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("equip") || args[0].equalsIgnoreCase("info"))) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(PetType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("equip")) {
            String prefix = args[2].toLowerCase();
            return Arrays.stream(Rarity.values())
                    .map(r -> r.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        PetManager.Pet active = petManager.getActivePet(id);
        player.sendMessage("=== Your Pets ===");
        for (PetType type : PetType.values()) {
            int level = petManager.getLevel(id, type);
            boolean isActive = active != null && active.type == type;
            String suffix = isActive ? " [ACTIVE]" : "";
            player.sendMessage("  " + type.getDisplayName() + ": Level " + level + "/" + PetManager.MAX_LEVEL + suffix);
        }
    }

    private void handleEquip(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /pet equip <type> [rarity]");
            return;
        }
        PetType type;
        try {
            type = PetType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown pet type: " + args[1] + ".");
            return;
        }
        Rarity rarity = Rarity.COMMON;
        if (args.length >= 3) {
            try {
                rarity = Rarity.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown rarity: " + args[2] + ". Defaulting to COMMON.");
            }
        }
        PetManager.Pet p = petManager.addPet(player.getUniqueId(), type, rarity);
        petManager.equipPet(player.getUniqueId(), p.id);
        player.sendMessage("Equipped " + type.getDisplayName() + " (" + rarity.getDisplayName() + ").");
    }

    private void handleUnequip(Player player) {
        if (petManager.unequipPet(player.getUniqueId())) {
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
                player.sendMessage("Unknown pet type: " + args[1] + ".");
                return;
            }
            int level = petManager.getLevel(id, type);
            long xp = petManager.getExperience(id, type);
            player.sendMessage("=== " + type.getDisplayName() + " ===");
            player.sendMessage("Level: " + level + "/" + PetManager.MAX_LEVEL);
            player.sendMessage("Total XP: " + xp);
        } else {
            PetManager.Pet data = petManager.getActivePet(id);
            if (data == null) {
                player.sendMessage("You have no active pet. Use /pet equip <type> to equip one.");
                return;
            }
            player.sendMessage("=== Active Pet: " + data.type.getDisplayName() + " ===");
            player.sendMessage("Rarity: " + data.rarity.getDisplayName());
            player.sendMessage("Level: " + petManager.getLevel(id, data.type) + "/" + PetManager.MAX_LEVEL);
            player.sendMessage("Total XP: " + petManager.getExperience(id, data.type));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Pet Commands ===");
        player.sendMessage("/pet list                  — list all pets");
        player.sendMessage("/pet equip <type> [rarity] — equip a pet");
        player.sendMessage("/pet unequip               — unequip active pet");
        player.sendMessage("/pet info [type]           — show pet details");
    }
}
