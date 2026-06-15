package com.skyblock.core.hotm;

import com.skyblock.core.gui.GuiBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.menu.MenuManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /hotm} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /hotm}                      — open the perk tree GUI</li>
 *   <li>{@code /hotm view [perk]}           — show level for one or all perks</li>
 *   <li>{@code /hotm upgrade <perk>}        — (op) upgrade a perk by one level</li>
 *   <li>{@code /hotm set <perk> <level>}    — (op) set a perk to an exact level</li>
 *   <li>{@code /hotm reset}                 — (op) reset all perks to zero</li>
 * </ul>
 * </p>
 */
public final class HotmCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "upgrade", "set", "reset", "history");
    private static final List<String> PERK_NAMES = Arrays.stream(HotmManager.HotmPerk.values())
            .map(p -> p.name().toLowerCase())
            .collect(Collectors.toList());

    /** Perk tree layout: each entry is {slot, perk}. 54-slot (6-row) chest GUI. */
    private static final int[][] PERK_SLOTS = {
            {10, 0},  // MINING_SPEED
            {11, 1},  // MINING_SPEED_BOOST
            {12, 5},  // EFFICIENT_MINER
            {14, 6},  // QUICK_FORGE
            {16, 7},  // TITANIUM_INSANITY
            {19, 4},  // DAILY_POWDER
            {21, 10}, // MINING_MADNESS
            {23, 11}, // SKY_MALL
            {25, 12}, // GOBLIN_KILLER
            {28, 13}, // STAR_POWDER
            {30, 14}, // MOLE
            {32, 15}, // PROFESSIONAL
            {34, 16}, // LONESOME_MINER
            {37, 17}, // GREAT_EXPLORER
            {39, 18}, // FORTUNATE
            {41, 2},  // PICKOBULUS
            {43, 19}, // MINING_EXPERIENCE_BOOST
    };

    private final HotmManager hotmManager;
    private final MenuManager menuManager;

    public HotmCommand(HotmManager hotmManager) {
        this.hotmManager = hotmManager;
        this.menuManager = MenuManager.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            menuManager.openMenu(player, new HotmMenu(player));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"    -> handleView(player, args);
            case "upgrade" -> handleUpgrade(player, args);
            case "set"     -> handleSet(player, args);
            case "reset"   -> handleReset(player);
            case "history" -> handleHistory(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /hotm <view|upgrade|set|reset|history>");
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
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("view") || sub.equals("upgrade") || sub.equals("set")) {
                String prefix = args[1].toLowerCase();
                return PERK_NAMES.stream()
                        .filter(p -> p.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // GUI
    // -------------------------------------------------------------------------

    /** Inner menu class that builds and handles the HOTM perk tree inventory. */
    private final class HotmMenu extends Menu {

        private final Player owner;

        HotmMenu(Player owner) {
            this.owner = owner;
        }

        @Override
        public void open(Player player) {
            ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ", null);
            GuiBuilder builder = GuiBuilder.create(ChatColor.DARK_AQUA + "Heart of the Mountain", 6);

            HotmManager.HotmPerk[] perks = HotmManager.HotmPerk.values();
            for (int[] entry : PERK_SLOTS) {
                int slot = entry[0];
                HotmManager.HotmPerk perk = perks[entry[1]];
                int level = hotmManager.getLevel(player.getUniqueId(), perk);
                boolean maxed = level >= perk.maxLevel;

                Material mat = level > 0 ? Material.LIME_DYE : Material.GRAY_DYE;
                String color = maxed ? ChatColor.GOLD.toString()
                        : (level > 0 ? ChatColor.GREEN.toString() : ChatColor.RED.toString());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Level: " + ChatColor.WHITE + level + "/" + perk.maxLevel);
                if (maxed) {
                    lore.add(ChatColor.GOLD + "MAXED");
                } else if (player.isOp()) {
                    lore.add(ChatColor.YELLOW + "Click to upgrade");
                }

                ItemStack item = namedWithLore(mat, color + perk.getDisplayName(), lore);
                builder.setItem(slot, item, e -> {
                    if (!e.getWhoClicked().hasPermission("minecraft.command.op")
                            && !((Player) e.getWhoClicked()).isOp()) {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have permission to upgrade perks.");
                        return;
                    }
                    int newLevel = hotmManager.upgrade(owner.getUniqueId(), perk);
                    if (newLevel == -1) {
                        e.getWhoClicked().sendMessage(ChatColor.RED + perk.getDisplayName() + " is already at max level.");
                    } else {
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "Upgraded " + perk.getDisplayName() + " to level " + newLevel + ".");
                    }
                    // Reopen to refresh levels
                    menuManager.openMenu((Player) e.getWhoClicked(), new HotmMenu(owner));
                });
            }

            // Close button
            builder.setItem(49, named(Material.BARRIER, ChatColor.RED + "Close", null),
                    e -> e.getWhoClicked().closeInventory());

            Inventory inv = builder.fill(filler).build();
            player.openInventory(inv);
        }

        @Override
        public void handleClick(InventoryClickEvent event) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null
                    && event.getClickedInventory().getHolder() instanceof GuiBuilder.GuiHolder holder) {
                holder.handleClick(event);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Text subcommands
    // -------------------------------------------------------------------------

    private void handleView(Player player, String[] args) {
        if (args.length >= 2) {
            HotmManager.HotmPerk perk = parsePerk(player, args[1]);
            if (perk == null) return;
            int level = hotmManager.getLevel(player.getUniqueId(), perk);
            player.sendMessage(perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
        } else {
            player.sendMessage("=== Heart of the Mountain ===");
            for (HotmManager.HotmPerk perk : HotmManager.HotmPerk.values()) {
                int level = hotmManager.getLevel(player.getUniqueId(), perk);
                player.sendMessage(perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
            }
        }
    }

    private void handleUpgrade(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /hotm upgrade <perk>");
            return;
        }
        HotmManager.HotmPerk perk = parsePerk(player, args[1]);
        if (perk == null) return;
        int newLevel = hotmManager.upgrade(player.getUniqueId(), perk);
        if (newLevel == -1) {
            player.sendMessage(perk.getDisplayName() + " is already at max level (" + perk.maxLevel + ").");
        } else {
            player.sendMessage("Upgraded " + perk.getDisplayName() + " to level " + newLevel + ".");
        }
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /hotm set <perk> <level>");
            return;
        }
        HotmManager.HotmPerk perk = parsePerk(player, args[1]);
        if (perk == null) return;
        int level = parseLevel(player, args[2]);
        if (level < 0) return;
        hotmManager.setLevel(player.getUniqueId(), perk, level);
        int actual = hotmManager.getLevel(player.getUniqueId(), perk);
        player.sendMessage(perk.getDisplayName() + " set to " + actual + ".");
    }

    private void handleHistory(Player player) {
        java.util.List<String> history = hotmManager.getHotmHistory(player.getUniqueId());
        player.sendMessage("=== HOTM History ===");
        if (history.isEmpty()) {
            player.sendMessage("No HOTM history found.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void handleReset(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        hotmManager.reset(player.getUniqueId());
        player.sendMessage("All Heart of the Mountain perks have been reset.");
    }

    private HotmManager.HotmPerk parsePerk(Player player, String input) {
        try {
            return HotmManager.HotmPerk.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown perk: " + input + ". Valid perks: " + String.join(", ", PERK_NAMES));
            return null;
        }
    }

    private int parseLevel(Player player, String input) {
        try {
            int level = Integer.parseInt(input);
            if (level < 0) {
                player.sendMessage("Level must not be negative.");
                return -1;
            }
            return level;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid level: " + input);
            return -1;
        }
    }

    private static ItemStack named(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack namedWithLore(Material material, String displayName, List<String> lore) {
        return named(material, displayName, lore);
    }
}
