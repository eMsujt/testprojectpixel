package com.skyblock.core.npc;

import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.npc.NpcManager.NpcDefinition;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles the {@code /npc} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /npc list}                         — list all registered NPCs</li>
 *   <li>{@code /npc shops}                        — list all registered NPC shops</li>
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
            case "list"  -> listNpcs(player);
            case "shops" -> listShops(player);
            case "shop"  -> {
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
            return List.of("list", "shops", "shop", "buy", "type").stream()
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
            return ShopManager.getInstance().getShop(npc.shopId())
                    .map(shop -> shop.entries().stream()
                            .map(ShopEntry::itemId)
                            .filter(id -> id.toLowerCase().startsWith(lower))
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }

    private void listShops(Player player) {
        player.sendMessage("=== NPC Shops ===");
        for (NpcDefinition npc : npcManager.getAllNpcs()) {
            int count = ShopManager.getInstance().getShop(npc.shopId())
                    .map(s -> s.entries().size()).orElse(0);
            player.sendMessage("- " + npc.id() + " (" + npc.name() + ", " + count + " items)");
        }
        player.sendMessage("Use /npc shop <id> to view a shop's items.");
    }

    private void listNpcs(Player player) {
        List<NpcDefinition> all = npcManager.getAllNpcs();
        player.sendMessage("=== NPCs ===");
        if (all.isEmpty()) {
            player.sendMessage("No NPCs are registered.");
            return;
        }
        for (NpcDefinition npc : all) {
            int count = ShopManager.getInstance().getShop(npc.shopId())
                    .map(s -> s.entries().size()).orElse(0);
            player.sendMessage("- " + npc.id() + " (" + npc.name() + ", " + count + " items)");
        }
        player.sendMessage("Use /npc shop <npc> to view an NPC's shop.");
    }

    private void showShop(Player player, String npcId) {
        NpcDefinition npc = npcManager.findById(npcId);
        if (npc == null) {
            player.sendMessage("Unknown NPC: " + npcId + ". Use /npc list to see NPCs.");
            return;
        }
        Optional<ShopManager.Shop> shopOpt = ShopManager.getInstance().getShop(npc.shopId());
        player.sendMessage("=== " + npc.name() + "'s Shop ===");
        if (shopOpt.isEmpty() || shopOpt.get().entries().isEmpty()) {
            player.sendMessage("This NPC has no items for sale.");
            return;
        }
        for (ShopEntry entry : shopOpt.get().entries()) {
            player.sendMessage("- " + formatName(entry.itemId()) + " — " + entry.buyPrice() + " coins");
        }
        player.sendMessage("Use /npc buy " + npc.id() + " <item> to purchase.");
    }

    private void handleBuy(Player player, String npcId, String itemName) {
        NpcDefinition npc = npcManager.findById(npcId);
        if (npc == null) {
            player.sendMessage("Unknown NPC: " + npcId + ". Use /npc list to see NPCs.");
            return;
        }
        String itemId = itemName.toUpperCase(Locale.ROOT).replace(' ', '_');
        Optional<ShopEntry> entryOpt = ShopManager.getInstance().getEntry(npc.shopId(), itemId);
        if (entryOpt.isEmpty()) {
            player.sendMessage("Item '" + itemName + "' not found in " + npc.name() + "'s shop.");
            return;
        }
        ShopEntry entry = entryOpt.get();
        if (!economyManager.withdraw(player.getUniqueId(), entry.buyPrice())) {
            player.sendMessage("You don't have enough coins. " + formatName(entry.itemId()) + " costs " + entry.buyPrice() + " coins.");
            return;
        }
        player.sendMessage("You purchased " + formatName(entry.itemId()) + " from " + npc.name() + " for " + entry.buyPrice() + " coins.");
    }

    private static String formatName(String itemId) {
        StringBuilder sb = new StringBuilder();
        for (String word : itemId.split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }

    private void listTypes(Player player) {
        player.sendMessage("=== NPC Types ===");
        for (NpcManager.NpcType type : NpcManager.NpcType.values()) {
            player.sendMessage("- " + type.name() + " (" + type.getDisplayName() + ")");
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage("Usage: /npc <list|shops|shop <npc>|buy <npc> <item>|type>");
    }
}
