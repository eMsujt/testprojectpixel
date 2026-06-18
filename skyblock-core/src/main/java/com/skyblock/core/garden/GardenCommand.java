package com.skyblock.core.garden;

import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.menu.GardenMenu;

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
 * Handles the {@code /garden} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /garden info}                              — view plot level and visitor count</li>
 *   <li>{@code /garden plot [set|add] <level>}            — (op) view or modify plot level</li>
 *   <li>{@code /garden visitors [set|add] <amount>}       — (op) view or modify visitor count</li>
 *   <li>{@code /garden crop [<crop>]}                     — view crop upgrade levels</li>
 *   <li>{@code /garden crop set <crop> <level>}           — (op) set a crop upgrade level</li>
 *   <li>{@code /garden crop add <crop> <amount>}          — (op) add to a crop upgrade level</li>
 *   <li>{@code /garden reset}                             — (op) reset all garden data</li>
 * </ul>
 * </p>
 */
public final class GardenCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "plot", "visitors", "crop", "plots", "tier", "harvest", "history", "reset");
    private static final List<String> CROP_TYPE_NAMES = Arrays.stream(GardenManager.CropType.values())
            .map(c -> c.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> MODIFY_OPS = Arrays.asList("set", "add");
    private static final List<String> CROP_OPS = Arrays.asList("set", "add");
    private static final List<String> TIER_NAMES = Arrays.stream(GardenManager.PlotTier.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> CROP_NAMES = Arrays.stream(GardenManager.GardenCrop.values())
            .map(c -> c.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> PLOT_NAMES = Arrays.stream(GardenManager.GardenPlot.values())
            .map(p -> p.name().toLowerCase())
            .collect(Collectors.toList());

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
            new GardenMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"     -> handleInfo(player);
            case "plot"     -> handlePlot(player, args);
            case "visitors" -> handleVisitors(player, args);
            case "crop"     -> handleCrop(player, args);
            case "plots"    -> handlePlots(player, args);
            case "tier"     -> handleTier(player, args);
            case "harvest"  -> handleHarvest(player, args);
            case "history"  -> handleHistory(player);
            case "reset"    -> handleReset(player);
            default         -> player.sendMessage("Unknown subcommand. Usage: /garden <info|plot|visitors|crop|plots|tier|harvest|history|reset>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            String prefix = args[1].toLowerCase();
            if (sub.equals("plot") || sub.equals("visitors")) {
                return MODIFY_OPS.stream().filter(o -> o.startsWith(prefix)).collect(Collectors.toList());
            }
            if (sub.equals("crop")) {
                List<String> opts = new java.util.ArrayList<>(CROP_OPS);
                opts.addAll(CROP_NAMES);
                return opts.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
            }
            if (sub.equals("plots")) {
                return PLOT_NAMES.stream().filter(p -> p.startsWith(prefix)).collect(Collectors.toList());
            }
            if (sub.equals("tier")) {
                return CROP_NAMES.stream().filter(c -> c.startsWith(prefix)).collect(Collectors.toList());
            }
            if (sub.equals("harvest")) {
                return CROP_TYPE_NAMES.stream().filter(c -> c.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("tier")) {
            String prefix = args[2].toLowerCase();
            return TIER_NAMES.stream().filter(t -> t.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("crop")
                && CROP_OPS.contains(args[1].toLowerCase())) {
            String prefix = args[2].toLowerCase();
            return CROP_NAMES.stream().filter(c -> c.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleInfo(Player player) {
        int plotLevel = gardenManager.getPlotLevel(player.getUniqueId());
        int visitors = gardenManager.getVisitorCount(player.getUniqueId());
        player.sendMessage("=== Garden Info ===");
        player.sendMessage("Plot Level: " + plotLevel);
        player.sendMessage("Total Visitors: " + visitors);
    }

    private void handlePlot(Player player, String[] args) {
        if (args.length >= 2) {
            String op = args[1].toLowerCase();
            if (op.equals("set") || op.equals("add")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 3) {
                    player.sendMessage("Usage: /garden plot " + op + " <level>");
                    return;
                }
                int amount = parseAmount(player, args[2]);
                if (amount < 0) return;
                if (op.equals("set")) {
                    gardenManager.setPlotLevel(player.getUniqueId(), amount);
                    player.sendMessage("Garden plot level set to " + gardenManager.getPlotLevel(player.getUniqueId()) + ".");
                } else {
                    int newLevel = gardenManager.addPlotLevel(player.getUniqueId(), amount);
                    player.sendMessage("Garden plot level: " + newLevel + ".");
                }
                return;
            }
        }
        int plotLevel = gardenManager.getPlotLevel(player.getUniqueId());
        player.sendMessage("Garden Plot Level: " + plotLevel);
    }

    private void handleVisitors(Player player, String[] args) {
        if (args.length >= 2) {
            String op = args[1].toLowerCase();
            if (op.equals("set") || op.equals("add")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 3) {
                    player.sendMessage("Usage: /garden visitors " + op + " <amount>");
                    return;
                }
                int amount = parseAmount(player, args[2]);
                if (amount < 0) return;
                if (op.equals("set")) {
                    gardenManager.setVisitorCount(player.getUniqueId(), amount);
                    player.sendMessage("Visitor count set to " + amount + ".");
                } else {
                    int newCount = gardenManager.addVisitorCount(player.getUniqueId(), amount);
                    player.sendMessage("Visitor count: " + newCount + ".");
                }
                return;
            }
        }
        int visitors = gardenManager.getVisitorCount(player.getUniqueId());
        player.sendMessage("Total Visitors: " + visitors);
    }

    private void handleCrop(Player player, String[] args) {
        if (args.length >= 2) {
            String op = args[1].toLowerCase();
            if (op.equals("set") || op.equals("add")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 4) {
                    player.sendMessage("Usage: /garden crop " + op + " <crop> <level>");
                    return;
                }
                GardenManager.GardenCrop crop = parseCrop(player, args[2]);
                if (crop == null) return;
                int amount = parseAmount(player, args[3]);
                if (amount < 0) return;
                if (op.equals("set")) {
                    gardenManager.setCropUpgrade(player.getUniqueId(), crop, amount);
                    player.sendMessage(crop.getDisplayName() + " upgrade set to " + amount + ".");
                } else {
                    int newLevel = gardenManager.addCropUpgrade(player.getUniqueId(), crop, amount);
                    player.sendMessage(crop.getDisplayName() + " upgrade: " + newLevel + ".");
                }
                return;
            }
            // treat as crop name for view
            GardenManager.GardenCrop crop = parseCrop(player, op);
            if (crop == null) return;
            int level = gardenManager.getCropUpgrade(player.getUniqueId(), crop);
            player.sendMessage(crop.getDisplayName() + " upgrade level: " + level);
        } else {
            player.sendMessage("=== Crop Upgrades ===");
            for (GardenManager.GardenCrop crop : GardenManager.GardenCrop.values()) {
                int level = gardenManager.getCropUpgrade(player.getUniqueId(), crop);
                player.sendMessage(crop.getDisplayName() + ": " + level);
            }
        }
    }

    private void handlePlots(Player player, String[] args) {
        if (args.length >= 2) {
            GardenManager.GardenPlot plot = parsePlot(player, args[1]);
            if (plot == null) return;
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to unlock plots.");
                return;
            }
            gardenManager.unlockPlot(player.getUniqueId(), plot);
            player.sendMessage("Unlocked plot: " + plot.getDisplayName() + ".");
            return;
        }
        player.sendMessage("=== Garden Plots ===");
        for (GardenManager.GardenPlot plot : GardenManager.GardenPlot.values()) {
            boolean unlocked = gardenManager.isPlotUnlocked(player.getUniqueId(), plot);
            player.sendMessage(plot.getDisplayName() + ": " + (unlocked ? "Unlocked" : "Locked"));
        }
    }

    private void handleTier(Player player, String[] args) {
        if (args.length >= 2) {
            GardenManager.GardenCrop crop = parseCrop(player, args[1]);
            if (crop == null) return;
            if (args.length >= 3) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to set plot tiers.");
                    return;
                }
                GardenManager.PlotTier tier;
                if (args[2].equalsIgnoreCase("upgrade")) {
                    tier = gardenManager.upgradeCropPlotTier(player.getUniqueId(), crop);
                    player.sendMessage(crop.getDisplayName() + " plot tier upgraded to " + tier.getDisplayName() + ".");
                    return;
                }
                try {
                    tier = GardenManager.PlotTier.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("Unknown tier: " + args[2] + ". Valid tiers: " + String.join(", ", TIER_NAMES));
                    return;
                }
                gardenManager.setCropPlotTier(player.getUniqueId(), crop, tier);
                player.sendMessage(crop.getDisplayName() + " plot tier set to " + tier.getDisplayName() + ".");
                return;
            }
            GardenManager.PlotTier tier = gardenManager.getCropPlotTier(player.getUniqueId(), crop);
            player.sendMessage(crop.getDisplayName() + " plot tier: " + tier.getDisplayName());
        } else {
            player.sendMessage("=== Crop Plot Tiers ===");
            for (GardenManager.GardenCrop crop : GardenManager.GardenCrop.values()) {
                GardenManager.PlotTier tier = gardenManager.getCropPlotTier(player.getUniqueId(), crop);
                player.sendMessage(crop.getDisplayName() + ": " + tier.getDisplayName());
            }
        }
    }

    private void handleHarvest(Player player, String[] args) {
        if (args.length >= 2) {
            GardenManager.CropType crop = parseCropType(player, args[1]);
            if (crop == null) return;
            int yield = gardenManager.harvest(player.getUniqueId(), crop);
            long total = gardenManager.getHarvestCount(player.getUniqueId(), crop);
            player.sendMessage("Harvested " + yield + "x " + crop.name() + ". Total: " + total + ".");
            return;
        }
        player.sendMessage("=== Harvest Totals ===");
        for (GardenManager.CropType crop : GardenManager.CropType.values()) {
            long total = gardenManager.getHarvestCount(player.getUniqueId(), crop);
            player.sendMessage(crop.name() + ": " + total);
        }
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        java.util.List<String> history = gardenManager.getGardenHistory(id);
        player.sendMessage("=== Garden History ===");
        if (history.isEmpty()) {
            player.sendMessage("No garden history found.");
        } else {
            for (int i = 0; i < history.size(); i++) {
                player.sendMessage((i + 1) + ". " + history.get(i));
            }
        }
    }

    private void handleReset(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        gardenManager.reset(player.getUniqueId());
        player.sendMessage("Your garden data has been reset.");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private GardenManager.GardenPlot parsePlot(Player player, String input) {
        try {
            return GardenManager.GardenPlot.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown plot: " + input + ". Valid plots: " + String.join(", ", PLOT_NAMES));
            return null;
        }
    }

    private GardenManager.GardenCrop parseCrop(Player player, String input) {
        try {
            return GardenManager.GardenCrop.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crop: " + input + ". Valid crops: " + String.join(", ", CROP_NAMES));
            return null;
        }
    }

    private GardenManager.CropType parseCropType(Player player, String input) {
        try {
            return GardenManager.CropType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crop: " + input + ". Valid crops: " + String.join(", ", CROP_TYPE_NAMES));
            return null;
        }
    }

    private int parseAmount(Player player, String input) {
        try {
            int amount = Integer.parseInt(input);
            if (amount < 0) {
                player.sendMessage("Amount must not be negative.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + input);
            return -1;
        }
    }

}
