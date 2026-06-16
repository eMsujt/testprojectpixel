package com.skyblock.trades.command;

import com.skyblock.trades.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @deprecated Duplicate of {@link com.skyblock.core.trade.TradeCommand}. Use that class instead.
 * The canonical command is registered by {@code com.skyblock.plugin.SkyBlockPlugin} and
 * {@code com.skyblock.core.SkyblockPlugin}.
 */
@Deprecated
public final class TradeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "offer", "coins", "confirm", "cancel", "info"
    );

    private final TradeManager tradeManager;
    /** Maps each participant UUID to their active session UUID. */
    private final Map<UUID, UUID> playerSession = new ConcurrentHashMap<>();

    public TradeCommand(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
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
            case "offer"   -> handleOffer(player, args);
            case "coins"   -> handleCoins(player, args);
            case "confirm" -> handleConfirm(player);
            case "cancel"  -> handleCancel(player);
            case "info"    -> handleInfo(player);
            default        -> handleOpen(player, args[0]);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> suggestions = SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
            Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .forEach(suggestions::add);
            return suggestions;
        }
        return Collections.emptyList();
    }

    private void handleOpen(Player player, String targetName) {
        if (playerSession.containsKey(player.getUniqueId())) {
            player.sendMessage("You are already in a trade. Use /trade cancel to cancel it first.");
            return;
        }
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage("Player '" + targetName + "' is not online.");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot trade with yourself.");
            return;
        }
        if (playerSession.containsKey(target.getUniqueId())) {
            player.sendMessage(target.getName() + " is already in a trade.");
            return;
        }
        try {
            TradeManager.TradeSession session = tradeManager.openTrade(player.getUniqueId(), target.getUniqueId());
            UUID sid = session.getSessionId();
            playerSession.put(player.getUniqueId(), sid);
            playerSession.put(target.getUniqueId(), sid);
            player.sendMessage("Trade opened with " + target.getName() + ". Use /trade offer, /trade coins, then /trade confirm.");
            target.sendMessage(player.getName() + " has opened a trade with you. Use /trade offer, /trade coins, then /trade confirm.");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleOffer(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trade offer <item> [amount]");
            return;
        }
        TradeManager.TradeSession session = requireSession(player);
        if (session == null) return;

        String itemId = args[1];
        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount: " + args[2]);
                return;
            }
        }
        try {
            tradeManager.offerItem(session.getSessionId(), player.getUniqueId(), itemId, amount);
            player.sendMessage("Added " + amount + "x " + itemId + " to your offer.");
            notifyPartner(player, session, player.getName() + " updated their item offer.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            cleanUpIfClosed(session);
        }
    }

    private void handleCoins(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trade coins <amount>");
            return;
        }
        TradeManager.TradeSession session = requireSession(player);
        if (session == null) return;

        double coins;
        try {
            coins = Double.parseDouble(args[1]);
            if (coins < 0 || !Double.isFinite(coins)) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[1]);
            return;
        }
        try {
            tradeManager.offerCoins(session.getSessionId(), player.getUniqueId(), coins);
            player.sendMessage("Set your coin offer to " + coins + ".");
            notifyPartner(player, session, player.getName() + " updated their coin offer.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            cleanUpIfClosed(session);
        }
    }

    private void handleConfirm(Player player) {
        TradeManager.TradeSession session = requireSession(player);
        if (session == null) return;

        try {
            boolean completed = tradeManager.confirm(session.getSessionId(), player.getUniqueId());
            if (completed) {
                notifyBoth(session, "Trade completed!");
                removeSession(session);
            } else {
                player.sendMessage("You confirmed. Waiting for the other player.");
                notifyPartner(player, session, player.getName() + " confirmed the trade.");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            cleanUpIfClosed(session);
        }
    }

    private void handleCancel(Player player) {
        TradeManager.TradeSession session = requireSession(player);
        if (session == null) return;

        try {
            tradeManager.cancelTrade(session.getSessionId());
            notifyBoth(session, "Trade cancelled by " + player.getName() + ".");
            removeSession(session);
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            cleanUpIfClosed(session);
        }
    }

    private void handleInfo(Player player) {
        TradeManager.TradeSession session = requireSession(player);
        if (session == null) return;

        Player initiator = Bukkit.getPlayer(session.getInitiator());
        Player partner = Bukkit.getPlayer(session.getPartner());
        String initiatorName = initiator != null ? initiator.getName() : session.getInitiator().toString();
        String partnerName = partner != null ? partner.getName() : session.getPartner().toString();

        player.sendMessage("=== Trade: " + initiatorName + " <-> " + partnerName + " ===");
        player.sendMessage("State: " + session.getState());
        printSide(player, initiatorName, session.getInitiator(), session);
        printSide(player, partnerName, session.getPartner(), session);
    }

    private void printSide(Player viewer, String name, UUID side, TradeManager.TradeSession session) {
        viewer.sendMessage(name + " (confirmed=" + session.hasConfirmed(side) + "):");
        viewer.sendMessage("  Coins: " + session.getOfferedCoins(side));
        Map<String, Integer> items = session.getOfferedItems(side);
        if (items.isEmpty()) {
            viewer.sendMessage("  Items: none");
        } else {
            items.forEach((id, qty) -> viewer.sendMessage("  - " + qty + "x " + id));
        }
    }

    private TradeManager.TradeSession requireSession(Player player) {
        UUID sid = playerSession.get(player.getUniqueId());
        if (sid == null) {
            player.sendMessage("You are not in a trade. Use /trade <player> to start one.");
            return null;
        }
        return tradeManager.getSession(sid).orElse(null);
    }

    private void notifyPartner(Player sender, TradeManager.TradeSession session, String message) {
        UUID partnerId = session.getInitiator().equals(sender.getUniqueId())
                ? session.getPartner()
                : session.getInitiator();
        Player partner = Bukkit.getPlayer(partnerId);
        if (partner != null) {
            partner.sendMessage(message);
        }
    }

    private void notifyBoth(TradeManager.TradeSession session, String message) {
        Player initiator = Bukkit.getPlayer(session.getInitiator());
        Player partner = Bukkit.getPlayer(session.getPartner());
        if (initiator != null) initiator.sendMessage(message);
        if (partner != null) partner.sendMessage(message);
    }

    private void removeSession(TradeManager.TradeSession session) {
        playerSession.remove(session.getInitiator());
        playerSession.remove(session.getPartner());
    }

    private void cleanUpIfClosed(TradeManager.TradeSession session) {
        TradeManager.TradeState state = session.getState();
        if (state == TradeManager.TradeState.COMPLETED || state == TradeManager.TradeState.CANCELLED) {
            removeSession(session);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trade Commands ===");
        player.sendMessage("/trade <player>           — open a trade with a player");
        player.sendMessage("/trade offer <item> [qty] — add an item to your offer");
        player.sendMessage("/trade coins <amount>     — set the coins you offer");
        player.sendMessage("/trade confirm            — confirm the trade");
        player.sendMessage("/trade cancel             — cancel the trade");
        player.sendMessage("/trade info               — view the current trade");
    }
}
