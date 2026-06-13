package com.skyblock.core.mailbox;

import com.skyblock.core.mailbox.MailboxManager.MailMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Handles the {@code /mailbox} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /mailbox inbox}               — list all messages in your inbox</li>
 *   <li>{@code /mailbox send <player> <msg>}  — send a message to another player's inbox</li>
 *   <li>{@code /mailbox clear}               — delete all messages from your inbox</li>
 * </ul>
 * </p>
 */
public final class MailboxCommand implements TabExecutor {

    private final MailboxManager mailboxManager;

    public MailboxCommand(MailboxManager mailboxManager) {
        this.mailboxManager = Objects.requireNonNull(mailboxManager, "mailboxManager");
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
            case "inbox" -> handleInbox(player);
            case "send"  -> handleSend(player, args);
            case "clear" -> handleClear(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("inbox", "send", "clear").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            String lower = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleInbox(Player player) {
        List<MailMessage> messages = mailboxManager.getInbox(player.getUniqueId());
        if (messages.isEmpty()) {
            player.sendMessage("Your mailbox is empty.");
            return;
        }
        player.sendMessage("=== Mailbox (" + messages.size() + " messages) ===");
        for (int i = 0; i < messages.size(); i++) {
            MailMessage msg = messages.get(i);
            player.sendMessage((i + 1) + ". [" + msg.sender + "] " + msg.content);
        }
    }

    private void handleSend(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /mailbox send <player> <message>");
            return;
        }
        String targetName = args[1];
        Player target = Bukkit.getPlayerExact(targetName);
        UUID targetId;
        if (target != null) {
            targetId = target.getUniqueId();
        } else {
            player.sendMessage("Player not found: " + targetName);
            return;
        }
        String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        long timestamp = System.currentTimeMillis();
        mailboxManager.sendMail(targetId, new MailMessage(player.getName(), content, timestamp));
        player.sendMessage("Mail sent to " + target.getName() + ".");
        if (target.isOnline()) {
            target.sendMessage("You have new mail from " + player.getName() + ". Use /mailbox inbox to read it.");
        }
    }

    private void handleClear(Player player) {
        boolean had = mailboxManager.clearInbox(player.getUniqueId());
        if (had) {
            player.sendMessage("Your mailbox has been cleared.");
        } else {
            player.sendMessage("Your mailbox is already empty.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mailbox Commands ===");
        player.sendMessage("/mailbox inbox — read your messages");
        player.sendMessage("/mailbox send <player> <message> — send a message");
        player.sendMessage("/mailbox clear — delete all your messages");
    }
}
