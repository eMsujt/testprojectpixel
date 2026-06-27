package com.skyblock.core.backpack;

import com.skyblock.core.manager.BackpackManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /backpack} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /backpack}                  — open your backpack container</li>
 *   <li>{@code /backpack tier}             — show your current tier and capacity</li>
 *   <li>{@code /backpack upgrade <tier>}   — (op) set your backpack tier</li>
 * </ul>
 * Items are placed/taken directly in the backpack container.</p>
 */
public final class BackpackCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("tier", "upgrade");

    private final BackpackManager backpackManager;

    public BackpackCommand(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            backpackManager.open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "tier"    -> handleTier(player);
            case "upgrade" -> handleUpgrade(player, args);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("upgrade")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(BackpackManager.BackpackTier.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleTier(Player player) {
        BackpackManager.BackpackTier tier = backpackManager.getTier(player.getUniqueId());
        Inventory inv = backpackManager.getBackpack(player.getUniqueId());
        int used = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                used++;
            }
        }
        player.sendMessage("§7Backpack tier: §a" + tier.name() + " §7(" + used + "/" + tier.slots + " slots used)");
    }

    private void handleUpgrade(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("§cYou do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("§7Usage: /backpack upgrade <SMALL|MEDIUM|LARGE|GREATER|JUMBO>");
            return;
        }
        BackpackManager.BackpackTier tier;
        try {
            tier = BackpackManager.BackpackTier.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUnknown tier: " + args[1] + ". Valid: SMALL, MEDIUM, LARGE, GREATER, JUMBO");
            return;
        }
        backpackManager.setTier(player.getUniqueId(), tier);
        player.sendMessage("§aBackpack upgraded to " + tier.name() + " (" + tier.slots + " slots).");
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6=== Backpack Commands ===");
        player.sendMessage("§e/backpack §7— open your backpack");
        player.sendMessage("§e/backpack tier §7— view your tier and capacity");
        player.sendMessage("§e/backpack upgrade <tier> §7— (op) set your backpack tier");
    }
}
