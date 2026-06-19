package com.skyblock.core.command;

import org.bukkit.command.CommandExecutor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandRegistry {

    private static final Map<String, CommandExecutor> COMMANDS = new LinkedHashMap<>();

    static {
        COMMANDS.put("auctionhouse", new AuctionHouseCommand());
        COMMANDS.put("collectionsmenu", new CollectionsCommand());
        COMMANDS.put("dungeons", new DungeonsCommand());
        COMMANDS.put("forge", new ForgeCommand());
        COMMANDS.put("garden", new GardenCommand());
        COMMANDS.put("hotm", new HotMCommand());
        COMMANDS.put("island", new IslandCommand());
        COMMANDS.put("minionsmenu", new MinionCommand());
        COMMANDS.put("profile", new ProfileCommand());
        COMMANDS.put("skyblock", new SkyBlockCommand());
        COMMANDS.put("slayer", new SlayerCommand());
        COMMANDS.put("stats", new StatsCommand());
    }

    public static void register(String name, CommandExecutor command) {
        COMMANDS.put(name, command);
    }

    public static CommandExecutor get(String name) {
        return COMMANDS.get(name);
    }

    public static Map<String, CommandExecutor> all() {
        return Collections.unmodifiableMap(COMMANDS);
    }

    private CommandRegistry() {}
}
