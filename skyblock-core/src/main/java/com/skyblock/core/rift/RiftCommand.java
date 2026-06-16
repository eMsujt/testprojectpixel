package com.skyblock.core.rift;

import com.skyblock.core.rift.RiftManager.RiftArea;
import com.skyblock.core.rift.RiftManager.RiftData;
import com.skyblock.core.rift.RiftManager.RiftMobType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles the {@code /rift} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /rift info}           — show current Rift status and kill counts</li>
 *   <li>{@code /rift enter [zone]}   — enter the Rift in the given zone (default WYLD_WOODS)</li>
 *   <li>{@code /rift exit}           — leave the Rift</li>
 *   <li>{@code /rift reset}          — reset all Rift data</li>
 * </ul>
 * </p>
 */
public final class RiftCommand implements TabExecutor {

    private final RiftManager riftManager;

    public RiftCommand(RiftManager riftManager) {
        this.riftManager = riftManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player);
            case "enter" -> handleEnter(player, args);
            case "exit"  -> handleExit(player);
            case "reset" -> handleReset(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("info", "enter", "exit", "reset").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("enter")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(RiftArea.values())
                    .map(z -> z.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        RiftData data = riftManager.getRiftData(player.getUniqueId());
        player.sendMessage("=== Rift Status ===");
        player.sendMessage("In Rift: " + data.inRift);
        if (data.inRift) {
            player.sendMessage("Zone: " + (data.zone != null ? data.zone.name() : "unknown"));
            player.sendMessage("Time remaining: " + data.timeRemainingSeconds + "s");
        }
        player.sendMessage("Motes: " + data.motes);
        player.sendMessage("Timecharms: " + data.timecharms);
        player.sendMessage("Rift souls: " + data.riftSouls);
        player.sendMessage("Enigma souls: " + data.enigmaSouls + "/" + RiftManager.ENIGMA_SOUL_TOTAL);
        player.sendMessage("=== Kill Counts ===");
        boolean any = false;
        for (Map.Entry<RiftMobType, Integer> entry : data.kills.entrySet()) {
            player.sendMessage(entry.getKey().name().toLowerCase() + ": " + entry.getValue());
            any = true;
        }
        if (!any) {
            player.sendMessage("No Rift mob kills recorded.");
        }
    }

    private void handleEnter(Player player, String[] args) {
        RiftArea zone = RiftArea.WYLD_WOODS;
        if (args.length >= 2) {
            try {
                zone = RiftArea.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown zone: " + args[1]);
                return;
            }
        }
        RiftData current = riftManager.getRiftData(player.getUniqueId());
        if (current.inRift) {
            player.sendMessage("You are already in the Rift.");
            return;
        }
        riftManager.enterRift(player.getUniqueId(), zone);
        player.sendMessage("You have entered the Rift: " + zone.name());
    }

    private void handleExit(Player player) {
        boolean wasIn = riftManager.exitRift(player.getUniqueId());
        if (wasIn) {
            player.sendMessage("You have left the Rift.");
        } else {
            player.sendMessage("You are not currently in the Rift.");
        }
    }

    private void handleReset(Player player) {
        riftManager.reset(player.getUniqueId());
        player.sendMessage("All Rift data has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Rift Commands ===");
        player.sendMessage("/rift info — show Rift status and kill counts");
        player.sendMessage("/rift enter [zone] — enter the Rift");
        player.sendMessage("/rift exit — leave the Rift");
        player.sendMessage("/rift reset — reset all Rift data");
    }
}
