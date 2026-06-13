package com.skyblock.core.mailbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /mailbox} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /mailbox list}   — list pending deliveries</li>
 *   <li>{@code /mailbox claim}  — claim all pending deliveries</li>
 * </ul>
 * </p>
 */
public final class MailboxCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "claim");

    private final MailboxManager mailboxManager;

    public MailboxCommand(MailboxManager mailboxManager) {
        this.mailboxManager = mailboxManager;
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
            case "list"  -> handleList(player);
            case "claim" -> handleClaim(player);
            default      -> sendHelp(player);
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

    private void handleList(Player player) {
        List<MailboxManager.MailboxItem> items = mailboxManager.getDeliveries(player.getUniqueId());
        if (items.isEmpty()) {
            player.sendMessage("Your mailbox is empty.");
            return;
        }
        player.sendMessage("=== Mailbox (" + items.size() + " item(s)) ===");
        for (int i = 0; i < items.size(); i++) {
            MailboxManager.MailboxItem item = items.get(i);
            player.sendMessage("  " + (i + 1) + ". From " + item.sender() + ": " + item.message());
        }
    }

    private void handleClaim(Player player) {
        if (!mailboxManager.hasDeliveries(player.getUniqueId())) {
            player.sendMessage("You have no deliveries to claim.");
            return;
        }
        mailboxManager.claimAll(player.getUniqueId());
        player.sendMessage("All mailbox deliveries claimed.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mailbox Commands ===");
        player.sendMessage("/mailbox list  — list pending deliveries");
        player.sendMessage("/mailbox claim — claim all pending deliveries");
    }
}
