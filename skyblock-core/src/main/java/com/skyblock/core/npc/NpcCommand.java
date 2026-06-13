package com.skyblock.core.npc;

import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.npc.NpcManager.NpcDefinition;
import com.skyblock.core.npc.NpcManager.ShopItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /npc} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /npc list}                         — list all registered NPCs</li>
 *   <li>{@code /npc shop <npc>}                   — view an NPC's shop items</li>
 *   <li>{@code /npc buy <npc> <item>}             — purchase one item from an NPC</li>
 *   <li>{@code /npc type}                         — list all NPC role types</li>
 * </ul>
 * </p>
 */
public final class NpcCommand implements TabExecutor {

    private final NpcManager npcManager;
    private final EconomyManager economyManager;

    public NpcCommand(NpcManager npcManager, EconomyManager economyManager) {
        this.npcManager = npcManager;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> listNpcs(player);
            case "shop" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /npc shop <npc>");
                } else {
                    showShop(player, args[1]);
                }
            }
            case "buy" -> {
                if (args.length < 3) {
                    player.sendMessage("Usage: /npc buy <npc> <item>");
                } else {
                    handleBuy(player, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                }
            }
            case "type" -> listTypes(player);
            default -> sendUsage(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return List.of("list", "shop", "buy", "type").stream()
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("shop") || args[0].equalsIgnoreCase("buy"))) {
            String lower = args[1].toLowerCase();
            return npcManager.getAllNpcs().stream()
                    .map(NpcDefinition::id)
                    .filter(id -> id.startsWith(lower))
                    .collect(Collectors.toList());
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("buy")) {
            NpcDefinition npc = npcManager.findById(args[1]);
            if (npc == null) return Collections.emptyList();
            String lower = args[2].toLowerCase();
            return npc.items().stream()
                    .map(ShopItem::name)
                    .filter(n -> n.toLowerCase().startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void listNpcs(Player player) {
        List<NpcDefinition> all = npcManager.getAllNpcs();
        player.sendMessage("=== NPCs ===");
        if (all.isEmpty()) {
            player.sendMessage("No NPCs are registered.");
            return;
        }
        for (NpcDefinition npc : all) {
            player.sendMessage("- " + npc.id() + " (" + npc.name() + ", " + npc.items().size() + " items)");
        }
        player.sendMessage("Use /npc shop <npc> to view an NPC's shop.");
    }

    private void showShop(Player player, String npcId) {
        NpcDefinition npc = npcManager.findById(npcId);
        if (npc == null) {
            player.sendMessage("Unknown NPC: " + npcId + ". Use /npc list to see NPCs.");
            return;
        }
        player.sendMessage("=== " + npc.name() + "'s Shop ===");
        if (npc.items().isEmpty()) {
            player.sendMessage("This NPC has no items for sale.");
            return;
        }
        for (ShopItem item : npc.items()) {
            player.sendMessage("- " + item.name() + " — " + item.price() + " coins");
        }
        player.sendMessage("Use /npc buy " + npc.id() + " <item> to purchase.");
    }

    private void handleBuy(Player player, String npcId, String itemName) {
        NpcDefinition npc = npcManager.findById(npcId);
        if (npc == null) {
            player.sendMessage("Unknown NPC: " + npcId + ". Use /npc list to see NPCs.");
            return;
        }
        ShopItem item = npcManager.findItem(npcId, itemName);
        if (item == null) {
            player.sendMessage("Item '" + itemName + "' not found in " + npc.name() + "'s shop.");
            return;
        }
        if (!economyManager.withdraw(player.getUniqueId(), item.price())) {
            player.sendMessage("You don't have enough coins. " + item.name() + " costs " + item.price() + " coins.");
            return;
        }
        player.sendMessage("You purchased " + item.name() + " from " + npc.name() + " for " + item.price() + " coins.");
    }

    private void listTypes(Player player) {
        player.sendMessage("=== NPC Types ===");
        for (NpcManager.NpcType type : NpcManager.NpcType.values()) {
            player.sendMessage("- " + type.name() + " (" + type.getDisplayName() + ")");
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage("Usage: /npc <list|shop <npc>|buy <npc> <item>|type>");
    }
}
