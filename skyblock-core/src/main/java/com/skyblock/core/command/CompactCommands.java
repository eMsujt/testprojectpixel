package com.skyblock.core.command;

import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.manager.RepairManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.manager.Warp;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.menu.AccessoryBagMenu;
import com.skyblock.core.menu.BankMenu;
import com.skyblock.core.menu.CalendarMenu;
import com.skyblock.core.menu.IslandMenu;
import com.skyblock.core.menu.MiningMenu;
import com.skyblock.core.menu.ReforgeMenu;
import com.skyblock.core.menu.WarpMenu;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.model.Stat;
import com.skyblock.core.season.SeasonManager;
import com.skyblock.core.talisman.manager.TalismanManager;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Single compilation unit for small, independent SkyBlock commands.
 */
public final class CompactCommands {

    private CompactCommands() {}

    // =========================================================================
    // /island
    // =========================================================================

    public static final class IslandCommand extends PlayerCommand {

        private static final List<String> SUB_COMMANDS = Arrays.asList("create", "home", "visit", "leave", "upgrade");

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            if (args.length == 0) {
                openMenu(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "create"  -> handleCreate(player);
                case "home"    -> handleHome(player);
                case "visit"   -> handleVisit(player, args);
                case "leave"   -> handleLeave(player);
                case "upgrade" -> openMenu(player);
                default -> player.sendMessage("§cUnknown sub-command. Usage: /" + label + " [create|visit|leave|upgrade]");
            }
            return true;
        }

        @Override
        protected void openMenu(Player player) {
            new IslandMenu(player.getUniqueId()).open(player);
        }

        private void handleCreate(Player player) {
            IslandManager manager = IslandManager.getInstance();
            if (manager.hasIsland(player.getUniqueId())) {
                player.sendMessage("§cYou already have an island!");
                return;
            }
            manager.createIsland(player);
            player.sendMessage("§aYour island has been created!");
        }

        private void handleHome(Player player) {
            IslandManager manager = IslandManager.getInstance();
            Optional<org.bukkit.World> world = manager.getIslandWorld(player.getUniqueId());
            if (world.isEmpty()) {
                player.sendMessage("§cYou do not have an island yet. Use /island create.");
                return;
            }
            player.teleport(world.get().getSpawnLocation());
            player.sendMessage("§aTeleported to your island.");
        }

        private void handleVisit(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /island visit <player>");
                return;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§cPlayer not found: " + args[1]);
                return;
            }
            IslandManager manager = IslandManager.getInstance();
            Optional<org.bukkit.World> world = manager.getIslandWorld(target.getUniqueId());
            if (world.isEmpty()) {
                player.sendMessage("§c" + target.getName() + " does not have an island.");
                return;
            }
            player.teleport(world.get().getSpawnLocation());
            player.sendMessage("§aTeleported to " + target.getName() + "'s island.");
        }

        private void handleLeave(Player player) {
            boolean left = IslandManager.getInstance().leaveIsland(player.getUniqueId());
            if (left) {
                player.sendMessage("§aYou have left the island.");
            } else {
                player.sendMessage("§cYou are not a member of any island.");
            }
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String prefix = args[0].toLowerCase();
                return SUB_COMMANDS.stream().filter(s -> s.startsWith(prefix)).toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("visit")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .toList();
            }
            return List.of();
        }
    }

    // =========================================================================
    // /calendar
    // =========================================================================

    public static final class CalendarCommand extends PlayerCommand {

        private static final List<String> SUBCOMMANDS = Arrays.asList("info", "months", "events", "set");

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            if (args.length == 0) {
                openMenu(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "info"   -> handleInfo(player);
                case "months" -> handleMonths(player);
                case "events" -> handleEvents(player);
                case "set"    -> handleSet(player, args);
                default       -> player.sendMessage(
                        "§cUnknown sub-command. Usage: /" + label + " [info|months|events|set]");
            }
            return true;
        }

        @Override
        protected void openMenu(Player player) {
            new CalendarMenu().open(player);
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
            CalendarManager calendar = CalendarManager.getInstance();
            CalendarManager.SkyBlockMonth month = calendar.getCurrentMonth();
            int dayOfMonth = calendar.getCurrentDayOfMonth();
            int yearDay = calendar.getCurrentDay();
            player.sendMessage("=== SkyBlock Calendar ===");
            player.sendMessage(String.format("Date: %s %d (Year Day %d / %d)",
                    month.getDisplayName(), dayOfMonth, yearDay, CalendarManager.DAYS_PER_YEAR));
            List<String> events = calendar.getEventsToday();
            if (!events.isEmpty()) {
                player.sendMessage("Today's events: " + String.join(", ", events));
            }
        }

        private void handleMonths(Player player) {
            player.sendMessage("=== SkyBlock Months ===");
            CalendarManager.SkyBlockMonth[] months = CalendarManager.SkyBlockMonth.values();
            for (int i = 0; i < months.length; i++) {
                player.sendMessage(String.format("%d. %s", i + 1, months[i].getDisplayName()));
            }
        }

        private void handleEvents(Player player) {
            int count = CalendarManager.getInstance().getEventParticipation(player.getUniqueId());
            player.sendMessage("Calendar events participated in this year: " + count);
        }

        private void handleSet(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /calendar set <day>");
                return;
            }
            int day;
            try {
                day = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid day: '" + args[1] + "'. Please provide a number between 1 and "
                        + CalendarManager.DAYS_PER_YEAR + ".");
                return;
            }
            try {
                CalendarManager calendar = CalendarManager.getInstance();
                calendar.setCurrentDay(day);
                CalendarManager.SkyBlockMonth month = calendar.getCurrentMonth();
                int dayOfMonth = calendar.getCurrentDayOfMonth();
                player.sendMessage(String.format("Calendar set to %s %d (Year Day %d).",
                        month.getDisplayName(), dayOfMonth, day));
            } catch (IllegalArgumentException e) {
                player.sendMessage("Day out of range. Please provide a number between 1 and "
                        + CalendarManager.DAYS_PER_YEAR + ".");
            }
        }
    }

    // =========================================================================
    // /mayor (command-package variant; see also mayor.MayorCommand)
    // =========================================================================

    public static final class MayorCommand extends PlayerCommand {

        private final MayorManager mayorManager;

        public MayorCommand(MayorManager mayorManager) {
            this.mayorManager = mayorManager;
        }

        @Override
        protected void openMenu(Player player) {
            showStatus(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            if (args.length == 0) {
                showStatus(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "vote"    -> handleVote(player, args);
                case "set"     -> handleSet(player, args);
                case "info"    -> handleInfo(player);
                case "history" -> handleHistory(player);
                default        -> sendHelp(player, label);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String lower = args[0].toLowerCase();
                return Arrays.asList("vote", "set", "info", "history").stream()
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            if (args.length == 2 && (args[0].equalsIgnoreCase("vote") || args[0].equalsIgnoreCase("set"))) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(MayorManager.MayorCandidate.values())
                        .map(c -> c.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            return List.of();
        }

        private void showStatus(Player player) {
            MayorManager.MayorCandidate mayor = mayorManager.getCurrentMayor();
            if (mayor == null) {
                player.sendMessage("§eNo mayor is currently in office.");
            } else {
                player.sendMessage("§6Current Mayor: §f" + mayor.getDisplayName());
                player.sendMessage("§7Perks: §f" + String.join(", ", mayor.getPerks()));
            }
            player.sendMessage("§7Election in §f" + mayorManager.getDaysUntilElection() + "§7 day(s).");
            MayorManager.MayorCandidate vote = mayorManager.getVote(player.getUniqueId());
            if (vote != null) {
                player.sendMessage("§7Your vote: §f" + vote.getDisplayName());
            }
        }

        private void handleVote(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /mayor vote <candidate>");
                return;
            }
            MayorManager.MayorCandidate candidate = parseMayor(player, args[1]);
            if (candidate == null) return;
            mayorManager.vote(player.getUniqueId(), candidate);
            player.sendMessage("§aYou voted for §f" + candidate.getDisplayName() + "§a!");
        }

        private void handleSet(Player player, String[] args) {
            if (!player.hasPermission("skyblock.mayor.admin")) {
                player.sendMessage("§cYou do not have permission to set the mayor.");
                return;
            }
            if (args.length < 2) {
                player.sendMessage("§cUsage: /mayor set <candidate>");
                return;
            }
            MayorManager.MayorCandidate candidate = parseMayor(player, args[1]);
            if (candidate == null) return;
            mayorManager.setCurrentMayor(candidate);
            player.sendMessage("§aMayor set to §f" + candidate.getDisplayName() + "§a.");
        }

        private void handleInfo(Player player) {
            MayorManager.MayorCandidate mayor = mayorManager.getCurrentMayor();
            if (mayor == null) {
                player.sendMessage("§eNo mayor is currently in office.");
                return;
            }
            player.sendMessage("§6=== Mayor: " + mayor.getDisplayName() + " ===");
            player.sendMessage("§7Perks:");
            for (String perk : mayor.getPerks()) {
                player.sendMessage("  §f- " + perk);
            }
            player.sendMessage("§7Stat Bonuses:");
            Map<Stat, Double> bonuses = mayorManager.getActiveStatBonuses();
            if (bonuses.isEmpty()) {
                player.sendMessage("  §7None");
            } else {
                bonuses.forEach((stat, val) ->
                        player.sendMessage("  §f" + stat.name() + ": +" + val));
            }
            player.sendMessage("§7Cycle day: §f" + mayorManager.getCycleDay()
                    + "§7 / §f" + MayorManager.ELECTION_CYCLE_DAYS);
        }

        private void handleHistory(Player player) {
            List<String> history = mayorManager.getElectionHistory();
            if (history.isEmpty()) {
                player.sendMessage("§7No election history yet.");
                return;
            }
            player.sendMessage("§6=== Election History ===");
            int start = Math.max(0, history.size() - 10);
            for (int i = start; i < history.size(); i++) {
                player.sendMessage("§7" + history.get(i));
            }
        }

        private MayorManager.MayorCandidate parseMayor(Player player, String input) {
            try {
                return MayorManager.MayorCandidate.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cUnknown candidate: §f" + input
                        + "§c. Valid candidates: "
                        + Arrays.stream(MayorManager.MayorCandidate.values())
                                .map(c -> c.name().toLowerCase())
                                .reduce((a, b) -> a + ", " + b).orElse(""));
                return null;
            }
        }

        private void sendHelp(Player player, String label) {
            player.sendMessage("§6=== Mayor Commands ===");
            player.sendMessage("§7/" + label + " §f— view current mayor");
            player.sendMessage("§7/" + label + " vote <candidate> §f— vote for a candidate");
            player.sendMessage("§7/" + label + " info §f— detailed mayor info and stat bonuses");
            player.sendMessage("§7/" + label + " history §f— view election history");
            player.sendMessage("§7/" + label + " set <candidate> §f— force-set mayor (admin)");
        }
    }

    // =========================================================================
    // /mining
    // =========================================================================

    public static final class MiningCommand implements TabExecutor {

        private static final List<String> SUBCOMMANDS =
                Arrays.asList("ores", "speedbonus", "zones", "powder", "hotm", "menu");

        private final MiningManager miningManager;

        public MiningCommand(MiningManager miningManager) {
            if (miningManager == null) {
                throw new IllegalArgumentException("miningManager must not be null");
            }
            this.miningManager = miningManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                handleStatus(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "ores"       -> handleOres(player);
                case "speedbonus" -> handleSpeedBonus(player);
                case "zones"      -> handleZones(player);
                case "powder"     -> handlePowder(player);
                case "hotm"       -> handleHotm(player);
                case "menu"       -> new MiningMenu(player).open(player);
                default           -> sendHelp(player);
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

        private void handleStatus(Player player) {
            UUID id = player.getUniqueId();
            int level = miningManager.getLevel(id);
            double xp = miningManager.getXp(id);
            int speedBonus = miningManager.getSpeedBonusForPlayer(id);
            player.sendMessage("=== Mining ===");
            player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
            player.sendMessage("Speed Bonus: +" + speedBonus);
        }

        private void handleOres(Player player) {
            player.sendMessage("=== Ore XP Values ===");
            for (MiningManager.OreType ore : MiningManager.OreType.values()) {
                player.sendMessage("  " + ore.name() + ": " + ore.getXp() + " XP");
            }
        }

        private void handleSpeedBonus(Player player) {
            player.sendMessage("=== Mining Speed Bonus Table ===");
            for (MiningManager.MiningSpeedBonus entry : miningManager.getSpeedTable()) {
                String range = entry.getMinLevel() == entry.getMaxLevel()
                        ? "Level " + entry.getMinLevel()
                        : "Level " + entry.getMinLevel() + "-" + entry.getMaxLevel();
                player.sendMessage("  " + range + ": +" + entry.getSpeedBonus());
            }
        }

        private void handleZones(Player player) {
            int level = miningManager.getLevel(player.getUniqueId());
            player.sendMessage("=== Mining Zones (your level: " + level + ") ===");
            for (MiningManager.MiningArea zone : MiningManager.MiningArea.values()) {
                String status = level >= zone.getMinLevel() ? "Unlocked" : "Requires level " + zone.getMinLevel();
                player.sendMessage("  " + zone.getDisplayName() + ": " + status);
            }
        }

        private void handlePowder(Player player) {
            UUID id = player.getUniqueId();
            HOTMManager hotm = HOTMManager.getInstance();
            player.sendMessage("=== Mining Powder ===");
            player.sendMessage("Mithril Powder:  " + String.format("%,d", hotm.getMithrilPowder(id)));
            player.sendMessage("Gemstone Powder: " + String.format("%,d", hotm.getGemstonePowder(id)));
        }

        private void handleHotm(Player player) {
            UUID id = player.getUniqueId();
            HOTMManager hotm = HOTMManager.getInstance();
            int tier = hotm.getHotmTier(id);
            long xp = hotm.getMiningXp(id);
            player.sendMessage("=== Heart of the Mountain ===");
            player.sendMessage("HOTM Tier: " + tier + " / " + HOTMManager.MAX_TIER);
            player.sendMessage("HOTM XP:   " + String.format("%,d", xp));
            player.sendMessage("Mithril Powder:  " + String.format("%,d", hotm.getMithrilPowder(id)));
            player.sendMessage("Gemstone Powder: " + String.format("%,d", hotm.getGemstonePowder(id)));
            if (!hotm.getHotmHistory(id).isEmpty()) {
                List<String> history = hotm.getHotmHistory(id);
                player.sendMessage("Last upgrade: " + history.get(history.size() - 1));
            }
        }

        private void sendHelp(Player player) {
            player.sendMessage("=== Mining Commands ===");
            player.sendMessage("/mining                — show mining level, XP, and speed bonus");
            player.sendMessage("/mining ores           — list ore types and their XP values");
            player.sendMessage("/mining speedbonus     — show the full speed-bonus table");
            player.sendMessage("/mining zones          — list mining zones and their level requirements");
            player.sendMessage("/mining powder         — show Mithril and Gemstone powder amounts");
            player.sendMessage("/mining hotm           — show Heart of the Mountain tier and XP");
            player.sendMessage("/mining menu           — open the Mining overview menu");
        }
    }

    // =========================================================================
    // /season
    // =========================================================================

    public static final class SeasonCommand implements TabExecutor {

        private static final List<String> SUBCOMMANDS = Arrays.asList("current", "next", "set", "advance");
        private static final List<String> SEASON_NAMES = Arrays.stream(SeasonManager.Season.values())
                .map(s -> s.name().toLowerCase())
                .collect(Collectors.toList());

        private final SeasonManager seasonManager;

        public SeasonCommand(SeasonManager seasonManager) {
            this.seasonManager = seasonManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                player.sendMessage("Usage: /season <current|next|set <season>|advance>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "current" -> handleCurrent(player);
                case "next"    -> handleNext(player);
                case "set"     -> handleSet(player, args);
                case "advance" -> handleAdvance(player);
                default        -> player.sendMessage("Unknown subcommand. Usage: /season <current|next|set|advance>");
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
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                String prefix = args[1].toLowerCase();
                return SEASON_NAMES.stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        private void handleCurrent(Player player) {
            SeasonManager.Season season = seasonManager.getCurrentSeason();
            player.sendMessage("Current Season: " + season.displayName() + " (Day " + seasonManager.getDay() + ")");
        }

        private void handleNext(Player player) {
            SeasonManager.Season next = seasonManager.getNextSeason();
            player.sendMessage("Next Season: " + next.displayName());
        }

        private void handleSet(Player player, String[] args) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            if (args.length < 2) {
                player.sendMessage("Usage: /season set <season>");
                return;
            }
            SeasonManager.Season season = parseSeason(player, args[1]);
            if (season == null) return;
            seasonManager.setCurrentSeason(season);
            player.sendMessage("Season set to: " + season.displayName());
        }

        private void handleAdvance(Player player) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            SeasonManager.Season newSeason = seasonManager.advanceSeason();
            player.sendMessage("Season advanced to: " + newSeason.displayName());
        }

        private SeasonManager.Season parseSeason(Player player, String input) {
            try {
                return SeasonManager.Season.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown season: " + input
                        + ". Valid seasons: " + String.join(", ", SEASON_NAMES));
                return null;
            }
        }
    }

    // =========================================================================
    // /networth
    // =========================================================================

    public static final class NetWorthCommand implements TabExecutor {

        private final BankManager bankManager;

        public NetWorthCommand(BankManager bankManager) {
            this.bankManager = bankManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            UUID id = player.getUniqueId();
            double bank = bankManager.getBalance(id);
            long purse = bankManager.getPurseBalance(id);
            double total = bank + purse;
            player.sendMessage("=== Net Worth ===");
            player.sendMessage("Purse: " + purse + " coins");
            player.sendMessage("Bank: " + bank + " coins");
            player.sendMessage("Total: " + total + " coins");
            player.sendMessage("Bank Tier: " + BankManager.BankTier.forBalance(total).getDisplayName());
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /repair
    // =========================================================================

    public static final class RepairCommand implements TabExecutor {

        private final RepairManager repairManager;

        public RepairCommand(RepairManager repairManager) {
            if (repairManager == null) {
                throw new IllegalArgumentException("repairManager must not be null");
            }
            this.repairManager = repairManager;
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
                case "hand" -> handleHand(player);
                case "all"  -> handleAll(player);
                default     -> sendHelp(player);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String lower = args[0].toLowerCase();
                return Arrays.asList("hand", "all").stream()
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            return Collections.emptyList();
        }

        private void handleHand(Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            int cost = repairManager.getRepairCost(item);
            if (cost < 0) {
                player.sendMessage("That item cannot be repaired.");
                return;
            }
            if (cost == 0) {
                player.sendMessage("That item is already fully repaired.");
                return;
            }
            if (!repairManager.hasCoins(player, cost)) {
                player.sendMessage("You need " + cost + " coins to repair that item.");
                return;
            }
            repairManager.deductCoins(player, cost);
            repairManager.repair(item);
            player.sendMessage("Item repaired for " + cost + " coins.");
        }

        private void handleAll(Player player) {
            ItemStack[] contents = player.getInventory().getContents();
            int totalCost = 0;
            for (ItemStack item : contents) {
                int cost = repairManager.getRepairCost(item);
                if (cost > 0) {
                    totalCost += cost;
                }
            }
            if (totalCost == 0) {
                player.sendMessage("All your items are already fully repaired or cannot be repaired.");
                return;
            }
            if (!repairManager.hasCoins(player, totalCost)) {
                player.sendMessage("You need " + totalCost + " coins to repair all your items.");
                return;
            }
            repairManager.deductCoins(player, totalCost);
            int repaired = 0;
            for (ItemStack item : contents) {
                if (repairManager.repair(item)) {
                    repaired++;
                }
            }
            player.sendMessage("Repaired " + repaired + " item(s) for " + totalCost + " coins.");
        }

        private void sendHelp(Player player) {
            player.sendMessage("=== Repair Commands ===");
            player.sendMessage("/repair hand — repair the item in your hand");
            player.sendMessage("/repair all  — repair all items in your inventory");
        }
    }

    // =========================================================================
    // /reforge
    // =========================================================================

    public static final class ReforgeCommand implements TabExecutor {

        private static final List<String> SUBCOMMANDS = Arrays.asList("status", "list", "info", "apply", "clear", "stone", "itemtype");
        private static final List<String> STONE_SUBCOMMANDS = Arrays.asList("list", "info");

        private final ReforgeManager reforgeManager;

        public ReforgeCommand(ReforgeManager reforgeManager) {
            if (reforgeManager == null) {
                throw new IllegalArgumentException("reforgeManager must not be null");
            }
            this.reforgeManager = reforgeManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                new ReforgeMenu().open(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "status"   -> handleStatus(player);
                case "list"     -> handleList(player);
                case "info"     -> handleInfo(player, args);
                case "apply"    -> handleApply(player, args);
                case "clear"    -> handleClear(player);
                case "stone"    -> handleStone(player, args);
                case "itemtype" -> handleItemType(player);
                default         -> sendHelp(player);
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
            if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("apply"))) {
                String prefix = args[1].toLowerCase();
                return Arrays.stream(ReforgeManager.ReforgeType.values())
                        .filter(r -> r != ReforgeManager.ReforgeType.NONE)
                        .map(ReforgeManager.ReforgeType::getDisplayName)
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("stone")) {
                String prefix = args[1].toLowerCase();
                return STONE_SUBCOMMANDS.stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("stone") && args[1].equalsIgnoreCase("info")) {
                String prefix = args[2].toLowerCase();
                return Arrays.stream(ReforgeManager.ReforgeStone.values())
                        .map(ReforgeManager.ReforgeStone::getDisplayName)
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        private void handleStatus(Player player) {
            UUID id = player.getUniqueId();
            ReforgeManager.ReforgeType reforge = reforgeManager.getReforge(id);
            player.sendMessage("=== Reforge ===");
            player.sendMessage("Current: " + reforge.getDisplayName());
            if (reforge != ReforgeManager.ReforgeType.NONE) {
                printBonuses(player, reforge);
            }
        }

        private void handleList(Player player) {
            player.sendMessage("=== Available Reforges ===");
            for (ReforgeManager.ReforgeType r : ReforgeManager.ReforgeType.values()) {
                if (r == ReforgeManager.ReforgeType.NONE) continue;
                player.sendMessage("  " + r.getDisplayName()
                        + " [STR+" + r.getStrengthBonus()
                        + " DEF+" + r.getDefenseBonus()
                        + " SPD+" + r.getSpeedBonus() + "]");
            }
        }

        private void handleInfo(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /reforge info <name>");
                return;
            }
            ReforgeManager.ReforgeType reforge = ReforgeManager.ReforgeType.fromName(args[1]);
            if (reforge == null || reforge == ReforgeManager.ReforgeType.NONE) {
                player.sendMessage("Unknown reforge: " + args[1]);
                return;
            }
            player.sendMessage("=== " + reforge.getDisplayName() + " ===");
            printBonuses(player, reforge);
        }

        private void handleApply(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /reforge apply <name>");
                return;
            }
            ReforgeManager.ReforgeType reforge = ReforgeManager.ReforgeType.fromName(args[1]);
            if (reforge == null || reforge == ReforgeManager.ReforgeType.NONE) {
                player.sendMessage("Unknown reforge: " + args[1]);
                return;
            }
            reforgeManager.setReforge(player.getUniqueId(), reforge);
            player.sendMessage("Applied reforge: " + reforge.getDisplayName());
            printBonuses(player, reforge);
        }

        private void handleClear(Player player) {
            reforgeManager.clearReforge(player.getUniqueId());
            player.sendMessage("Reforge cleared.");
        }

        private void handleStone(Player player, String[] args) {
            if (args.length < 2 || args[1].equalsIgnoreCase("list")) {
                player.sendMessage("=== Reforge Stones ===");
                for (ReforgeManager.ReforgeStone stone : ReforgeManager.ReforgeStone.values()) {
                    player.sendMessage("  " + stone.getDisplayName() + " → " + stone.getReforge());
                }
                return;
            }
            if (args[1].equalsIgnoreCase("info")) {
                if (args.length < 3) {
                    player.sendMessage("Usage: /reforge stone info <stone>");
                    return;
                }
                ReforgeManager.ReforgeStone stone = ReforgeManager.ReforgeStone.fromName(args[2]);
                if (stone == null) {
                    player.sendMessage("Unknown reforge stone: " + args[2]);
                    return;
                }
                player.sendMessage("=== " + stone.getDisplayName() + " ===");
                player.sendMessage("  Applies reforge: " + stone.getReforge());
                return;
            }
            player.sendMessage("Usage: /reforge stone [list|info <stone>]");
        }

        private void handleItemType(Player player) {
            player.sendMessage("=== Reforgeable Item Types ===");
            for (ReforgeManager.ReforgeItemType type : ReforgeManager.ReforgeItemType.values()) {
                player.sendMessage("  " + type.getDisplayName());
            }
        }

        private void sendHelp(Player player) {
            player.sendMessage("=== Reforge Commands ===");
            player.sendMessage("/reforge                       — show current reforge and bonuses");
            player.sendMessage("/reforge list                  — list all available reforges");
            player.sendMessage("/reforge info <name>           — show stat bonuses for a reforge");
            player.sendMessage("/reforge apply <name>          — apply a reforge");
            player.sendMessage("/reforge clear                 — remove the current reforge");
            player.sendMessage("/reforge stone [list]          — list all reforge stones");
            player.sendMessage("/reforge stone info <stone>    — show which reforge a stone applies");
            player.sendMessage("/reforge itemtype              — list reforgeable item categories");
        }

        private static void printBonuses(Player player, ReforgeManager.ReforgeType reforge) {
            player.sendMessage("  Strength: +" + reforge.getStrengthBonus());
            player.sendMessage("  Defense:  +" + reforge.getDefenseBonus());
            player.sendMessage("  Speed:    +" + reforge.getSpeedBonus());
        }
    }

    // =========================================================================
    // /bank
    // =========================================================================

    public static final class BankCommand extends PlayerCommand {

        private final BankManager bankManager;

        public BankCommand(BankManager bankManager) {
            this.bankManager = bankManager;
        }

        @Override
        protected void openMenu(Player p) {
            new BankMenu(p.getUniqueId()).open(p);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            if (args.length == 0) {
                openMenu(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "balance"  -> handleBalance(player);
                case "deposit"  -> handleDeposit(player, args);
                case "withdraw" -> handleWithdraw(player, args);
                case "tier"     -> handleTier(player, args);
                case "type"     -> handleType(player, args);
                case "history"  -> handleHistory(player);
                case "interest" -> handleInterest(player);
                case "coop"     -> handleCoop(player, args);
                default         -> sendHelp(player);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String lower = args[0].toLowerCase();
                return Arrays.asList("balance", "deposit", "withdraw", "tier", "type", "history", "interest", "coop").stream()
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("coop")) {
                String lower = args[1].toLowerCase();
                return Arrays.asList("balance", "deposit", "withdraw").stream()
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("type")) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(BankManager.BankType.values())
                        .map(t -> t.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("tier")) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(BankManager.BankTier.values())
                        .map(t -> t.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            return Collections.emptyList();
        }

        private void handleBalance(Player player) {
            double balance = bankManager.getBalance(player.getUniqueId());
            player.sendMessage("Your bank balance: " + balance + " coins");
        }

        private void handleDeposit(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /bank deposit <amount>");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount.");
                return;
            }
            try {
                bankManager.deposit(player.getUniqueId(), amount);
                player.sendMessage("Deposited " + amount + " coins. New balance: " + bankManager.getBalance(player.getUniqueId()));
            } catch (IllegalArgumentException e) {
                player.sendMessage(e.getMessage());
            }
        }

        private void handleWithdraw(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /bank withdraw <amount>");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount.");
                return;
            }
            try {
                bankManager.withdraw(player.getUniqueId(), amount);
                player.sendMessage("Withdrew " + amount + " coins. New balance: " + bankManager.getBalance(player.getUniqueId()));
            } catch (IllegalArgumentException e) {
                player.sendMessage(e.getMessage());
            }
        }

        private void handleTier(Player player, String[] args) {
            if (args.length < 2) {
                BankManager.BankTier tier = bankManager.getTier(player.getUniqueId());
                player.sendMessage("Your bank tier: " + tier.getDisplayName() + " (interest rate: " + tier.getInterestRate() + "%)");
                return;
            }
            try {
                BankManager.BankTier tier = BankManager.BankTier.valueOf(args[1].toUpperCase());
                bankManager.setTier(player.getUniqueId(), tier);
                player.sendMessage("Bank tier set to: " + tier.getDisplayName());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown tier. Valid tiers: STARTER, GOLD, DELUXE, SUPER_DELUXE, PREMIER, PREMIER_PLUS");
            }
        }

        private void handleType(Player player, String[] args) {
            if (args.length < 2) {
                BankManager.BankType type = bankManager.getBankType(player.getUniqueId());
                player.sendMessage("Your bank type: " + type.getDisplayName() + (type.isShared() ? " (shared with island)" : ""));
                return;
            }
            try {
                BankManager.BankType type = BankManager.BankType.valueOf(args[1].toUpperCase());
                bankManager.setBankType(player.getUniqueId(), type);
                player.sendMessage("Bank type set to: " + type.getDisplayName());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown type. Valid types: PERSONAL, ISLAND");
            }
        }

        private void handleHistory(Player player) {
            List<String> history = bankManager.getAccount(player.getUniqueId()).transactionHistory();
            if (history.isEmpty()) {
                player.sendMessage("No transactions recorded.");
                return;
            }
            player.sendMessage("=== Transaction History ===");
            int start = Math.max(0, history.size() - 10);
            for (int i = start; i < history.size(); i++) {
                player.sendMessage(history.get(i));
            }
        }

        private void handleInterest(Player player) {
            double interest = bankManager.applyInterest(player.getUniqueId());
            if (interest <= 0) {
                player.sendMessage("No interest applied (balance is zero).");
            } else {
                player.sendMessage(String.format("Interest applied: +%.2f coins. New balance: %.2f coins",
                        interest, bankManager.getBalance(player.getUniqueId())));
            }
        }

        private void handleCoop(Player player, String[] args) {
            if (args.length < 3) {
                player.sendMessage("Usage: /bank coop <balance|deposit|withdraw> <coopName> [amount]");
                return;
            }
            String sub = args[1].toLowerCase();
            String coopName = args[2];
            switch (sub) {
                case "balance" -> {
                    double balance = bankManager.getCoopBalance(coopName);
                    player.sendMessage("Co-op bank balance (" + coopName + "): " + balance + " coins");
                }
                case "deposit" -> {
                    if (args.length < 4) {
                        player.sendMessage("Usage: /bank coop deposit <coopName> <amount>");
                        return;
                    }
                    double amount = parseAmount(player, args[3]);
                    if (amount <= 0) return;
                    try {
                        bankManager.depositCoop(coopName, amount);
                        player.sendMessage("Deposited " + amount + " coins into co-op bank (" + coopName
                                + "). New balance: " + bankManager.getCoopBalance(coopName));
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(e.getMessage());
                    }
                }
                case "withdraw" -> {
                    if (args.length < 4) {
                        player.sendMessage("Usage: /bank coop withdraw <coopName> <amount>");
                        return;
                    }
                    double amount = parseAmount(player, args[3]);
                    if (amount <= 0) return;
                    try {
                        bankManager.withdrawCoop(coopName, amount);
                        player.sendMessage("Withdrew " + amount + " coins from co-op bank (" + coopName
                                + "). New balance: " + bankManager.getCoopBalance(coopName));
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(e.getMessage());
                    }
                }
                default -> player.sendMessage("Usage: /bank coop <balance|deposit|withdraw> <coopName> [amount]");
            }
        }

        private double parseAmount(Player player, String input) {
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    player.sendMessage("Amount must be a positive number.");
                    return 0;
                }
                return amount;
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount: " + input);
                return 0;
            }
        }

        private void sendHelp(Player player) {
            player.sendMessage("=== Bank Commands ===");
            player.sendMessage("/bank balance — view your balance");
            player.sendMessage("/bank deposit <amount> — deposit coins");
            player.sendMessage("/bank withdraw <amount> — withdraw coins");
            player.sendMessage("/bank tier [tier] — view or set your bank tier");
            player.sendMessage("/bank type [type] — view or set your bank type");
            player.sendMessage("/bank history — view recent transactions");
            player.sendMessage("/bank interest — apply interest to your balance");
            player.sendMessage("/bank coop <balance|deposit|withdraw> <coopName> [amount] — manage co-op bank");
        }
    }

    // =========================================================================
    // /accessorybag
    // =========================================================================

    public static final class AccessoryBagCommand implements TabExecutor {

        private static final List<String> SUBCOMMANDS = Arrays.asList("list", "add", "remove", "bonuses", "rarity", "tier");

        private final AccessoryBagManager accessoryBagManager;

        public AccessoryBagCommand(AccessoryBagManager accessoryBagManager) {
            this.accessoryBagManager = accessoryBagManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                new AccessoryBagMenu(player).open(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "list"    -> handleList(player);
                case "add"     -> handleAdd(player, args);
                case "remove"  -> handleRemove(player, args);
                case "bonuses" -> handleBonuses(player);
                case "rarity"  -> handleRarity(player, args);
                case "tier"    -> handleTier(player, args);
                default        -> player.sendMessage("Unknown subcommand. Usage: /accessorybag <list|add|remove|bonuses|rarity|tier>");
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
                if (sub.equals("add") || sub.equals("remove")) {
                    String prefix = args[1].toUpperCase();
                    return Arrays.stream(TalismanManager.TalismanType.values())
                            .map(Enum::name)
                            .filter(n -> n.startsWith(prefix))
                            .sorted()
                            .collect(Collectors.toList());
                }
                if (sub.equals("rarity")) {
                    String prefix = args[1].toUpperCase();
                    return Arrays.stream(AccessoryRarity.values())
                            .map(Enum::name)
                            .filter(n -> n.startsWith(prefix))
                            .sorted()
                            .collect(Collectors.toList());
                }
                if (sub.equals("tier")) {
                    String prefix = args[1].toUpperCase();
                    return Arrays.stream(AccessoryBagManager.AccessoryTier.values())
                            .map(Enum::name)
                            .filter(n -> n.startsWith(prefix))
                            .sorted()
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        }

        private void handleList(Player player) {
            var contents = accessoryBagManager.getContents(player.getUniqueId());
            int size = accessoryBagManager.getSize(player.getUniqueId());
            player.sendMessage(String.format("=== Accessory Bag (%d/%d) ===", size, AccessoryBagManager.MAX_SLOTS));
            if (contents.isEmpty()) {
                player.sendMessage("Your accessory bag is empty. Use /accessorybag add <type> to add accessories.");
                return;
            }
            contents.stream()
                    .sorted((a, b) -> a.name().compareTo(b.name()))
                    .forEach(t -> player.sendMessage(String.format("  %s — +%.1f %s", t.name(), t.bonus, t.stat.name())));
        }

        private void handleAdd(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /accessorybag add <type>");
                return;
            }
            TalismanManager.TalismanType type = parseType(args[1]);
            if (type == null) {
                player.sendMessage("Unknown accessory type: " + args[1] + ". Use /accessorybag add for available types.");
                return;
            }
            if (accessoryBagManager.hasAccessory(player.getUniqueId(), type)) {
                player.sendMessage(type.name() + " is already in your accessory bag.");
                return;
            }
            if (accessoryBagManager.getSize(player.getUniqueId()) >= AccessoryBagManager.MAX_SLOTS) {
                player.sendMessage("Your accessory bag is full (" + AccessoryBagManager.MAX_SLOTS + "/" + AccessoryBagManager.MAX_SLOTS + ").");
                return;
            }
            accessoryBagManager.addAccessory(player.getUniqueId(), type);
            player.sendMessage(String.format("Added %s to your accessory bag (+%.1f %s).", type.name(), type.bonus, type.stat.name()));
        }

        private void handleRemove(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /accessorybag remove <type>");
                return;
            }
            TalismanManager.TalismanType type = parseType(args[1]);
            if (type == null) {
                player.sendMessage("Unknown accessory type: " + args[1] + ". Use /accessorybag list to see your accessories.");
                return;
            }
            if (accessoryBagManager.removeAccessory(player.getUniqueId(), type)) {
                player.sendMessage("Removed " + type.name() + " from your accessory bag.");
            } else {
                player.sendMessage(type.name() + " is not in your accessory bag.");
            }
        }

        private void handleBonuses(Player player) {
            Map<Stat, Double> bonuses = accessoryBagManager.getTotalBonuses(player.getUniqueId());
            if (bonuses.isEmpty()) {
                player.sendMessage("Your accessory bag is empty. Add accessories with /accessorybag add <type>.");
                return;
            }
            player.sendMessage("=== Accessory Bag Bonuses ===");
            bonuses.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> player.sendMessage(String.format("+%.1f %s", e.getValue(), e.getKey().name())));
        }

        private void handleRarity(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /accessorybag rarity <COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|SPECIAL|VERY_SPECIAL>");
                return;
            }
            AccessoryRarity rarity;
            try {
                rarity = AccessoryRarity.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown rarity: " + args[1] + ". Valid values: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, SPECIAL, VERY_SPECIAL.");
                return;
            }
            var contents = accessoryBagManager.getContentsByRarity(player.getUniqueId(), rarity);
            player.sendMessage(String.format("=== %s Accessories (x%.1f multiplier) ===",
                    rarity.getDisplayName(), rarity.statMultiplier));
            if (contents.isEmpty()) {
                player.sendMessage("You have no " + rarity.getDisplayName() + " accessories in your bag.");
                return;
            }
            contents.stream()
                    .sorted((a, b) -> a.name().compareTo(b.name()))
                    .forEach(t -> player.sendMessage(String.format("  %s — +%.1f %s", t.name(), t.bonus, t.stat.name())));
        }

        private void handleTier(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /accessorybag tier <COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|SPECIAL>");
                return;
            }
            AccessoryBagManager.AccessoryTier tier;
            try {
                tier = AccessoryBagManager.AccessoryTier.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown tier: " + args[1] + ". Valid values: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, SPECIAL.");
                return;
            }
            int magicPower = accessoryBagManager.getMagicPower(player.getUniqueId(), tier);
            player.sendMessage(String.format("=== %s Tier (%d magic power each) ===",
                    tier.getDisplayName(), tier.magicPower));
            player.sendMessage(String.format("Total magic power from %s accessories: %d",
                    tier.getDisplayName(), magicPower));
        }

        private static TalismanManager.TalismanType parseType(String name) {
            try {
                return TalismanManager.TalismanType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    // =========================================================================
    // /enchanting
    // =========================================================================

    public static final class EnchantingCommand implements TabExecutor {

        private static final List<String> SUBCOMMANDS =
                Arrays.asList("list", "info", "apply", "remove", "view", "type", "book", "history");

        private static final List<String> BOOK_SUBCOMMANDS = Arrays.asList("add", "list", "apply");

        private final EnchantingManager enchantingManager;

        public EnchantingCommand(EnchantingManager enchantingManager) {
            this.enchantingManager = enchantingManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                player.sendMessage("Usage: /enchanting <list|info|apply|remove|view|type>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "list"    -> handleList(player);
                case "info"    -> handleInfo(player, args);
                case "apply"   -> handleApply(player, args);
                case "remove"  -> handleRemove(player, args);
                case "view"    -> handleView(player);
                case "type"    -> handleType(player);
                case "book"    -> handleBook(player, args);
                case "history" -> handleHistory(player);
                default        -> player.sendMessage("Unknown subcommand. Usage: /enchanting <list|info|apply|remove|view|type|book|history>");
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
                if (sub.equals("info") || sub.equals("apply") || sub.equals("remove")) {
                    String prefix = args[1].toLowerCase();
                    return Arrays.stream(EnchantingManager.SkyBlockEnchantment.values())
                            .map(e -> e.name().toLowerCase())
                            .filter(n -> n.startsWith(prefix))
                            .sorted()
                            .collect(Collectors.toList());
                }
                if (sub.equals("book")) {
                    String prefix = args[1].toLowerCase();
                    return BOOK_SUBCOMMANDS.stream()
                            .filter(s -> s.startsWith(prefix))
                            .collect(Collectors.toList());
                }
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("book") && args[1].equalsIgnoreCase("add")) {
                String prefix = args[2].toLowerCase();
                return Arrays.stream(EnchantingManager.SkyBlockEnchantment.values())
                        .map(e -> e.name().toLowerCase())
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        private void handleList(Player player) {
            player.sendMessage("=== SkyBlock Enchant Types ===");
            Arrays.stream(EnchantingManager.SkyBlockEnchantment.values())
                    .forEach(e -> player.sendMessage(String.format(
                            "%s (max level: %d)",
                            e.getDisplayName(),
                            e.getMaxLevel())));
        }

        private void handleInfo(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /enchanting info <enchantment>");
                return;
            }
            EnchantingManager.SkyBlockEnchantment type = parseType(args[1]);
            if (type == null) {
                player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
                return;
            }
            int currentLevel = enchantingManager.getLevel(player.getUniqueId(), type);
            int maxLevel = type.getMaxLevel();
            player.sendMessage(String.format("%s — current level: %d / max level: %d",
                    type.getDisplayName(), currentLevel, maxLevel));
        }

        private void handleApply(Player player, String[] args) {
            if (args.length < 3) {
                player.sendMessage("Usage: /enchanting apply <enchantment> <level>");
                return;
            }
            EnchantingManager.SkyBlockEnchantment type = parseType(args[1]);
            if (type == null) {
                player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
                return;
            }
            int level;
            try {
                level = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Level must be a number.");
                return;
            }
            try {
                enchantingManager.setEnchantment(player.getUniqueId(), type, level);
                player.sendMessage("Applied " + type.getDisplayName() + " level " + level + ".");
            } catch (IllegalArgumentException e) {
                player.sendMessage(e.getMessage());
            }
        }

        private void handleRemove(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /enchanting remove <enchantment>");
                return;
            }
            EnchantingManager.SkyBlockEnchantment type = parseType(args[1]);
            if (type == null) {
                player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
                return;
            }
            boolean removed = enchantingManager.removeEnchantment(player.getUniqueId(), type);
            if (removed) {
                player.sendMessage("Removed " + type.getDisplayName() + ".");
            } else {
                player.sendMessage("You do not have " + type.getDisplayName() + " applied.");
            }
        }

        private void handleView(Player player) {
            Map<EnchantingManager.SkyBlockEnchantment, Integer> enchantments =
                    enchantingManager.getEnchantments(player.getUniqueId());
            if (enchantments.isEmpty()) {
                player.sendMessage("You have no active enchantments.");
                return;
            }
            player.sendMessage("=== Your Enchantments ===");
            enchantments.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> player.sendMessage(
                            e.getKey().getDisplayName() + " " + e.getValue()));
        }

        private void handleType(Player player) {
            player.sendMessage("=== SkyBlock Enchant Names ===");
            Arrays.stream(EnchantingManager.SkyBlockEnchant.values())
                    .forEach(e -> player.sendMessage(e.name()));
        }

        private void handleBook(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /enchanting book <add|list|apply>");
                return;
            }
            switch (args[1].toLowerCase()) {
                case "add" -> {
                    if (args.length < 4) {
                        player.sendMessage("Usage: /enchanting book add <enchantment> <level> [name]");
                        return;
                    }
                    EnchantingManager.SkyBlockEnchantment type = parseType(args[2]);
                    if (type == null) {
                        player.sendMessage("Unknown enchantment: " + args[2] + ". Use /enchanting list to see available enchantments.");
                        return;
                    }
                    int level;
                    try {
                        level = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("Level must be a number.");
                        return;
                    }
                    String bookName = args.length >= 5
                            ? String.join(" ", Arrays.copyOfRange(args, 4, args.length))
                            : type.getDisplayName() + " Book " + SkyblockUtils.toRoman(level);
                    try {
                        EnchantingManager.EnchantmentBook book =
                                new EnchantingManager.EnchantmentBook(bookName, type, level);
                        enchantingManager.addBook(player.getUniqueId(), book);
                        player.sendMessage("Added book \"" + bookName + "\" to your inventory.");
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(e.getMessage());
                    }
                }
                case "list" -> {
                    List<EnchantingManager.EnchantmentBook> books =
                            enchantingManager.getBooks(player.getUniqueId());
                    if (books.isEmpty()) {
                        player.sendMessage("You have no enchantment books.");
                        return;
                    }
                    player.sendMessage("=== Your Enchantment Books ===");
                    IntStream.range(0, books.size()).forEach(i -> {
                        EnchantingManager.EnchantmentBook b = books.get(i);
                        player.sendMessage(String.format("%d. %s (%s %d)",
                                i + 1, b.name(), b.enchantment().getDisplayName(), b.level()));
                    });
                }
                case "apply" -> {
                    if (args.length < 3) {
                        player.sendMessage("Usage: /enchanting book apply <index>");
                        return;
                    }
                    int index;
                    try {
                        index = Integer.parseInt(args[2]) - 1;
                    } catch (NumberFormatException e) {
                        player.sendMessage("Index must be a number.");
                        return;
                    }
                    try {
                        EnchantingManager.EnchantmentBook book =
                                enchantingManager.applyBook(player.getUniqueId(), index);
                        player.sendMessage("Applied \"" + book.name() + "\" — "
                                + book.enchantment().getDisplayName() + " " + book.level() + " is now active.");
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage("No book at that index. Use /enchanting book list to see your books.");
                    }
                }
                default -> player.sendMessage("Usage: /enchanting book <add|list|apply>");
            }
        }

        private void handleHistory(Player player) {
            List<String> history = enchantingManager.getEnchantingHistory(player.getUniqueId());
            if (history.isEmpty()) {
                player.sendMessage("You have no enchanting history.");
                return;
            }
            player.sendMessage("=== Your Enchanting History ===");
            for (int i = 0; i < history.size(); i++) {
                player.sendMessage((i + 1) + ". " + history.get(i));
            }
        }

        private static EnchantingManager.SkyBlockEnchantment parseType(String name) {
            try {
                return EnchantingManager.SkyBlockEnchantment.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    // =========================================================================
    // /warp
    // =========================================================================

    public static final class WarpCommand implements TabExecutor {

        private final WarpManager warpManager;

        public WarpCommand(WarpManager warpManager) {
            this.warpManager = warpManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (label.equalsIgnoreCase("hub")) {
                handleHub(player);
                return true;
            }
            if (args.length == 0) {
                new WarpMenu(player).open(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "set"       -> handleSet(player, args);
                case "remove"    -> handleRemove(player, args);
                case "list"      -> handleList(player);
                case "locations" -> handleLocations(player);
                case "zones"     -> handleZones(player);
                case "hub"       -> handleHub(player);
                default          -> handleTeleport(player, args[0]);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String lower = args[0].toLowerCase();
                List<String> options = new ArrayList<>(Arrays.asList("set", "remove", "list", "locations", "zones"));
                warpManager.getWarpNames().forEach(options::add);
                return options.stream().filter(s -> s.startsWith(lower)).toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                String lower = args[1].toLowerCase();
                return warpManager.getWarpNames().stream().filter(s -> s.startsWith(lower)).toList();
            }
            return Collections.emptyList();
        }

        private void handleTeleport(Player player, String name) {
            Optional<Warp> warp = warpManager.getWarp(name);
            if (warp.isEmpty()) {
                player.sendMessage("Warp '" + name + "' not found.");
                return;
            }
            player.teleport(warp.get().toLocation());
            player.sendMessage("Warped to " + name + ".");
        }

        private void handleHub(Player player) {
            Optional<Warp> warp = warpManager.getWarp(WarpManager.WarpLocation.HUB);
            if (warp.isPresent()) {
                player.teleport(warp.get().toLocation());
            } else {
                World world = Bukkit.getWorlds().get(0);
                player.teleport(world.getSpawnLocation());
            }
            player.sendMessage("Warped to the Hub.");
        }

        private void handleSet(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /warp set <name>");
                return;
            }
            warpManager.setWarp(args[1], player.getLocation());
            player.sendMessage("Warp '" + args[1] + "' set to your current location.");
        }

        private void handleRemove(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /warp remove <name>");
                return;
            }
            if (warpManager.removeWarp(args[1])) {
                player.sendMessage("Warp '" + args[1] + "' removed.");
            } else {
                player.sendMessage("Warp '" + args[1] + "' not found.");
            }
        }

        private void handleList(Player player) {
            Set<String> names = warpManager.getWarpNames();
            if (names.isEmpty()) {
                player.sendMessage("No warps are set.");
                return;
            }
            player.sendMessage("=== Warps ===");
            names.stream().sorted().forEach(n -> player.sendMessage("- " + n));
        }

        private void handleLocations(Player player) {
            player.sendMessage("=== Warp Locations ===");
            for (WarpManager.WarpLocation loc : WarpManager.WarpLocation.values()) {
                player.sendMessage("- " + loc.getDisplayName() + " (" + loc.warpKey() + ")");
            }
        }

        private void handleZones(Player player) {
            player.sendMessage("=== SkyBlock Locations ===");
            for (WarpManager.SkyBlockLocation loc : WarpManager.SkyBlockLocation.values()) {
                player.sendMessage("- " + loc.getDisplayName());
            }
        }
    }

}
