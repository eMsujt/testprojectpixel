package com.skyblock.core.trade;

import com.skyblock.core.manager.TradeManager;
import org.bukkit.Bukkit;
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
 * Handles the {@code /trade} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /trade <player>}        — send a trade request</li>
 *   <li>{@code /trade accept <player>} — accept a pending trade request</li>
 *   <li>{@code /trade decline <player>}— decline a pending trade request</li>
 *   <li>{@code /trade add <slot>}      — add held/hotbar item to your trade offer</li>
 *   <li>{@code /trade remove <index>}  — remove an item from your trade offer by index</li>
 *   <li>{@code /trade view}            — view the current trade offers on both sides</li>
 *   <li>{@code /trade confirm}         — confirm your side of the trade</li>
 *   <li>{@code /trade cancel}          — cancel the active trade session</li>
 * </ul>
 * </p>
 */
public final class TradeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("accept", "decline", "add", "remove", "coins", "view", "confirm", "cancel");

    private final TradeManager tradeManager;

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
            case "accept"  -> handleAccept(player, args);
            case "decline" -> handleDecline(player, args);
            case "add"     -> handleAdd(player, args);
            case "remove"  -> handleRemove(player, args);
            case "coins"   -> handleCoins(player, args);
            case "view"    -> handleView(player);
            case "confirm" -> handleConfirm(player);
            case "cancel"  -> handleCancel(player);
            default        -> handleRequest(player, args[0]);
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

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleRequest(Player player, String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage("Player '" + targetName + "' is not online.");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot trade with yourself.");
            return;
        }
        if (tradeManager.hasSession(player.getUniqueId())) {
            player.sendMessage("You already have an active trade session. Use /trade cancel first.");
            return;
        }
        TradeManager.TradeRequest request = new TradeManager.TradeRequest(
                player.getUniqueId(), target.getUniqueId(),
                Collections.emptyList(), 0.0);
        tradeManager.sendRequest(request);
        player.sendMessage("Trade request sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " has sent you a trade request. Use /trade accept "
                + player.getName() + " to accept.");
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trade accept <player>");
            return;
        }
        Player requester = Bukkit.getPlayerExact(args[1]);
        if (requester == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!tradeManager.hasPendingRequest(requester.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("No pending trade request from " + requester.getName() + ".");
            return;
        }
        if (tradeManager.hasSession(player.getUniqueId())
                || tradeManager.hasSession(requester.getUniqueId())) {
            player.sendMessage("One of the players already has an active trade session.");
            return;
        }
        tradeManager.clearRequest(requester.getUniqueId());
        tradeManager.openSession(requester.getUniqueId(), player.getUniqueId());
        player.sendMessage("Trade session started with " + requester.getName() + "!");
        requester.sendMessage(player.getName() + " accepted your trade request!");
    }

    private void handleDecline(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trade decline <player>");
            return;
        }
        Player requester = Bukkit.getPlayerExact(args[1]);
        if (requester == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!tradeManager.hasPendingRequest(requester.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("No pending trade request from " + requester.getName() + ".");
            return;
        }
        tradeManager.clearRequest(requester.getUniqueId());
        player.sendMessage("You declined the trade request from " + requester.getName() + ".");
        requester.sendMessage(player.getName() + " declined your trade request.");
    }

    private void handleAdd(Player player, String[] args) {
        TradeManager.TradeSession session = tradeManager.getSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("You do not have an active trade session.");
            return;
        }
        // Unconfirm both sides when the offer changes
        session.unconfirm(player.getUniqueId());
        session.unconfirm(session.getOther(player.getUniqueId()));

        int slot = 0;
        if (args.length >= 2) {
            try {
                slot = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid slot number.");
                return;
            }
        }
        if (slot < 0 || slot > 8) {
            player.sendMessage("Slot must be between 1 and 9.");
            return;
        }
        ItemStack item = player.getInventory().getItem(slot);
        if (item == null || item.getType().isAir()) {
            player.sendMessage("No item in slot " + (slot + 1) + ".");
            return;
        }
        session.addItem(player.getUniqueId(), item.clone());
        player.sendMessage("Added " + item.getType().name() + " x" + item.getAmount() + " to your trade offer.");

        Player partner = Bukkit.getPlayer(session.getOther(player.getUniqueId()));
        if (partner != null) {
            partner.sendMessage(player.getName() + " added " + item.getType().name()
                    + " x" + item.getAmount() + " to their trade offer.");
        }
    }

    private void handleRemove(Player player, String[] args) {
        TradeManager.TradeSession session = tradeManager.getSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("You do not have an active trade session.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /trade remove <index>");
            return;
        }
        int index;
        try {
            index = Integer.parseInt(args[1]) - 1;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid index.");
            return;
        }
        session.unconfirm(player.getUniqueId());
        session.unconfirm(session.getOther(player.getUniqueId()));

        if (!session.removeItem(player.getUniqueId(), index)) {
            player.sendMessage("No item at index " + (index + 1) + ".");
            return;
        }
        player.sendMessage("Item removed from your trade offer.");
    }

    private void handleCoins(Player player, String[] args) {
        TradeManager.TradeSession session = tradeManager.getSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("You do not have an active trade session.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /trade coins <amount>");
            return;
        }
        double coins;
        try {
            coins = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid coin amount.");
            return;
        }
        if (coins < 0) {
            player.sendMessage("Coin amount cannot be negative.");
            return;
        }
        // Changing the offer voids both confirmations
        session.unconfirm(player.getUniqueId());
        session.unconfirm(session.getOther(player.getUniqueId()));

        session.setOfferedCoins(player.getUniqueId(), coins);
        player.sendMessage("You are now offering " + coins + " coins.");

        Player partner = Bukkit.getPlayer(session.getOther(player.getUniqueId()));
        if (partner != null) {
            partner.sendMessage(player.getName() + " is now offering " + coins + " coins.");
        }
    }

    private void handleView(Player player) {
        TradeManager.TradeSession session = tradeManager.getSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("You do not have an active trade session.");
            return;
        }
        UUID partnerId = session.getOther(player.getUniqueId());
        Player partner = Bukkit.getPlayer(partnerId);
        String partnerName = partner != null ? partner.getName() : partnerId.toString();

        player.sendMessage("=== Trade Session ===");
        player.sendMessage("Your offer:");
        listItems(player, session.getOfferedItems(player.getUniqueId()));
        player.sendMessage("  Coins: " + session.getOfferedCoins(player.getUniqueId()));
        player.sendMessage(partnerName + "'s offer:");
        listItems(player, session.getOfferedItems(partnerId));
        player.sendMessage("  Coins: " + session.getOfferedCoins(partnerId));
        player.sendMessage("Your confirmation: " + (session.isConfirmed(player.getUniqueId()) ? "YES" : "NO"));
        player.sendMessage(partnerName + "'s confirmation: " + (session.isConfirmed(partnerId) ? "YES" : "NO"));
    }

    private void handleConfirm(Player player) {
        TradeManager.TradeSession session = tradeManager.getSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("You do not have an active trade session.");
            return;
        }
        session.confirm(player.getUniqueId());
        player.sendMessage("You confirmed the trade.");

        UUID partnerId = session.getOther(player.getUniqueId());
        Player partner = Bukkit.getPlayer(partnerId);
        if (partner != null) {
            partner.sendMessage(player.getName() + " confirmed the trade.");
        }

        if (session.bothConfirmed()) {
            completeTrade(player, session, partnerId, partner);
        }
    }

    private void handleCancel(Player player) {
        TradeManager.TradeSession session = tradeManager.getSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("You do not have an active trade session.");
            return;
        }
        UUID partnerId = session.getOther(player.getUniqueId());
        tradeManager.closeSession(player.getUniqueId());
        player.sendMessage("Trade cancelled.");

        Player partner = Bukkit.getPlayer(partnerId);
        if (partner != null) {
            partner.sendMessage(player.getName() + " cancelled the trade.");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void completeTrade(Player playerA, TradeManager.TradeSession session,
                               UUID partnerBId, Player playerB) {
        List<ItemStack> aItems = session.getOfferedItems(playerA.getUniqueId());
        List<ItemStack> bItems = session.getOfferedItems(partnerBId);

        tradeManager.closeSession(playerA.getUniqueId());

        if (playerB != null) {
            for (ItemStack item : aItems) {
                playerB.getInventory().addItem(item.clone());
            }
            for (ItemStack item : bItems) {
                playerA.getInventory().addItem(item.clone());
            }
            playerA.sendMessage("Trade completed with " + playerB.getName() + "!");
            playerB.sendMessage("Trade completed with " + playerA.getName() + "!");
        } else {
            playerA.sendMessage("Trade failed: your partner is no longer online.");
        }
    }

    private void listItems(Player receiver, List<ItemStack> items) {
        if (items.isEmpty()) {
            receiver.sendMessage("  (nothing)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                receiver.sendMessage("  " + (i + 1) + ". " + item.getType().name() + " x" + item.getAmount());
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trade Commands ===");
        player.sendMessage("/trade <player>         — send a trade request");
        player.sendMessage("/trade accept <player>  — accept a trade request");
        player.sendMessage("/trade decline <player> — decline a trade request");
        player.sendMessage("/trade add [slot]       — add item from hotbar slot (default: slot 1)");
        player.sendMessage("/trade remove <index>   — remove item from your offer by index");
        player.sendMessage("/trade coins <amount>   — set the coins you offer");
        player.sendMessage("/trade view             — view current trade offers");
        player.sendMessage("/trade confirm          — confirm your side of the trade");
        player.sendMessage("/trade cancel           — cancel the trade");
    }
}
