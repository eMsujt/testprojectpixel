package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * 54-slot SkyBlock main menu hub, matching Hypixel's slot layout.
 *
 * <p>Content rows use the same slot positions as the reference implementation
 * ({@link com.skyblock.core.menu.SkyBlockMainMenu}); all remaining slots
 * receive a silent gray glass-pane filler.</p>
 */
public final class SkyBlockMenu extends Menu {

    private static final Set<Integer> CONTENT_SLOTS = Set.of(
            10, 12, 14, 16,
            19, 20, 21, 22, 23, 24, 25, 26,
            28, 29, 30, 31, 32, 33, 34, 35,
            37, 38, 39, 40, 41, 42, 43, 44,
            45, 46, 47, 48, 49, 50, 51
    );

    public SkyBlockMenu() {
        super(ChatColor.GOLD + "SkyBlock Menu", 6);
    }

    @Override
    protected void build() {
        // --- filler ---
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(" ")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        for (int slot = 0; slot < 54; slot++) {
            if (!CONTENT_SLOTS.contains(slot)) {
                setItem(slot, filler);
            }
        }

        // --- Row 1: skills & stats (slots 10-16 even) ---
        setItem(10, item(Material.BOOK,              ChatColor.GREEN        + "Skills"),
                e -> e.getWhoClicked().sendMessage("Use /skills to view your skills."));
        setItem(12, item(Material.CHEST,             ChatColor.GOLD         + "Collections"),
                e -> e.getWhoClicked().sendMessage("Use /collection to view your collections."));
        setItem(14, item(Material.IRON_SWORD,        ChatColor.RED          + "Slayer"),
                e -> e.getWhoClicked().sendMessage("Use /slay to manage your slayer quests."));
        setItem(16, item(Material.BONE,              ChatColor.AQUA         + "Pets"),
                e -> e.getWhoClicked().sendMessage("Use /pets to manage your pets."));

        // --- Row 2: economy (slots 19-26) ---
        setItem(19, item(Material.GOLD_INGOT,        ChatColor.GREEN        + "Bazaar"),
                e -> e.getWhoClicked().sendMessage("Use /bazaar to open the Bazaar."));
        setItem(20, item(Material.SHIELD,            ChatColor.BLUE         + "Guild"),
                e -> e.getWhoClicked().sendMessage("Use /guild to manage your guild."));
        setItem(21, item(Material.GOLD_INGOT,        ChatColor.GOLD         + "Auction House"),
                e -> e.getWhoClicked().sendMessage("Use /auction to open the Auction House."));
        setItem(22, item(Material.CHEST,             ChatColor.GOLD         + "Bank"),
                e -> e.getWhoClicked().sendMessage("Use /bank to manage your bank account."));
        setItem(23, item(Material.POTION,            ChatColor.LIGHT_PURPLE + "Booster"),
                e -> e.getWhoClicked().sendMessage("Use /booster to manage your active boosters."));
        setItem(24, item(Material.FIREWORK_ROCKET,   ChatColor.GOLD         + "SkyBlock Events"),
                e -> e.getWhoClicked().sendMessage("Use /event to view and join SkyBlock events."));
        setItem(25, item(Material.CRAFTING_TABLE,    ChatColor.WHITE        + "Crafting"),
                e -> e.getWhoClicked().sendMessage("Use /crafting to open the SkyBlock recipes."));
        setItem(26, item(Material.EMERALD,           ChatColor.GREEN        + "Trade"),
                e -> e.getWhoClicked().sendMessage("Use /trade to open the trade menu."));

        // --- Row 3: island & progress (slots 28-35) ---
        setItem(28, item(Material.GRASS_BLOCK,       ChatColor.GREEN        + "Island"),
                e -> e.getWhoClicked().sendMessage("Use /island to manage your island."));
        setItem(29, item(Material.SHIELD,            ChatColor.BLUE         + "Guild"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("guild"));
        setItem(30, item(Material.WRITTEN_BOOK,      ChatColor.YELLOW       + "Quest"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("quest"));
        setItem(31, item(Material.FIREWORK_ROCKET,   ChatColor.GOLD         + "Event"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("event"));
        setItem(32, item(Material.COMPASS,           ChatColor.YELLOW       + "Quests"),
                e -> e.getWhoClicked().sendMessage("Use /quest to view your quests."));
        setItem(33, item(Material.PISTON,            ChatColor.GRAY         + "Minions"),
                e -> e.getWhoClicked().sendMessage("Use /minion to manage your minions."));
        setItem(34, item(Material.NAME_TAG,          ChatColor.YELLOW       + "Titles"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("title"));
        setItem(35, item(Material.PLAYER_HEAD,       ChatColor.GREEN        + "Friends"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("friend"));

        // --- Row 4: utility (slots 37-44) + profile at 45 ---
        setItem(37, item(Material.ENDER_PEARL,       ChatColor.LIGHT_PURPLE + "Warp"),
                e -> e.getWhoClicked().sendMessage("Use /warp to teleport to locations."));
        setItem(38, item(Material.GOLD_BLOCK,        ChatColor.GREEN        + "Bank"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("bank"));
        setItem(39, item(Material.EXPERIENCE_BOTTLE, ChatColor.GREEN        + "Boosters"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("booster"));
        setItem(40, item(Material.PAPER,             ChatColor.YELLOW       + "Mayor"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("mayor"));
        setItem(41, item(Material.ENCHANTING_TABLE,  ChatColor.DARK_AQUA   + "Enchanting"),
                e -> e.getWhoClicked().sendMessage("Use /enchanting to manage enchantments."));
        setItem(42, item(Material.PAPER,             ChatColor.YELLOW       + "Mailbox"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("mailbox"));
        setItem(43, item(Material.DIAMOND,           ChatColor.AQUA         + "Talisman Bag"),
                e -> e.getWhoClicked().sendMessage("Use /talisman bag to open your talisman bag."));
        setItem(44, item(Material.WRITTEN_BOOK,      ChatColor.YELLOW       + "Quest"),
                e -> e.getWhoClicked().sendMessage("Use /quest to view your active quests."));
        setItem(45, item(Material.PLAYER_HEAD,       ChatColor.AQUA         + "Profile"),
                e -> e.getWhoClicked().sendMessage("Use /profile to view your SkyBlock profile."));

        // --- Row 5: extras & close (slots 46-51) ---
        setItem(46, item(Material.CHEST,             ChatColor.GOLD         + "Backpack"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("backpack open"));
        setItem(47, item(Material.ANVIL,             ChatColor.GRAY         + "Reforge"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("reforge"));
        setItem(48, item(Material.NETHER_STAR,       ChatColor.YELLOW       + "Stats"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("stats"));
        setItem(49, item(Material.BARRIER,           ChatColor.RED          + "Close"),
                e -> e.getWhoClicked().closeInventory());
        setItem(50, item(Material.FURNACE,           ChatColor.GOLD         + "Forge"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("forge"));
        setItem(51, item(Material.FIRE_CHARGE,       ChatColor.RED          + "Kuudra"),
                e -> ((org.bukkit.entity.Player) e.getWhoClicked()).performCommand("kuudra"));
    }

    private static ItemStack item(Material material, String displayName) {
        return new ItemBuilder(material)
                .displayName(displayName)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
    }
}
