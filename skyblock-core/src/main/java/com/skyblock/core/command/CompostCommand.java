package com.skyblock.core.command;

import com.skyblock.core.item.SkyblockItems;
import com.skyblock.core.manager.GardenManager;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Player-facing command for the Garden Composter, backed by the canonical
 * composter state on {@link GardenManager}. Lets a player inspect their
 * organic-matter / fuel reserves, run a processing pass, and collect produced
 * compost. Adding raw organic matter or fuel directly is op-gated, matching the
 * convention used by {@link GardenCommand}.
 */
public final class CompostCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "matter", "fuel", "process", "collect");
    private static final List<String> ADD_TARGETS = Arrays.asList("matter", "fuel");

    private final GardenManager gardenManager;

    public CompostCommand(GardenManager gardenManager) {
        this.gardenManager = gardenManager;
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
            case "info"    -> handleInfo(player);
            case "matter"  -> handleAdd(player, args, true);
            case "fuel"    -> handleAdd(player, args, false);
            case "process" -> handleProcess(player);
            case "collect" -> handleCollect(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /compost <info|matter|fuel|process|collect>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Composter ===");
        player.sendMessage("Organic Matter: " + gardenManager.getComposterOrganicMatter(id));
        player.sendMessage("Fuel: " + gardenManager.getComposterFuel(id));
        player.sendMessage("Compost (uncollected): " + gardenManager.getComposterCompost(id));
        player.sendMessage("Cost per compost: " + GardenManager.ORGANIC_MATTER_PER_COMPOST
                + " matter + " + GardenManager.FUEL_PER_COMPOST + " fuel");
    }

    private void handleAdd(Player player, String[] args, boolean matter) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /compost " + args[0].toLowerCase() + " <amount>");
            return;
        }
        long amount = parseAmount(player, args[1]);
        if (amount < 0) return;
        UUID id = player.getUniqueId();
        if (matter) {
            long total = gardenManager.addComposterOrganicMatter(id, amount);
            player.sendMessage("Organic matter: " + total + ".");
        } else {
            long total = gardenManager.addComposterFuel(id, amount);
            player.sendMessage("Fuel: " + total + ".");
        }
    }

    private void handleProcess(Player player) {
        long produced = gardenManager.processComposter(player.getUniqueId());
        if (produced > 0) {
            player.sendMessage("Composter produced " + produced + " compost.");
        } else {
            player.sendMessage("Not enough organic matter and fuel to produce compost.");
        }
    }

    private void handleCollect(Player player) {
        long collected = gardenManager.collectComposterCompost(player.getUniqueId());
        if (collected <= 0) {
            player.sendMessage("No compost to collect.");
            return;
        }
        giveCompost(player, collected);
        player.sendMessage("Collected " + collected + " compost.");
    }

    /** Gives the player the collected compost as real items (overflow dropped). */
    private static void giveCompost(Player player, long amount) {
        ItemStack proto = SkyblockItems.build("COMPOST", 1);
        if (proto == null) {
            proto = new ItemStack(Material.BONE_MEAL);
        }
        long remaining = amount;
        int max = proto.getMaxStackSize();
        while (remaining > 0) {
            int n = (int) Math.min(remaining, max);
            ItemStack stack = proto.clone();
            stack.setAmount(n);
            for (ItemStack leftover : player.getInventory().addItem(stack).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= n;
        }
    }

    private long parseAmount(Player player, String input) {
        try {
            long amount = Long.parseLong(input);
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
