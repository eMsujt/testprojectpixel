package com.skyblock.core.menu;

import com.skyblock.core.gui.GuiBuilder;
import com.skyblock.core.menu.MenuManager.SkyBlockMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Full 54-slot GUI hub for SkyBlock, opened via {@link MenuManager#openMenu}.
 */
public final class SkyBlockMainMenu extends SkyBlockMenu {

    @Override
    public void open(Player player) {
        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ");
        Inventory inv = GuiBuilder.create(ChatColor.GOLD + "SkyBlock Menu", 6)
                // Row 1 — skills & stats
                .setItem(10, named(Material.BOOK,          ChatColor.GREEN      + "Skills"),
                        e -> e.getWhoClicked().sendMessage("Use /skills to view your skills."))
                .setItem(12, named(Material.CHEST,         ChatColor.GOLD       + "Collections"),
                        e -> e.getWhoClicked().sendMessage("Use /collection to view your collections."))
                .setItem(14, named(Material.IRON_SWORD,    ChatColor.RED        + "Slayer"),
                        e -> e.getWhoClicked().sendMessage("Use /slay to manage your slayer quests."))
                .setItem(16, named(Material.BONE,          ChatColor.AQUA       + "Pets"),
                        e -> e.getWhoClicked().sendMessage("Use /pets to manage your pets."))
                // Row 2 — economy
                .setItem(19, named(Material.GOLD_INGOT,    ChatColor.GREEN      + "Bazaar"),
                        e -> e.getWhoClicked().sendMessage("Use /bazaar to open the Bazaar."))
                .setItem(20, named(Material.SHIELD,        ChatColor.BLUE       + "Guild"),
                        e -> e.getWhoClicked().sendMessage("Use /guild to manage your guild."))
                .setItem(21, named(Material.GOLD_INGOT,    ChatColor.GOLD       + "Auction House"),
                        e -> e.getWhoClicked().sendMessage("Use /auction to open the Auction House."))
                .setItem(22, named(Material.CHEST,         ChatColor.GOLD       + "Bank"),
                        e -> e.getWhoClicked().sendMessage("Use /bank to manage your bank account."))
                .setItem(23, named(Material.POTION,        ChatColor.LIGHT_PURPLE + "Booster"),
                        e -> e.getWhoClicked().sendMessage("Use /booster to manage your active boosters."))
                .setItem(24, named(Material.FIREWORK_ROCKET, ChatColor.GOLD    + "SkyBlock Events"),
                        e -> e.getWhoClicked().sendMessage("Use /event to view and join SkyBlock events."))
                .setItem(25, named(Material.CRAFTING_TABLE,ChatColor.WHITE      + "Crafting"),
                        e -> e.getWhoClicked().sendMessage("Use /crafting to open the SkyBlock recipes."))
                .setItem(26, named(Material.EMERALD,       ChatColor.GREEN      + "Trade"),
                        e -> e.getWhoClicked().sendMessage("Use /trade to open the trade menu."))
                // Row 3 — island & progress
                .setItem(28, named(Material.GRASS_BLOCK,   ChatColor.GREEN      + "Island"),
                        e -> e.getWhoClicked().sendMessage("Use /island to manage your island."))
                .setItem(29, named(Material.SHIELD,        ChatColor.BLUE       + "Guild"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("guild"))
                .setItem(30, named(Material.WRITTEN_BOOK,  ChatColor.YELLOW     + "Quest"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("quest"))
                .setItem(31, named(Material.FIREWORK_ROCKET, ChatColor.GOLD     + "Event"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("event"))
                .setItem(32, named(Material.COMPASS,       ChatColor.YELLOW     + "Quests"),
                        e -> e.getWhoClicked().sendMessage("Use /quest to view your quests."))
                .setItem(33, named(Material.PISTON,        ChatColor.GRAY       + "Minions"),
                        e -> e.getWhoClicked().sendMessage("Use /minion to manage your minions."))
                .setItem(34, named(Material.NAME_TAG,      ChatColor.YELLOW     + "Titles"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("title"))
                .setItem(35, named(Material.IRON_PICKAXE, ChatColor.GRAY       + "Minions"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("minion"))
                // Row 4 — utility
                .setItem(37, named(Material.ENDER_PEARL,   ChatColor.LIGHT_PURPLE + "Warp"),
                        e -> e.getWhoClicked().sendMessage("Use /warp to teleport to locations."))
                .setItem(39, named(Material.BREWING_STAND, ChatColor.DARK_PURPLE  + "Alchemy"),
                        e -> e.getWhoClicked().sendMessage("Use /alchemy to view your alchemy level."))
                .setItem(41, named(Material.ENCHANTING_TABLE, ChatColor.DARK_AQUA + "Enchanting"),
                        e -> e.getWhoClicked().sendMessage("Use /enchanting to manage enchantments."))
                .setItem(43, named(Material.DIAMOND,       ChatColor.AQUA       + "Talisman Bag"),
                        e -> e.getWhoClicked().sendMessage("Use /talisman bag to open your talisman bag."))
                .setItem(44, named(Material.WRITTEN_BOOK,  ChatColor.YELLOW     + "Quest"),
                        e -> e.getWhoClicked().sendMessage("Use /quest to view your active quests."))
                .setItem(45, named(Material.PLAYER_HEAD,   ChatColor.AQUA       + "Profile"),
                        e -> e.getWhoClicked().sendMessage("Use /profile to view your SkyBlock profile."))
                // Row 5 — extras & close
                .setItem(46, named(Material.CHEST,         ChatColor.GOLD       + "Backpack"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("backpack open"))
                .setItem(47, named(Material.ANVIL,         ChatColor.GRAY       + "Reforge"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("reforge"))
                .setItem(48, named(Material.NETHER_STAR,   ChatColor.YELLOW     + "Stats"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("stats"))
                .setItem(49, named(Material.BARRIER,       ChatColor.RED        + "Close"),
                        e -> e.getWhoClicked().closeInventory())
                .setItem(50, named(Material.FURNACE,       ChatColor.ORANGE     + "Forge"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("forge"))
                .setItem(51, named(Material.FIRE_CHARGE,   ChatColor.RED        + "Kuudra"),
                        e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("kuudra"))
                .fill(filler)
                .build();
        player.openInventory(inv);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() != null
                && event.getClickedInventory().getHolder() instanceof GuiBuilder.GuiHolder holder) {
            holder.handleClick(event);
        }
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
}
