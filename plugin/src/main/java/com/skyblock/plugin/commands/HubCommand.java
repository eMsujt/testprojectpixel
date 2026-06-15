package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.AuctionHouseManager;
import com.skyblock.plugin.managers.BankManager;
import com.skyblock.core.manager.BazaarManager;
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
import com.skyblock.plugin.managers.SlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                case "bank"   -> new BankCommand().onCommand(sender, command, label, rest);
                case "mayor"  -> new MayorCommand().onCommand(sender, command, label, rest);
                case "kuudra" -> new KuudraCommand().onCommand(sender, command, label, rest);
                case "pets"   -> new PetsCommand().onCommand(sender, command, label, rest);
                default       -> player.sendMessage("Unknown sub-command. Try /hub bank, /hub mayor, /hub kuudra, or /hub pets.");
            }
            return true;
        }
        player.openInventory(buildMenu(player));
        return true;
    }

    private Inventory buildMenu(Player player) {
        UUID id = player.getUniqueId();

        // Snapshot all manager data once on the calling thread
        int gardenLevel       = GardenManager.getInstance().getGardenLevel(id);
        int gardenPlots       = GardenManager.getInstance().getGardenPlots(id);
        int gardenUnlocked    = GardenManager.getInstance().getUnlockedPlots(id);

        Map<String, Long> slayerKills = SlayerManager.getInstance().getKillCounts(id);

        int fishingXp              = FishingManager.getInstance().getFishingXp(id);
        Map<String, Long> fishCounts = FishingManager.getInstance().getFishCounts(id);

        int dungeonFloor    = DungeonManager.getInstance().getDungeonFloor(id);
        int dungeonHighest  = DungeonManager.getInstance().getHighestFloor(id);

        Map<String, Long>    skillXps    = SkillsManager.getInstance().getSkillXPs(id);
        Map<String, Integer> enchants    = EnchantingManager.getInstance().getEnchantLevels(id);

        int auctionListings = AuctionHouseManager.getInstance().getListings(id).size();

        int bazaarItems     = BazaarManager.getInstance().getBuyPrices().size();

        String activeProfile = ProfileManager.getInstance().getActiveProfile(id);

        PetsManager.Pet activePet = PetsManager.getInstance().getActivePet(id);

        int kuudraTier      = KuudraManager.getInstance().getKuudraTier(id);

        Map<String, Long> collections = CollectionsManager.getInstance().getCollectionCounts(id);

        boolean islandUnlocked = IslandManager.getInstance().isIslandUnlocked(id);
        int islandLevel        = IslandManager.getInstance().getIslandLevel(id);
        int islandVisitors     = IslandManager.getInstance().getVisitorCount(id);

        int hotmLevel       = HOTMManager.getInstance().getHotmLevel(id);

        double bankBalance   = BankManager.getInstance().getBalance(id);

        Inventory inv = Bukkit.createInventory(null, 54, "Hub");

        inv.setItem(0, makeItem(Material.GRASS_BLOCK, "Garden",
                "Level: " + gardenLevel,
                "Plots: " + gardenPlots,
                "Unlocked: " + gardenUnlocked));

        inv.setItem(1, makeItem(Material.IRON_SWORD, "Slayer",
                "Boss types: " + slayerKills.size(),
                "Total kills: " + slayerKills.values().stream().mapToLong(Long::longValue).sum()));

        inv.setItem(2, makeItem(Material.COD, "Fishing",
                "XP: " + fishingXp,
                "Types caught: " + fishCounts.size(),
                "Total caught: " + fishCounts.values().stream().mapToLong(Long::longValue).sum()));

        inv.setItem(3, makeItem(Material.ENDER_EYE, "Dungeons",
                "Current floor: " + dungeonFloor,
                "Highest floor: " + dungeonHighest));

        inv.setItem(4, makeItem(Material.BOOK, "Skills",
                "Skills tracked: " + skillXps.size(),
                "Total XP: " + skillXps.values().stream().mapToLong(Long::longValue).sum()));

        inv.setItem(5, makeItem(Material.ENCHANTING_TABLE, "Enchanting",
                "Enchants unlocked: " + enchants.size()));

        inv.setItem(6, makeItem(Material.GOLD_INGOT, "Auction House",
                "Active listings: " + auctionListings));

        inv.setItem(7, makeItem(Material.EMERALD, "Bazaar",
                "Items tracked: " + bazaarItems));

        inv.setItem(8, makeItem(Material.PLAYER_HEAD, "Profile",
                "Active: " + (activeProfile != null ? activeProfile : "None")));

        String petLine = activePet != null
                ? activePet.getName() + " (" + activePet.getRarity() + ") Lv" + activePet.getLevel()
                : "No active pet";
        inv.setItem(9, makeItem(Material.BONE, "Pets",
                "Active pet: " + petLine));

        inv.setItem(10, makeItem(Material.BLAZE_POWDER, "Kuudra",
                "Tier: " + kuudraTier));

        inv.setItem(11, makeItem(Material.CHEST, "Collections",
                "Collections: " + collections.size(),
                "Total items: " + collections.values().stream().mapToLong(Long::longValue).sum()));

        inv.setItem(12, makeItem(Material.OAK_SAPLING, "Island",
                "Unlocked: " + islandUnlocked,
                "Level: " + islandLevel,
                "Visitors: " + islandVisitors));

        inv.setItem(13, makeItem(Material.IRON_PICKAXE, "Heart of the Mountain",
                "HOTM Level: " + hotmLevel));

        inv.setItem(14, makeItem(Material.GOLD_NUGGET, "Bank",
                "Balance: " + String.format("%.2f", bankBalance)));

        return inv;
    }

    private ItemStack makeItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
