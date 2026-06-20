package com.skyblock.core.command;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.menu.CollectionsMenu;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CollectionsCommand extends PlayerCommand {

    private static final List<String> SUBCOMMANDS;

    static {
        List<String> subs = new ArrayList<>(Arrays.asList("category", "reset", "history"));
        for (Collection c : Collection.values()) {
            subs.add(c.name().toLowerCase());
        }
        SUBCOMMANDS = Collections.unmodifiableList(subs);
    }

    private final CollectionManager collectionsManager;

    public CollectionsCommand(CollectionManager collectionsManager) {
        if (collectionsManager == null) {
            throw new IllegalArgumentException("collectionsManager must not be null");
        }
        this.collectionsManager = collectionsManager;
    }

    @Override
    protected void openMenu(Player p) {
        new CollectionsMenu(p.getUniqueId()).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "history" -> handleHistory(player);
            case "reset" -> {
                boolean had = collectionsManager.reset(player.getUniqueId());
                player.sendMessage(had ? "Your collection progress has been reset."
                                       : "You have no collection progress to reset.");
            }
            case "category" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /collections category <" +
                            Arrays.stream(CollectionCategory.values())
                                  .map(c -> c.name().toLowerCase())
                                  .collect(Collectors.joining("|")) + ">");
                    return true;
                }
                CollectionCategory category = parseCategory(args[1]);
                if (category == null) {
                    player.sendMessage("Unknown category: " + args[1]);
                    return true;
                }
                handleCategory(player, category);
            }
            default -> {
                Collection collection = Collection.parse(args[0]);
                if (collection == null) {
                    player.sendMessage("Unknown collection: " + args[0] +
                            ". Use /collections to see all collections.");
                    return true;
                }
                handleCollection(player, collection);
            }
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
        if (args.length == 2 && args[0].equalsIgnoreCase("category")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(CollectionCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleHistory(Player player) {
        List<String> history = collectionsManager.getCollectionsHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("No collection history yet.");
            return;
        }
        player.sendMessage("=== Collection History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void handleCollection(Player player, Collection collection) {
        UUID id = player.getUniqueId();
        long total = collectionsManager.getItems(id, collection);
        int tier = collectionsManager.getTier(id, collection);
        long toNext = collectionsManager.getItemsToNextTier(id, collection);
        player.sendMessage("=== " + collection.getDisplayName() + " Collection ===");
        player.sendMessage("  Total gathered : " + total);
        player.sendMessage("  Tier           : " + tier);
        if (toNext > 0) {
            player.sendMessage("  To next tier   : " + toNext);
        }
    }

    private void handleCategory(Player player, CollectionCategory category) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== " + category.getDisplayName() + " Collections ===");
        for (Collection c : category.getCollections()) {
            long total = collectionsManager.getItems(id, c);
            int tier = collectionsManager.getTier(id, c);
            player.sendMessage(String.format("  %-22s %d  (Tier %d)", c.name(), total, tier));
        }
    }

    private static CollectionCategory parseCategory(String input) {
        for (CollectionCategory c : CollectionCategory.values()) {
            if (c.name().equalsIgnoreCase(input)) return c;
        }
        return null;
    }
}
