package com.skyblock.plugin.commands;

import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.menu.manager.SkyBlockMenuManager;
import com.skyblock.core.pet.PetCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public final class HubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        if (args.length > 0) {
            String[] rest = Arrays.copyOfRange(args, 1, args.length);
            switch (args[0].toLowerCase()) {
                case "bank"   -> new com.skyblock.core.bank.command.BankCommand(BankManager.getInstance()).onCommand(sender, command, label, rest);
                case "mayor"  -> new MayorCommand().onCommand(sender, command, label, rest);
                case "kuudra" -> new com.skyblock.core.kuudra.KuudraCommand(KuudraManager.getInstance()).onCommand(sender, command, label, rest);
                case "pets"   -> new PetCommand(PetManager.getInstance()).onCommand(sender, command, label, rest);
                default       -> player.sendMessage("Unknown sub-command. Try /hub bank, /hub mayor, /hub kuudra, or /hub pets.");
            }
            return true;
        }
        SkyBlockMenuManager.getInstance().openMainMenu(player);
        return true;
    }
}
