package com.skyblock.core.command;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.manager.RepairManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.manager.Warp;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.menu.BankMenu;
import com.skyblock.core.menu.CalendarMenu;
import com.skyblock.core.menu.CollectionMenu;
import com.skyblock.core.menu.EssenceMenu;
import com.skyblock.core.menu.MinionMenu;
import com.skyblock.core.menu.PetsMenu;
import com.skyblock.core.menu.WardrobeMenu;
import com.skyblock.core.menu.FairySoulMenu;
import com.skyblock.core.menu.IslandMenu;
import com.skyblock.core.menu.TalismanMenu;
import com.skyblock.core.menu.MiningMenu;
import com.skyblock.core.menu.ReforgeMenu;
import com.skyblock.core.menu.WarpMenu;
import com.skyblock.core.model.Stat;
import com.skyblock.core.season.SeasonManager;
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

/**
 * Single compilation unit for small, independent SkyBlock commands.
 */
public final class CompactCommands {

    private CompactCommands() {}

    // =========================================================================
    // /collection
    // =========================================================================

    public static final class CollectionCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new CollectionMenu(player).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /essence
    // =========================================================================

    public static final class EssenceCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new EssenceMenu(player).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /minion
    // =========================================================================

    public static final class MinionCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new MinionMenu(player).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /wardrobe
    // =========================================================================

    public static final class WardrobeCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new WardrobeMenu(player).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /pets
    // =========================================================================

    public static final class PetsCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new PetsMenu(player).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /bank
    // =========================================================================

    public static final class BankCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new BankMenu(player).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /fairysoul
    // =========================================================================

    public static final class FairySoulCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new FairySoulMenu(player.getUniqueId()).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /talisman
    // =========================================================================

    public static final class TalismanCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new TalismanMenu(player.getUniqueId()).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    // =========================================================================
    // /island
    // =========================================================================

    public static final class IslandCommand extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new IslandMenu(player.getUniqueId()).open(player);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            openMenu(player);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
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
                new WarpMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player);
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
