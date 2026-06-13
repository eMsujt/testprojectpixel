package com.skyblock.core.hub;

import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command as a comprehensive router.
 *
 * <p>With no subcommand the SkyBlock main menu opens. With a subcommand the
 * request is dispatched to the appropriate existing command or built-in handler:
 * <ul>
 *   <li>{@code /skyblock hub}             — teleport to the hub world spawn</li>
 *   <li>{@code /skyblock bank [args]}     — delegates to {@code /bank}</li>
 *   <li>{@code /skyblock auction [args]}  — delegates to {@code /auction}</li>
 *   <li>{@code /skyblock bazaar [args]}   — delegates to {@code /bazaar}</li>
 *   <li>{@code /skyblock island [args]}   — delegates to {@code /island}</li>
 *   <li>{@code /skyblock skills [args]}   — delegates to {@code /skills}</li>
 *   <li>{@code /skyblock pets [args]}     — delegates to {@code /pets}</li>
 *   <li>{@code /skyblock profile [args]}  — delegates to {@code /profile}</li>
 *   <li>{@code /skyblock quest [args]}    — delegates to {@code /quest}</li>
 *   <li>{@code /skyblock minion [args]}   — delegates to {@code /minion}</li>
 *   <li>{@code /skyblock fishing [args]}  — delegates to {@code /fishing}</li>
 *   <li>{@code /skyblock mining [args]}   — delegates to {@code /mining}</li>
 *   <li>{@code /skyblock slayer [args]}   — delegates to {@code /slay}</li>
 *   <li>{@code /skyblock hotm [args]}     — delegates to {@code /hotm}</li>
 *   <li>{@code /skyblock garden [args]}   — delegates to {@code /garden}</li>
 *   <li>{@code /skyblock backpack [args]} — delegates to {@code /backpack}</li>
 *   <li>{@code /skyblock reforge [args]}  — delegates to {@code /reforge}</li>
 *   <li>{@code /skyblock achievement [args]} — delegates to {@code /achievement}</li>
 *   <li>{@code /skyblock dungeon [args]}  — delegates to {@code /dungeon}</li>
 *   <li>{@code /skyblock help}            — lists all available subcommands</li>
 * </ul>
 * </p>
 */
public final class SkyblockHubCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "hub", "bank", "auction", "bazaar", "island", "skills", "pets",
            "profile", "quest", "minion", "fishing", "mining", "slayer",
            "hotm", "garden", "backpack", "reforge", "achievement", "dungeon", "help"
    );

    private final SkyBlockMenuManager menuManager;
    private final String hubWorldName;

    public SkyblockHubCommand(SkyBlockMenuManager menuManager, String hubWorldName) {
        this.menuManager = Objects.requireNonNull(menuManager, "menuManager");
        this.hubWorldName = Objects.requireNonNull(hubWorldName, "hubWorldName");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        if (args.length == 0) {
            menuManager.openMainMenu(player);
            return true;
        }
        String sub = args[0].toLowerCase();
        String[] rest = Arrays.copyOfRange(args, 1, args.length);
        switch (sub) {
            case "hub" -> {
                World hub = Bukkit.getWorld(hubWorldName);
                if (hub == null) {
                    player.sendMessage("Hub world is not available.");
                } else {
                    player.teleport(hub.getSpawnLocation());
                    player.sendMessage("Teleported to the hub!");
                }
            }
            case "help" -> {
                player.sendMessage("SkyBlock subcommands: " + String.join(", ", SUBCOMMANDS));
            }
            default -> {
                String target = resolveCommand(sub);
                String delegated = rest.length > 0 ? target + " " + String.join(" ", rest) : target;
                player.performCommand(delegated);
            }
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
        return Collections.emptyList();
    }

    private static String resolveCommand(String sub) {
        return switch (sub) {
            case "auction", "ah" -> "auction";
            case "bazaar", "bz" -> "bazaar";
            case "quests" -> "quest";
            case "minions" -> "minion";
            case "slayer" -> "slay";
            default -> sub;
        };
    }
}
