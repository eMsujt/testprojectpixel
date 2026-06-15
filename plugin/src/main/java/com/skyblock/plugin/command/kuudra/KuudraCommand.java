package com.skyblock.plugin.command.kuudra;

import com.skyblock.core.kuudra.KuudraManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.plugin.commands.KuudraCommand} instead.
 */
@Deprecated
public final class KuudraCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("history")) {
            handleHistory(player);
            return true;
        }

        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();

        player.sendMessage("=== Your Kuudra Completions ===");
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            int count = manager.getCompletionCount(id, tier);
            player.sendMessage("  " + tier.getDisplayName() + ": " + count + " completions");
        }

        KuudraManager.KuudraRun run = manager.getActiveRun(id);
        if (run != null) {
            player.sendMessage("Active Run: " + run.getTier().getDisplayName() + " tier");
        }
        return true;
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = KuudraManager.getInstance().getKuudraHistory(id);
        player.sendMessage("=== Kuudra History ===");
        if (history.isEmpty()) {
            player.sendMessage("No Kuudra history found.");
            return;
        }
        for (String entry : history) {
            player.sendMessage(entry);
        }
    }
}
