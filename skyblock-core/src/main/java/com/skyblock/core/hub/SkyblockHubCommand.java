package com.skyblock.core.hub;

import com.skyblock.core.menu.SkyBlockMenuManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
            "hub", "bank", "mayor", "auction", "ah", "bazaar", "island", "skills", "pets",
            "profile", "quest", "minion", "fishing", "mining", "slayer",
            "hotm", "garden", "collections", "backpack", "reforge", "achievement",
            "dungeon", "stats", "enchanting", "kuudra", "crafting", "trade",
            "event", "foraging", "guild", "forge", "coop", "crimson",
            "booster", "warp", "network", "mailbox", "mail", "title", "friend", "run", "menu", "help"
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
                if (rest.length > 0 && rest[0].equalsIgnoreCase("open")) {
                    World hub = Bukkit.getWorld(hubWorldName);
                    if (hub == null) {
                        player.sendMessage("§cHub world is not available.");
                    } else {
                        player.teleport(hub.getSpawnLocation());
                        player.sendMessage("§aTeleported to the Hub!");
                    }
                } else {
                    sendHubMenu(player);
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

    private static void sendHubMenu(Player player) {
        player.sendMessage("§6§l--- SkyBlock Hub ---");

        TextComponent hubLabel = new TextComponent("§7Teleport to the Hub  ");
        TextComponent hubOpen = new TextComponent("§a§l[Open]");
        hubOpen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skyblock hub open"));
        hubOpen.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§eClick to teleport to the Hub").create()));
        TextComponent hubLine = new TextComponent(hubLabel);
        hubLine.addExtra(hubOpen);
        player.spigot().sendMessage(hubLine);

        player.sendMessage("§e§lManagers:");
        String[][] managers = {
            {"Garden",     "garden"},
            {"Slayer",     "slayer"},
            {"Pets",       "pets"},
            {"Skills",     "skills"},
            {"Collections", "collections"},
            {"Bank",       "bank"},
            {"Mayor",      "mayor"},
            {"Auction",    "auction"},
            {"Bazaar",     "bazaar"},
            {"Island",     "island"},
            {"Profile",    "profile"},
            {"Quest",      "quest"},
            {"Minion",     "minion"},
            {"Fishing",    "fishing"},
            {"Mining",     "mining"},
            {"HOTM",       "hotm"},
            {"Backpack",   "backpack"},
            {"Reforge",    "reforge"},
            {"Achievement","achievement"},
            {"Dungeon",    "dungeon"},
            {"Stats",      "stats"},
            {"Enchanting", "enchanting"},
            {"Kuudra",     "kuudra"},
            {"Crafting",   "crafting"},
            {"Trade",      "trade"},
            {"Event",      "event"},
            {"Foraging",   "foraging"},
            {"Guild",      "guild"},
            {"Forge",      "forge"},
            {"Coop",       "coop"},
            {"Crimson",    "crimson"},
            {"Booster",    "booster"},
            {"Warp",       "warp"},
            {"Network",    "network"},
            {"Mailbox",    "mailbox"},
            {"Title",      "title"},
            {"Friend",     "friend"},
            {"Run",        "run"},
        };
        for (String[] entry : managers) {
            String name = entry[0];
            String sub = entry[1];
            TextComponent label = new TextComponent("§7" + name + "  ");
            TextComponent btn = new TextComponent("§a§l[Open]");
            btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skyblock " + sub));
            btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§eClick to open " + name).create()));
            TextComponent line = new TextComponent(label);
            line.addExtra(btn);
            player.spigot().sendMessage(line);
        }
    }

    private static String resolveCommand(String sub) {
        return switch (sub) {
            case "auction" -> "auction";
            case "ah" -> "ah";
            case "bazaar", "bz" -> "bazaar";
            case "island" -> "island";
            case "quests" -> "quest";
            case "minions" -> "minion";
            case "slayer" -> "slay";
            case "island" -> "island";
            case "bank" -> "bank";
            case "mayor" -> "mayor";
            case "friend" -> "friend";
            case "run" -> "run";
            case "title" -> "title";
            case "mailbox", "mail" -> "mailbox";
            case "booster" -> "booster";
            case "menu" -> "menu";
            case "island" -> "island";
            default -> sub;
        };
    }
}
