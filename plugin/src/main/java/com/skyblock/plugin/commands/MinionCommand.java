package com.skyblock.plugin.commands;

import com.skyblock.core.minion.MinionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class MinionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(id);

        if (args.length >= 1 && args[0].equalsIgnoreCase("upgrade")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /skyblock minion upgrade <slot>");
                return true;
            }
            int slot;
            try {
                slot = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid slot: " + args[1]);
                return true;
            }
            if (slot < 1 || slot > minionIds.size()) {
                player.sendMessage("Slot must be between 1 and " + minionIds.size() + ".");
                return true;
            }
            UUID minionId = minionIds.get(slot - 1);
            boolean upgraded = manager.upgradeMinion(minionId);
            if (upgraded) {
                MinionManager.MinionData data = manager.getMinion(minionId);
                player.sendMessage("Minion in slot " + slot + " upgraded to Tier " + (data.getTier().ordinal() + 1) + ".");
            } else {
                player.sendMessage("That minion is already at the maximum tier (11).");
            }
            return true;
        }

        player.sendMessage("=== Minions (" + minionIds.size() + "/" + MinionManager.MAX_SLOTS + ") ===");
        if (minionIds.isEmpty()) {
            player.sendMessage("You have no active minions.");
            return true;
        }
        int slot = 1;
        for (UUID minionId : minionIds) {
            MinionManager.MinionData data = manager.getMinion(minionId);
            if (data == null) { slot++; continue; }
            player.sendMessage("[" + slot + "] " + data.type.getDisplayName() + " — Tier: " + (data.getTier().ordinal() + 1));
            slot++;
        }
        return true;
    }
}
