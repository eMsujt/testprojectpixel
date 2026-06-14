package com.skyblock.plugin.command;

import com.skyblock.core.bank.BankManager;
import com.skyblock.core.mayor.MayorManager;
import com.skyblock.core.warp.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SkyblockHubCommand implements CommandExecutor {

    private final WarpManager warpManager;

    public SkyblockHubCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "bank":
                    handleBank(player);
                    return true;
                case "mayor":
                    handleMayor(player);
                    return true;
                default:
                    break;
            }
        }

        Location hub = warpManager.getWarp("hub");
        if (hub == null) {
            player.sendMessage("§cHub warp is not configured.");
            return true;
        }

        player.teleport(hub);
        player.sendMessage("§aTeleported to the Hub!");
        return true;
    }

    private void handleBank(Player player) {
        UUID id = player.getUniqueId();
        BankManager manager = BankManager.getInstance();
        double balance = manager.getBalance(id);
        BankManager.BankTier tier = manager.getTier(id);
        BankManager.BankType type = manager.getBankType(id);
        player.sendMessage("=== Bank ===");
        player.sendMessage("Balance: " + balance);
        player.sendMessage("Tier: " + tier.getDisplayName());
        player.sendMessage("Type: " + type.getDisplayName());
    }

    private void handleMayor(Player player) {
        UUID id = player.getUniqueId();
        MayorManager manager = MayorManager.getInstance();
        MayorManager.MayorCandidate current = manager.getCurrentMayor();
        player.sendMessage("=== Mayor ===");
        if (current != null) {
            player.sendMessage("Current Mayor: " + current.getDisplayName());
            player.sendMessage("Perks: " + String.join(", ", current.getPerks()));
        } else {
            player.sendMessage("Current Mayor: None");
        }
        MayorManager.MayorCandidate vote = manager.getVote(id);
        player.sendMessage("Your Vote: " + (vote != null ? vote.getDisplayName() : "None"));
    }
}
