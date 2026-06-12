package com.skyblock.core.ability;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /ability} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /ability list}              — show all unlocked abilities</li>
 *   <li>{@code /ability info <type>}       — show ability details and cooldown</li>
 *   <li>{@code /ability equip <type>}      — equip an unlocked ability</li>
 *   <li>{@code /ability unequip}           — clear the active ability slot</li>
 *   <li>{@code /ability unlock <type>}     — (op) unlock an ability for yourself</li>
 * </ul>
 * </p>
 */
public final class AbilityCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "info", "equip", "unequip", "unlock");
    private static final List<String> TYPE_NAMES = Arrays.stream(AbilityManager.AbilityType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private final AbilityManager abilityManager;

    public AbilityCommand(AbilityManager abilityManager) {
        this.abilityManager = abilityManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /ability <list|info|equip|unequip|unlock>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"     -> handleList(player);
            case "info"     -> handleInfo(player, args);
            case "equip"    -> handleEquip(player, args);
            case "unequip"  -> handleUnequip(player);
            case "unlock"   -> handleUnlock(player, args);
            default         -> player.sendMessage("Unknown subcommand. Usage: /ability <list|info|equip|unequip|unlock>");
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
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info") || sub.equals("equip") || sub.equals("unlock")) {
                String prefix = args[1].toLowerCase();
                return TYPE_NAMES.stream()
                        .filter(t -> t.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        Set<AbilityManager.AbilityType> unlocked = abilityManager.getUnlocked(player.getUniqueId());
        if (unlocked.isEmpty()) {
            player.sendMessage("You have no unlocked abilities.");
            return;
        }
        AbilityManager.AbilityType active = abilityManager.getActive(player.getUniqueId());
        player.sendMessage("=== Your Abilities ===");
        for (AbilityManager.AbilityType type : unlocked) {
            String marker = type.equals(active) ? " [ACTIVE]" : "";
            player.sendMessage("  " + capitalize(type.name()) + marker
                    + " (cooldown: " + type.cooldownSeconds + "s)");
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ability info <type>");
            return;
        }
        AbilityManager.AbilityType type = parseType(player, args[1]);
        if (type == null) return;
        long remaining = abilityManager.getRemainingCooldown(player.getUniqueId(), type);
        boolean unlocked = abilityManager.isUnlocked(player.getUniqueId(), type);
        AbilityManager.AbilityType active = abilityManager.getActive(player.getUniqueId());
        player.sendMessage("=== " + capitalize(type.name()) + " ===");
        player.sendMessage("  Cooldown: " + type.cooldownSeconds + "s");
        player.sendMessage("  Unlocked: " + (unlocked ? "Yes" : "No"));
        player.sendMessage("  Active: " + (type.equals(active) ? "Yes" : "No"));
        player.sendMessage("  Remaining cooldown: " + remaining + "s");
    }

    private void handleEquip(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ability equip <type>");
            return;
        }
        AbilityManager.AbilityType type = parseType(player, args[1]);
        if (type == null) return;
        if (!abilityManager.isUnlocked(player.getUniqueId(), type)) {
            player.sendMessage("You have not unlocked " + capitalize(type.name()) + ".");
            return;
        }
        abilityManager.setActive(player.getUniqueId(), type);
        player.sendMessage(capitalize(type.name()) + " is now your active ability.");
    }

    private void handleUnequip(Player player) {
        AbilityManager.AbilityType active = abilityManager.getActive(player.getUniqueId());
        if (active == null) {
            player.sendMessage("You have no active ability equipped.");
            return;
        }
        abilityManager.clearActive(player.getUniqueId());
        player.sendMessage("Unequipped " + capitalize(active.name()) + ".");
    }

    private void handleUnlock(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /ability unlock <type>");
            return;
        }
        AbilityManager.AbilityType type = parseType(player, args[1]);
        if (type == null) return;
        if (abilityManager.isUnlocked(player.getUniqueId(), type)) {
            player.sendMessage("You have already unlocked " + capitalize(type.name()) + ".");
            return;
        }
        abilityManager.unlock(player.getUniqueId(), type);
        player.sendMessage("Unlocked ability: " + capitalize(type.name()) + ".");
    }

    /** Parses an ability type name, sending an error to the player on failure. */
    private AbilityManager.AbilityType parseType(Player player, String input) {
        try {
            return AbilityManager.AbilityType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown ability: " + input
                    + ". Valid abilities: " + String.join(", ", TYPE_NAMES));
            return null;
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
