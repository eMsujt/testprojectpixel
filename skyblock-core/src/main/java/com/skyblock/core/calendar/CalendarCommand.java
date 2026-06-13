package com.skyblock.core.calendar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /calendar} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /calendar info}              — show the current SkyBlock date</li>
 *   <li>{@code /calendar months}            — list all SkyBlock months</li>
 *   <li>{@code /calendar events}            — show the player's event participation count</li>
 *   <li>{@code /calendar set <day>}         — set the current year-day (admin)</li>
 * </ul>
 * </p>
 */
public final class CalendarCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("info", "months", "events", "set");

    private final CalendarManager calendarManager;

    public CalendarCommand(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /calendar <info|months|events|set>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"   -> handleInfo(player);
            case "months" -> handleMonths(player);
            case "events" -> handleEvents(player);
            case "set"    -> handleSet(player, args);
            default       -> player.sendMessage("Unknown subcommand. Usage: /calendar <info|months|events|set>");
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

    private void handleInfo(Player player) {
        CalendarManager.SkyBlockMonth month = calendarManager.getCurrentMonth();
        int dayOfMonth = calendarManager.getCurrentDayOfMonth();
        int yearDay = calendarManager.getCurrentDay();
        player.sendMessage(String.format("=== SkyBlock Calendar ==="));
        player.sendMessage(String.format("Date: %s %d (Year Day %d / %d)",
                month.getDisplayName(), dayOfMonth, yearDay, CalendarManager.DAYS_PER_YEAR));
    }

    private void handleMonths(Player player) {
        player.sendMessage("=== SkyBlock Months ===");
        CalendarManager.SkyBlockMonth[] months = CalendarManager.SkyBlockMonth.values();
        for (int i = 0; i < months.length; i++) {
            player.sendMessage(String.format("%d. %s", i + 1, months[i].getDisplayName()));
        }
    }

    private void handleEvents(Player player) {
        int count = calendarManager.getEventParticipation(player.getUniqueId());
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
            calendarManager.setCurrentDay(day);
            CalendarManager.SkyBlockMonth month = calendarManager.getCurrentMonth();
            int dayOfMonth = calendarManager.getCurrentDayOfMonth();
            player.sendMessage(String.format("Calendar set to %s %d (Year Day %d).",
                    month.getDisplayName(), dayOfMonth, day));
        } catch (IllegalArgumentException e) {
            player.sendMessage("Day out of range. Please provide a number between 1 and "
                    + CalendarManager.DAYS_PER_YEAR + ".");
        }
    }
}
