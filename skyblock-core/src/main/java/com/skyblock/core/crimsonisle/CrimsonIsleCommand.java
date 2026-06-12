package com.skyblock.core.crimsonisle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /crimsonisle} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /crimsonisle}                       — show overview (faction, rep, keys)</li>
 *   <li>{@code /crimsonisle faction <MAGE|BARBARIAN>} — set faction alignment</li>
 *   <li>{@code /crimsonisle rep [faction]}          — view reputation</li>
 *   <li>{@code /crimsonisle keys [tier]}            — view Kuudra key counts</li>
 *   <li>{@code /crimsonisle reset}                  — clear all Crimson Isle data</li>
 * </ul>
 * </p>
 */
public final class CrimsonIsleCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("faction", "rep", "keys", "reset");

    private final CrimsonIsleManager manager;

    public CrimsonIsleCommand(CrimsonIsleManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("manager must not be null");
        }
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleOverview(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "faction" -> handleFaction(player, args);
            case "rep"     -> handleRep(player, args);
            case "keys"    -> handleKeys(player, args);
            case "reset"   -> handleReset(player);
            default        -> sendHelp(player);
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
            String prefix = args[1].toLowerCase();
            if ("faction".equals(sub)) {
                List<String> options = new ArrayList<>();
                for (CrimsonIsleManager.Faction f : CrimsonIsleManager.Faction.values()) {
                    options.add(f.name().toLowerCase());
                }
                return options.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
            }
            if ("rep".equals(sub)) {
                List<String> options = new ArrayList<>();
                for (CrimsonIsleManager.Faction f : CrimsonIsleManager.Faction.values()) {
                    options.add(f.name().toLowerCase());
                }
                return options.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
            }
            if ("keys".equals(sub)) {
                List<String> options = new ArrayList<>();
                for (CrimsonIsleManager.KuudraTier t : CrimsonIsleManager.KuudraTier.values()) {
                    options.add(t.name().toLowerCase());
                }
                return options.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleOverview(Player player) {
        UUID id = player.getUniqueId();
        CrimsonIsleManager.Faction faction = manager.getFaction(id);
        player.sendMessage("=== Crimson Isle ===");
        player.sendMessage("Faction: " + (faction != null ? faction.name() : "None"));
        Map<CrimsonIsleManager.Faction, Integer> rep = manager.getAllReputation(id);
        if (!rep.isEmpty()) {
            player.sendMessage("Reputation:");
            rep.forEach((f, r) -> player.sendMessage("  " + f.name() + ": " + r));
        }
        Map<CrimsonIsleManager.KuudraTier, Integer> keys = manager.getAllKeys(id);
        if (!keys.isEmpty()) {
            player.sendMessage("Kuudra Keys:");
            keys.forEach((t, k) -> player.sendMessage("  " + t.name() + ": " + k));
        }
    }

    private void handleFaction(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crimsonisle faction <MAGE|BARBARIAN>");
            return;
        }
        CrimsonIsleManager.Faction faction = parseFaction(args[1]);
        if (faction == null) {
            player.sendMessage("Unknown faction: " + args[1] + ". Use MAGE or BARBARIAN.");
            return;
        }
        manager.setFaction(player.getUniqueId(), faction);
        player.sendMessage("Faction set to " + faction.name() + ".");
    }

    private void handleRep(Player player, String[] args) {
        UUID id = player.getUniqueId();
        if (args.length >= 2) {
            CrimsonIsleManager.Faction faction = parseFaction(args[1]);
            if (faction == null) {
                player.sendMessage("Unknown faction: " + args[1]);
                return;
            }
            int rep = manager.getReputation(id, faction);
            player.sendMessage(faction.name() + " reputation: " + rep);
            return;
        }
        Map<CrimsonIsleManager.Faction, Integer> all = manager.getAllReputation(id);
        if (all.isEmpty()) {
            player.sendMessage("You have no faction reputation yet.");
            return;
        }
        player.sendMessage("=== Reputation ===");
        all.forEach((f, r) -> player.sendMessage("  " + f.name() + ": " + r));
    }

    private void handleKeys(Player player, String[] args) {
        UUID id = player.getUniqueId();
        if (args.length >= 2) {
            CrimsonIsleManager.KuudraTier tier = parseTier(args[1]);
            if (tier == null) {
                player.sendMessage("Unknown Kuudra tier: " + args[1]);
                return;
            }
            int keys = manager.getKeys(id, tier);
            player.sendMessage(tier.name() + " keys: " + keys);
            return;
        }
        Map<CrimsonIsleManager.KuudraTier, Integer> all = manager.getAllKeys(id);
        if (all.isEmpty()) {
            player.sendMessage("You have no Kuudra keys.");
            return;
        }
        player.sendMessage("=== Kuudra Keys ===");
        all.forEach((t, k) -> player.sendMessage("  " + t.name() + ": " + k));
    }

    private void handleReset(Player player) {
        manager.remove(player.getUniqueId());
        player.sendMessage("Your Crimson Isle data has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Crimson Isle Commands ===");
        player.sendMessage("/crimsonisle                         — overview");
        player.sendMessage("/crimsonisle faction <MAGE|BARBARIAN> — set faction");
        player.sendMessage("/crimsonisle rep [faction]           — view reputation");
        player.sendMessage("/crimsonisle keys [tier]             — view Kuudra keys");
        player.sendMessage("/crimsonisle reset                   — reset all data");
    }

    private static CrimsonIsleManager.Faction parseFaction(String name) {
        for (CrimsonIsleManager.Faction f : CrimsonIsleManager.Faction.values()) {
            if (f.name().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    private static CrimsonIsleManager.KuudraTier parseTier(String name) {
        for (CrimsonIsleManager.KuudraTier t : CrimsonIsleManager.KuudraTier.values()) {
            if (t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
