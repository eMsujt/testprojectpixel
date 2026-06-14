package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.AuctionHouseManager;
import com.skyblock.plugin.managers.BankManager;
import com.skyblock.plugin.managers.BazaarManager;
import com.skyblock.plugin.managers.CollectionsManager;
import com.skyblock.plugin.managers.DungeonManager;
import com.skyblock.plugin.managers.EnchantingManager;
import com.skyblock.plugin.managers.FishingManager;
import com.skyblock.plugin.managers.GardenManager;
import com.skyblock.plugin.managers.HOTMManager;
import com.skyblock.plugin.managers.IslandManager;
import com.skyblock.plugin.managers.KuudraManager;
import com.skyblock.plugin.managers.PetsManager;
import com.skyblock.plugin.managers.ProfileManager;
import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class HubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        player.openInventory(buildMenu(player));
        return true;
    }

    private Inventory buildMenu(Player player) {
        UUID id = player.getUniqueId();
        Inventory inv = Bukkit.createInventory(null, 54, "Hub");

        GardenManager garden = GardenManager.getInstance();
        inv.setItem(0, makeItem(Material.GRASS_BLOCK, "Garden",
                ChatColor.GRAY + "Level: " + ChatColor.GREEN + garden.getGardenLevel(id),
                ChatColor.GRAY + "Plots: " + ChatColor.YELLOW + garden.getGardenPlots(id),
                ChatColor.GRAY + "Unlocked: " + ChatColor.YELLOW + garden.getUnlockedPlots(id)));

        inv.setItem(1, makeItem(Material.IRON_SWORD, "Slayer",
                ChatColor.GRAY + "Track your slayer quests",
                ChatColor.GRAY + "Defeat bosses for XP",
                ChatColor.GRAY + "Unlock slayer perks"));

        FishingManager fishing = FishingManager.getInstance();
        long totalFish = fishing.getFishCounts(id).values().stream().mapToLong(Long::longValue).sum();
        inv.setItem(2, makeItem(Material.COD, "Fishing",
                ChatColor.GRAY + "Fishing XP: " + ChatColor.AQUA + fishing.getFishingXp(id),
                ChatColor.GRAY + "Total Fish: " + ChatColor.AQUA + totalFish,
                ChatColor.GRAY + "Types caught: " + ChatColor.AQUA + fishing.getFishCounts(id).size()));

        DungeonManager dungeons = DungeonManager.getInstance();
        int totalRuns = dungeons.getFloorCompletions(id).values().stream().mapToInt(Integer::intValue).sum();
        inv.setItem(3, makeItem(Material.ENDER_EYE, "Dungeons",
                ChatColor.GRAY + "Floor: " + ChatColor.LIGHT_PURPLE + dungeons.getDungeonFloor(id),
                ChatColor.GRAY + "Highest Floor: " + ChatColor.LIGHT_PURPLE + dungeons.getHighestFloor(id),
                ChatColor.GRAY + "Total Runs: " + ChatColor.LIGHT_PURPLE + totalRuns));

        SkillsManager skills = SkillsManager.getInstance();
        Map<String, Long> skillXPs = skills.getSkillXPs(id);
        long totalXP = skillXPs.values().stream().mapToLong(Long::longValue).sum();
        long activeSkills = skillXPs.entrySet().stream().filter(e -> e.getValue() > 0).count();
        inv.setItem(4, makeItem(Material.BOOK, "Skills",
                ChatColor.GRAY + "Total XP: " + ChatColor.YELLOW + totalXP,
                ChatColor.GRAY + "Active Skills: " + ChatColor.YELLOW + activeSkills + "/8",
                ChatColor.GRAY + "Level combat, farming, and more"));

        EnchantingManager enchanting = EnchantingManager.getInstance();
        int enchantCount = enchanting.getEnchantLevels(id).size();
        inv.setItem(5, makeItem(Material.ENCHANTING_TABLE, "Enchanting",
                ChatColor.GRAY + "Enchants known: " + ChatColor.BLUE + enchantCount,
                ChatColor.GRAY + "Enchant your gear",
                ChatColor.GRAY + "Unlock higher tiers"));

        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        int listings = ah.getListings(id).size();
        inv.setItem(6, makeItem(Material.GOLD_INGOT, "Auction House",
                ChatColor.GRAY + "Active listings: " + ChatColor.GOLD + listings,
                ChatColor.GRAY + "Browse and bid on items",
                ChatColor.GRAY + "Create your own auctions"));

        BazaarManager bazaar = BazaarManager.getInstance();
        int bazaarItems = bazaar.getBuyPrices().size();
        inv.setItem(7, makeItem(Material.EMERALD, "Bazaar",
                ChatColor.GRAY + "Items tracked: " + ChatColor.GREEN + bazaarItems,
                ChatColor.GRAY + "Instant buy and sell",
                ChatColor.GRAY + "Set buy and sell orders"));

        ProfileManager profile = ProfileManager.getInstance();
        inv.setItem(8, makeItem(Material.PLAYER_HEAD, "Profile",
                ChatColor.GRAY + "Player: " + ChatColor.WHITE + player.getName(),
                ChatColor.GRAY + "Active: " + ChatColor.WHITE + profile.getActiveProfile(id),
                ChatColor.GRAY + "View your full stats"));

        PetsManager pets = PetsManager.getInstance();
        PetsManager.Pet activePet = pets.getActivePet(id);
        String petLine = activePet != null
                ? ChatColor.GRAY + "Active: " + ChatColor.GREEN + activePet.getName() + " (Lv" + activePet.getLevel() + ")"
                : ChatColor.GRAY + "No active pet";
        inv.setItem(9, makeItem(Material.BONE, "Pets",
                petLine,
                ChatColor.GRAY + "Collect and level pets",
                ChatColor.GRAY + "Unlock pet abilities"));

        KuudraManager kuudra = KuudraManager.getInstance();
        inv.setItem(10, makeItem(Material.BLAZE_POWDER, "Kuudra",
                ChatColor.GRAY + "Tier: " + ChatColor.RED + kuudra.getKuudraTier(id),
                ChatColor.GRAY + "Face Kuudra in battle",
                ChatColor.GRAY + "Earn crimson rewards"));

        CollectionsManager collections = CollectionsManager.getInstance();
        Map<String, Long> collMap = collections.getCollectionCounts(id);
        long totalCollected = collMap.values().stream().mapToLong(Long::longValue).sum();
        inv.setItem(11, makeItem(Material.CHEST, "Collections",
                ChatColor.GRAY + "Items collected: " + ChatColor.GOLD + totalCollected,
                ChatColor.GRAY + "Types tracked: " + ChatColor.GOLD + collMap.size(),
                ChatColor.GRAY + "Unlock collection rewards"));

        IslandManager island = IslandManager.getInstance();
        inv.setItem(12, makeItem(Material.OAK_SAPLING, "Island",
                ChatColor.GRAY + "Level: " + ChatColor.GREEN + island.getIslandLevel(id),
                ChatColor.GRAY + "Visitors: " + ChatColor.GREEN + island.getVisitorCount(id),
                ChatColor.GRAY + "Unlocked: " + ChatColor.GREEN + (island.isIslandUnlocked(id) ? "Yes" : "No")));

        HOTMManager hotm = HOTMManager.getInstance();
        inv.setItem(13, makeItem(Material.IRON_PICKAXE, "Heart of the Mountain",
                ChatColor.GRAY + "HOTM Level: " + ChatColor.DARK_AQUA + hotm.getHotmLevel(id),
                ChatColor.GRAY + "Unlock mining perks",
                ChatColor.GRAY + "Upgrade your pickaxe"));

        BankManager bank = BankManager.getInstance();
        inv.setItem(14, makeItem(Material.GOLD_NUGGET, "Bank",
                ChatColor.GRAY + "Balance: " + ChatColor.GOLD + String.format("%.1f", bank.getBalance(id)),
                ChatColor.GRAY + "Deposit and withdraw coins",
                ChatColor.GRAY + "Keep your coins safe"));

        return inv;
    }

    private ItemStack makeItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
