package com.skyblock.core.mob;

import com.skyblock.core.manager.MobManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /spawnmob <id> [amount]} — admin command to spawn a custom SkyBlock mob
 * (from {@code mobs.yml}) at the player's location. Tab-completes the mob ids.
 * Used to place the area-specific Hub mobs (Crypt Ghoul, Golden Ghoul, Zombie
 * Villager, Old Wolf) and for testing.
 */
public final class SpawnMobCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("skyblock.admin")) {
            player.sendMessage("§cYou don't have permission to use this.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage("§eUsage: §6/spawnmob <id> [amount]");
            player.sendMessage("§7Mobs: §f" + String.join(", ", MobManager.getInstance().getMobs().keySet()));
            return true;
        }
        MobManager.MobDefinition def = MobManager.getInstance().getMob(args[0].toLowerCase());
        if (def == null) {
            player.sendMessage("§cUnknown mob '" + args[0] + "'.");
            return true;
        }
        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Math.max(1, Math.min(20, Integer.parseInt(args[1])));
            } catch (NumberFormatException ignored) {
                // keep default
            }
        }
        for (int i = 0; i < amount; i++) {
            CustomMobManager.getInstance().spawnMob(def, player.getLocation());
        }
        player.sendMessage("§aSpawned §e" + amount + "x §f" + def.getDisplayName() + "§a.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String id : MobManager.getInstance().getMobs().keySet()) {
                if (id.startsWith(prefix)) {
                    out.add(id);
                }
            }
            return out;
        }
        return List.of();
    }
}
