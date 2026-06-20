package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.menu.BankMenu;
import com.skyblock.core.menu.CalendarMenu;
import com.skyblock.core.menu.ForgeMenu;
import com.skyblock.core.menu.HotmMenu;
import com.skyblock.core.menu.IslandMenu;
import com.skyblock.core.menu.MiningMenu;
import com.skyblock.core.model.Stat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Single compilation unit for four small, independent SkyBlock commands:
 * /fairysoul, /calendar, /mayor (command-package variant), and /mining.
 */
public final class CompactCommands {

    private CompactCommands() {}

    // =========================================================================
    // /fairysoul
    // =========================================================================

    public static final class FairySoulCommand implements TabExecutor {

        private static final List<String> SUBCOMMANDS = Arrays.asList("count", "areas", "stats", "collect");

        private final FairySoulManager fairySoulManager;

        public FairySoulCommand(FairySoulManager fairySoulManager) {
            this.fairySoulManager = fairySoulManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                player.sendMessage("Usage: /fairysoul <count|areas|stats|collect>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "count"   -> handleCount(player);
                case "areas"   -> handleAreas(player);
                case "stats"   -> handleStats(player);
                case "collect" -> handleCollect(player, args);
                default        -> player.sendMessage("Unknown subcommand. Usage: /fairysoul <count|areas|stats|collect>");
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String prefix = args[0].toLowerCase();
                return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("collect")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(FairySoulManager.FairyIsland.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        private void handleCount(Player player) {
            int count = fairySoulManager.getFoundCount(player.getUniqueId());
            int total = fairySoulManager.getTotalSouls();
            player.sendMessage("Fairy Souls found: " + count + " / " + total);
        }

        private void handleAreas(Player player) {
            player.sendMessage("=== Fairy Soul Islands ===");
            for (FairySoulManager.FairyIsland island : FairySoulManager.FairyIsland.values()) {
                int found = fairySoulManager.getFoundCount(player.getUniqueId(), island);
                player.sendMessage(island.getDisplayName() + ": " + found + " / " + island.getSoulCount());
            }
        }

        private void handleStats(Player player) {
            Map<Stat, Double> bonuses = fairySoulManager.getStatBonuses(player.getUniqueId());
            if (bonuses.isEmpty()) {
                player.sendMessage("You have not earned any fairy soul stat bonuses yet.");
                return;
            }
            player.sendMessage("=== Fairy Soul Bonuses ===");
            bonuses.forEach((stat, amount) ->
                    player.sendMessage(stat.getSymbol() + " " + stat.getDisplayName() + ": +" + amount));
        }

        private void handleCollect(Player player, String[] args) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            if (args.length < 3) {
                player.sendMessage("Usage: /fairysoul collect <island> <index>");
                return;
            }
            FairySoulManager.FairyIsland island;
            try {
                island = FairySoulManager.FairyIsland.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown island: " + args[1]);
                return;
            }
            int soulIndex;
            try {
                soulIndex = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid soul index: must be an integer.");
                return;
            }
            try {
                boolean added = fairySoulManager.collectSoul(player.getUniqueId(), island, soulIndex);
                if (added) {
                    player.sendMessage("Fairy Soul found! Total: "
                            + fairySoulManager.getFoundCount(player.getUniqueId()));
                } else {
                    player.sendMessage("You have already found that fairy soul.");
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(e.getMessage());
            }
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
                return Arrays.stream(BankType.values())
                        .map(t -> t.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("tier")) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(BankTier.values())
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
                BankTier tier = bankManager.getTier(player.getUniqueId());
                player.sendMessage("Your bank tier: " + tier.getDisplayName() + " (interest rate: " + tier.getInterestRate() + "%)");
                return;
            }
            try {
                BankTier tier = BankTier.valueOf(args[1].toUpperCase());
                bankManager.setTier(player.getUniqueId(), tier);
                player.sendMessage("Bank tier set to: " + tier.getDisplayName());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown tier. Valid tiers: STARTER, GOLD, DELUXE, SUPER_DELUXE, PREMIER, PREMIER_PLUS");
            }
        }

        private void handleType(Player player, String[] args) {
            if (args.length < 2) {
                BankType type = bankManager.getBankType(player.getUniqueId());
                player.sendMessage("Your bank type: " + type.getDisplayName() + (type.isShared() ? " (shared with island)" : ""));
                return;
            }
            try {
                BankType type = BankType.valueOf(args[1].toUpperCase());
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
                    double amount = parseBankAmount(player, args[3]);
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
                    double amount = parseBankAmount(player, args[3]);
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

        private double parseBankAmount(Player player, String input) {
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
    // /forge
    // =========================================================================

    public static final class ForgeCommand implements TabExecutor {

        private final ForgeManager forgeManager;

        public ForgeCommand(ForgeManager forgeManager) {
            this.forgeManager = forgeManager;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (args.length == 0) {
                new ForgeMenu(player).open(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "start"   -> handleStart(player, args);
                case "collect" -> handleCollect(player, args);
                case "cancel"  -> handleCancel(player, args);
                case "list"    -> handleList(player);
                default        -> sendHelp(player);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String lower = args[0].toLowerCase();
                return Arrays.asList("start", "collect", "cancel", "list").stream()
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
                String lower = args[1].toLowerCase();
                return forgeManager.getRecipes().keySet().stream()
                        .filter(s -> s.startsWith(lower))
                        .sorted()
                        .toList();
            }
            return Collections.emptyList();
        }

        private void handleStart(Player player, String[] args) {
            if (args.length < 2) {
                player.sendMessage("Usage: /forge start <recipe>");
                return;
            }
            String recipeId = args[1].toLowerCase();
            try {
                ForgeJob job = forgeManager.startForge(player.getUniqueId(), recipeId, System.currentTimeMillis());
                player.sendMessage("Started forging " + job.getRecipe().getDisplayName()
                        + " in slot " + (job.getSlot() + 1)
                        + " (" + formatForgeDuration(job.getDurationSeconds()) + ").");
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown recipe: " + recipeId + ". Use /forge list to see available recipes.");
            } catch (IllegalStateException e) {
                player.sendMessage("All forge slots are busy. Collect a completed item first.");
            }
        }

        private void handleCollect(Player player, String[] args) {
            try {
                ForgeJob job;
                if (args.length >= 2) {
                    int slot = Integer.parseInt(args[1]) - 1;
                    job = forgeManager.collectForge(player.getUniqueId(), slot, System.currentTimeMillis());
                } else {
                    job = forgeManager.collectForge(player.getUniqueId(), System.currentTimeMillis());
                }
                player.sendMessage("Collected " + job.getRecipe().getOutputAmount() + "x "
                        + job.getRecipe().getOutputItem() + " from forging "
                        + job.getRecipe().getDisplayName() + "!");
            } catch (NumberFormatException e) {
                player.sendMessage("Usage: /forge collect [slot]");
            } catch (IllegalStateException e) {
                player.sendMessage(e.getMessage());
            }
        }

        private void handleCancel(Player player, String[] args) {
            boolean cancelled;
            if (args.length >= 2) {
                try {
                    int slot = Integer.parseInt(args[1]) - 1;
                    cancelled = forgeManager.cancelForge(player.getUniqueId(), slot);
                } catch (NumberFormatException e) {
                    player.sendMessage("Usage: /forge cancel [slot]");
                    return;
                }
            } else {
                cancelled = forgeManager.cancelForge(player.getUniqueId());
            }
            if (cancelled) {
                player.sendMessage("Forge job cancelled.");
            } else {
                player.sendMessage("No active forge job to cancel.");
            }
        }

        private void handleList(Player player) {
            Map<Integer, ForgeJob> jobs = forgeManager.getActiveJobs(player.getUniqueId());
            if (jobs.isEmpty()) {
                player.sendMessage("You have no active forge jobs.");
                return;
            }
            long now = System.currentTimeMillis();
            player.sendMessage("=== Active Forge Jobs ===");
            for (Map.Entry<Integer, ForgeJob> entry : jobs.entrySet()) {
                ForgeJob job = entry.getValue();
                if (job.isComplete(now)) {
                    player.sendMessage("Slot " + (entry.getKey() + 1) + ": " + job.getRecipe().getDisplayName() + " — Ready!");
                } else {
                    long elapsed = (now - job.getStartTimeMillis()) / 1000L;
                    long remaining = Math.max(0, job.getDurationSeconds() - elapsed);
                    player.sendMessage("Slot " + (entry.getKey() + 1) + ": " + job.getRecipe().getDisplayName()
                            + " — " + formatForgeDuration((int) remaining) + " remaining.");
                }
            }
        }

        private void sendHelp(Player player) {
            player.sendMessage("=== Forge Commands ===");
            player.sendMessage("/forge — open the forge menu");
            player.sendMessage("/forge start <recipe> — begin forging a recipe");
            player.sendMessage("/forge collect [slot] — collect a finished forge job");
            player.sendMessage("/forge cancel [slot] — cancel an active forge job");
            player.sendMessage("/forge list — list your active forge jobs");
        }

        private static String formatForgeDuration(int seconds) {
            if (seconds <= 0) return "0s";
            int h = seconds / 3600;
            int m = (seconds % 3600) / 60;
            int s = seconds % 60;
            StringBuilder sb = new StringBuilder();
            if (h > 0) sb.append(h).append("h ");
            if (m > 0) sb.append(m).append("m ");
            if (s > 0 || sb.length() == 0) sb.append(s).append("s");
            return sb.toString().trim();
        }
    }

    // =========================================================================
    // /hotm
    // =========================================================================

    public static final class HOTMCommand extends PlayerCommand {

        private static final List<String> HOTM_SUBCOMMANDS = Arrays.asList("view", "upgrade", "set", "reset", "powder", "history");
        private static final List<String> HOTM_PERK_NAMES = Arrays.stream(HOTMManager.HotMNode.values())
                .map(p -> p.name().toLowerCase())
                .collect(Collectors.toList());

        private final HOTMManager hotmManager;

        public HOTMCommand(HOTMManager hotmManager) {
            this.hotmManager = hotmManager;
        }

        @Override
        protected void openMenu(Player p) {
            new HotmMenu(SkyBlockCore.getInstance(), p).open(p);
        }

        @Override
        protected boolean execute(Player player, Command command, String label, String[] args) {
            if (args.length == 0) {
                openMenu(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "view"    -> handleView(player, args);
                case "upgrade" -> handleUpgrade(player, args);
                case "set"     -> handleSet(player, args);
                case "reset"   -> handleReset(player);
                case "powder"  -> handlePowder(player, args);
                case "history" -> handleHistory(player);
                default        -> player.sendMessage("Unknown subcommand. Usage: /hotmtree <view|upgrade|set|reset|powder|history>");
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                String prefix = args[0].toLowerCase();
                return HOTM_SUBCOMMANDS.stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (args.length == 2) {
                String sub = args[0].toLowerCase();
                if (sub.equals("view") || sub.equals("upgrade") || sub.equals("set")) {
                    String prefix = args[1].toLowerCase();
                    return HOTM_PERK_NAMES.stream()
                            .filter(p -> p.startsWith(prefix))
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        }

        private void handleView(Player player, String[] args) {
            if (args.length >= 2) {
                HOTMManager.HotMNode perk = parseHotmPerk(player, args[1]);
                if (perk == null) return;
                int level = hotmManager.getLevel(player.getUniqueId(), perk);
                player.sendMessage(perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
            } else {
                player.sendMessage("=== Heart of the Mountain ===");
                for (HOTMManager.HotMNode perk : HOTMManager.HotMNode.values()) {
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
                player.sendMessage("Usage: /hotmtree upgrade <perk>");
                return;
            }
            HOTMManager.HotMNode perk = parseHotmPerk(player, args[1]);
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
                player.sendMessage("Usage: /hotmtree set <perk> <level>");
                return;
            }
            HOTMManager.HotMNode perk = parseHotmPerk(player, args[1]);
            if (perk == null) return;
            int level = parseHotmLevel(player, args[2]);
            if (level < 0) return;
            hotmManager.setLevel(player.getUniqueId(), perk, level);
            int actual = hotmManager.getLevel(player.getUniqueId(), perk);
            player.sendMessage(perk.getDisplayName() + " set to " + actual + ".");
        }

        private void handleReset(Player player) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            hotmManager.reset(player.getUniqueId());
            player.sendMessage("All Heart of the Mountain perks have been reset.");
        }

        private void handlePowder(Player player, String[] args) {
            if (args.length == 1) {
                long balance = hotmManager.getMithrilPowder(player.getUniqueId());
                player.sendMessage("Mithril Powder: " + balance);
                return;
            }
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            if (args.length < 3 || (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("spend"))) {
                player.sendMessage("Usage: /hotmtree powder [add|spend <amount>]");
                return;
            }
            long amount;
            try {
                amount = Long.parseLong(args[2]);
                if (amount < 0) {
                    player.sendMessage("Amount must not be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount: " + args[2]);
                return;
            }
            if (args[1].equalsIgnoreCase("add")) {
                hotmManager.addMithrilPowder(player.getUniqueId(), amount);
                player.sendMessage("Added " + amount + " Mithril Powder. Balance: " + hotmManager.getMithrilPowder(player.getUniqueId()));
            } else {
                boolean success = hotmManager.spendMithrilPowder(player.getUniqueId(), amount);
                if (success) {
                    player.sendMessage("Spent " + amount + " Mithril Powder. Balance: " + hotmManager.getMithrilPowder(player.getUniqueId()));
                } else {
                    player.sendMessage("Insufficient Mithril Powder (have " + hotmManager.getMithrilPowder(player.getUniqueId()) + ", need " + amount + ").");
                }
            }
        }

        private void handleHistory(Player player) {
            List<String> history = hotmManager.getHotmHistory(player.getUniqueId());
            player.sendMessage("=== HOTM History ===");
            if (history.isEmpty()) {
                player.sendMessage("No HOTM events recorded.");
                return;
            }
            for (int i = 0; i < history.size(); i++) {
                player.sendMessage((i + 1) + ". " + history.get(i));
            }
        }

        private HOTMManager.HotMNode parseHotmPerk(Player player, String input) {
            try {
                return HOTMManager.HotMNode.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown perk: " + input + ". Valid perks: " + String.join(", ", HOTM_PERK_NAMES));
                return null;
            }
        }

        private int parseHotmLevel(Player player, String input) {
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
    }

    // =========================================================================
    // /island
    // =========================================================================

    public static final class IslandCommand extends PlayerCommand {

        private static final List<String> ISLAND_SUB_COMMANDS = Arrays.asList("create", "home", "visit", "leave", "upgrade");

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
                default        -> player.sendMessage("§cUnknown sub-command. Usage: /" + label + " [create|visit|leave|upgrade]");
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
                return ISLAND_SUB_COMMANDS.stream().filter(s -> s.startsWith(prefix)).toList();
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
}
