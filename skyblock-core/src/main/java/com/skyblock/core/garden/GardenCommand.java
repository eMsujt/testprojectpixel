package com.skyblock.core.garden;

import com.skyblock.core.garden.GardenManager.GardenCrop;
import com.skyblock.core.garden.GardenManager.PlotSlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /garden} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /garden info}                          — show garden level, composter, and visitor count</li>
 *   <li>{@code /garden plot list}                     — list all plots and their unlock status</li>
 *   <li>{@code /garden plot unlock <slot>}            — unlock a plot slot</li>
 *   <li>{@code /garden plot setcrop <slot> <crop>}    — set the active crop for a plot</li>
 *   <li>{@code /garden composter add <amount>}        — add organic matter to the composter</li>
 *   <li>{@code /garden composter status}              — show composter organic matter total</li>
 *   <li>{@code /garden reset}                         — reset all garden data</li>
 * </ul>
 * </p>
 */
public final class GardenCommand implements TabExecutor {

    private final GardenManager gardenManager;

    public GardenCommand(GardenManager gardenManager) {
        this.gardenManager = gardenManager;
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
            case "info"       -> handleInfo(player);
            case "plot"       -> handlePlot(player, args);
            case "composter"  -> handleComposter(player, args);
            case "reset"      -> handleReset(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("info", "plot", "composter", "reset").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "plot" -> {
                    String lower = args[1].toLowerCase();
                    return Arrays.asList("list", "unlock", "setcrop").stream()
                            .filter(s -> s.startsWith(lower))
                            .toList();
                }
                case "composter" -> {
                    String lower = args[1].toLowerCase();
                    return Arrays.asList("add", "status").stream()
                            .filter(s -> s.startsWith(lower))
                            .toList();
                }
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("plot")
                && (args[1].equalsIgnoreCase("unlock") || args[1].equalsIgnoreCase("setcrop"))) {
            String lower = args[2].toLowerCase();
            return Arrays.stream(PlotSlot.values())
                    .map(s -> s.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("plot") && args[1].equalsIgnoreCase("setcrop")) {
            String lower = args[3].toLowerCase();
            return Arrays.stream(GardenCrop.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        int level = gardenManager.getGardenLevel(player.getUniqueId());
        double xp = gardenManager.getGardenXp(player.getUniqueId());
        double matter = gardenManager.getComposterMatter(player.getUniqueId());
        int visitors = gardenManager.getVisitorCount(player.getUniqueId());
        int unlockedCount = gardenManager.getUnlockedPlots(player.getUniqueId()).size();

        player.sendMessage("=== Garden Info ===");
        player.sendMessage("Level: " + level + " (" + String.format("%.1f", xp) + " XP)");
        player.sendMessage("Unlocked Plots: " + unlockedCount + "/" + PlotSlot.values().length);
        player.sendMessage("Composter Matter: " + String.format("%.1f", matter));
        player.sendMessage("Visitors: " + visitors);
    }

    private void handlePlot(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /garden plot <list|unlock <slot>|setcrop <slot> <crop>>");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "list"    -> handlePlotList(player);
            case "unlock"  -> handlePlotUnlock(player, args);
            case "setcrop" -> handlePlotSetCrop(player, args);
            default        -> player.sendMessage("Usage: /garden plot <list|unlock <slot>|setcrop <slot> <crop>>");
        }
    }

    private void handlePlotList(Player player) {
        player.sendMessage("=== Garden Plots ===");
        for (PlotSlot slot : PlotSlot.values()) {
            boolean unlocked = gardenManager.isPlotUnlocked(player.getUniqueId(), slot);
            String status = unlocked ? "UNLOCKED" : "locked";
            String cropName = unlocked
                    ? gardenManager.getPlotCrop(player.getUniqueId(), slot).getDisplayName()
                    : slot.getDefaultCrop().getDisplayName();
            player.sendMessage(slot.getDisplayName() + " [" + status + "] — " + cropName);
        }
    }

    private void handlePlotUnlock(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /garden plot unlock <slot>");
            return;
        }
        PlotSlot slot;
        try {
            slot = PlotSlot.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown plot slot: " + args[2]);
            return;
        }
        boolean newly = gardenManager.unlockPlot(player.getUniqueId(), slot);
        if (newly) {
            player.sendMessage("Unlocked plot: " + slot.getDisplayName());
        } else {
            player.sendMessage("Plot already unlocked: " + slot.getDisplayName());
        }
    }

    private void handlePlotSetCrop(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Usage: /garden plot setcrop <slot> <crop>");
            return;
        }
        PlotSlot slot;
        try {
            slot = PlotSlot.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown plot slot: " + args[2]);
            return;
        }
        GardenCrop crop;
        try {
            crop = GardenCrop.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crop: " + args[3]);
            return;
        }
        try {
            gardenManager.setPlotCrop(player.getUniqueId(), slot, crop);
            player.sendMessage("Set " + slot.getDisplayName() + " crop to " + crop.getDisplayName() + ".");
        } catch (IllegalStateException e) {
            player.sendMessage("You must unlock this plot first: " + slot.getDisplayName());
        }
    }

    private void handleComposter(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /garden composter <add <amount>|status>");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "add"    -> handleComposterAdd(player, args);
            case "status" -> {
                double matter = gardenManager.getComposterMatter(player.getUniqueId());
                player.sendMessage("Composter organic matter: " + String.format("%.1f", matter));
            }
            default -> player.sendMessage("Usage: /garden composter <add <amount>|status>");
        }
    }

    private void handleComposterAdd(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /garden composter add <amount>");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Amount must be positive.");
            return;
        }
        double total = gardenManager.addComposterMatter(player.getUniqueId(), amount);
        player.sendMessage("Added " + String.format("%.1f", amount) + " organic matter. Total: "
                + String.format("%.1f", total));
    }

    private void handleReset(Player player) {
        gardenManager.reset(player.getUniqueId());
        player.sendMessage("All garden data has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Garden Commands ===");
        player.sendMessage("/garden info — show garden level, plots, composter, and visitors");
        player.sendMessage("/garden plot list — list all plots and their status");
        player.sendMessage("/garden plot unlock <slot> — unlock a plot slot");
        player.sendMessage("/garden plot setcrop <slot> <crop> — set the active crop for a plot");
        player.sendMessage("/garden composter add <amount> — add organic matter to the composter");
        player.sendMessage("/garden composter status — show composter total");
        player.sendMessage("/garden reset — reset all garden data");
    }
}
