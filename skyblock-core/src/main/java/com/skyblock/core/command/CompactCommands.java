package com.skyblock.core.command;

import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.menu.CalendarMenu;
import com.skyblock.core.menu.MiningMenu;
import com.skyblock.core.model.Stat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
}
