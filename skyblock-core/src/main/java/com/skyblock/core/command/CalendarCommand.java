package com.skyblock.core.command;

import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.menu.CalendarMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CalendarCommand extends PlayerCommand {

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
