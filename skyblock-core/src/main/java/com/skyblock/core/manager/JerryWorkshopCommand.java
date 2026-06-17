package com.skyblock.core.manager;

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
 * Handles the {@code /jerryworkshop} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /jerryworkshop}        — show your gift counts, snow minions, and stored snow</li>
 *   <li>{@code /jerryworkshop info}   — same as above</li>
 *   <li>{@code /jerryworkshop reset}  — reset all of your Jerry's Workshop progress</li>
 * </ul>
 * </p>
 */
public final class JerryWorkshopCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "reset");

    private final JerryWorkshopManager jerryWorkshopManager;

    public JerryWorkshopCommand(JerryWorkshopManager jerryWorkshopManager) {
        this.jerryWorkshopManager = jerryWorkshopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player);
            case "reset" -> handleReset(player);
            default      -> sendHelp(player);
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

    private void handleInfo(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Jerry's Workshop ===");
        player.sendMessage("Gifts created: " + jerryWorkshopManager.getGiftsCreated(id));
        player.sendMessage("Gifts given: " + jerryWorkshopManager.getGiftsGiven(id));
        player.sendMessage("Gifts received: " + jerryWorkshopManager.getGiftsReceived(id)
                + " (" + jerryWorkshopManager.getPendingGiftCount(id) + " pending)");
        player.sendMessage("Gifts opened: " + jerryWorkshopManager.getGiftsOpened(id));
        player.sendMessage("Snow minions: " + jerryWorkshopManager.getSnowMinionCount(id)
                + " / " + JerryWorkshopManager.SNOW_MINION_AREA_CAPACITY);
        player.sendMessage("Stored snow: " + jerryWorkshopManager.getSnow(id));
    }

    private void handleReset(Player player) {
        jerryWorkshopManager.reset(player.getUniqueId());
        player.sendMessage("Your Jerry's Workshop progress has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Jerry's Workshop Commands ===");
        player.sendMessage("/jerryworkshop        — show gift counts, snow minions, and snow");
        player.sendMessage("/jerryworkshop info   — show gift counts, snow minions, and snow");
        player.sendMessage("/jerryworkshop reset  — reset all of your progress");
    }
}
