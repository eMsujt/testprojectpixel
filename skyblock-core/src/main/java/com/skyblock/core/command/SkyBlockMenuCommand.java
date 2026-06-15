package com.skyblock.core.command;

import com.skyblock.core.gui.GuiBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.menu.MenuManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command.
 *
 * <p>Opens the SkyBlock main menu GUI for the player, with shortcuts to
 * key features: Skills, Pets, Quests, Collections, Minions, Slayer,
 * Bazaar, and Auction House.</p>
 */
public final class SkyBlockMenuCommand implements TabExecutor {

    private final MenuManager menuManager;

    public SkyBlockMenuCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        menuManager.openMenu(player, new MainMenu());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    private static ItemStack named(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static final class MainMenu extends Menu {

        @Override
        public void open(Player player) {
            ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ");
            Inventory inv = GuiBuilder.create(ChatColor.GOLD + "SkyBlock Menu", 3)
                    .setItem(10, named(Material.BOOK,         ChatColor.GREEN  + "Skills"),
                            e -> e.getWhoClicked().sendMessage("Use /skills to view your skills."))
                    .setItem(12, named(Material.BONE,         ChatColor.AQUA   + "Pets"),
                            e -> e.getWhoClicked().sendMessage("Use /pets to manage your pets."))
                    .setItem(14, named(Material.COMPASS,      ChatColor.YELLOW + "Quests"),
                            e -> e.getWhoClicked().sendMessage("Use /quests to view your quests."))
                    .setItem(16, named(Material.CHEST,        ChatColor.GOLD   + "Collections"),
                            e -> e.getWhoClicked().sendMessage("Use /collections to view your collections."))
                    .setItem(19, named(Material.IRON_PICKAXE, ChatColor.GRAY   + "Minions"),
                            e -> e.getWhoClicked().sendMessage("Use /minions to manage your minions."))
                    .setItem(21, named(Material.IRON_SWORD,   ChatColor.RED    + "Slayer"),
                            e -> e.getWhoClicked().sendMessage("Use /slay to manage your slayer quests."))
                    .setItem(23, named(Material.EMERALD,      ChatColor.GREEN  + "Bazaar"),
                            e -> e.getWhoClicked().sendMessage("Use /bazaar to open the Bazaar."))
                    .setItem(25, named(Material.GOLD_INGOT,   ChatColor.GOLD   + "Auction House"),
                            e -> e.getWhoClicked().sendMessage("Use /auction to open the Auction House."))
                    .fill(filler)
                    .build();
            player.openInventory(inv);
        }

        @Override
        public void handleClick(InventoryClickEvent event) {
            if (event.getClickedInventory() != null
                    && event.getClickedInventory().getHolder() instanceof GuiBuilder.GuiHolder holder) {
                holder.handleClick(event);
            }
        }
    }
}
