package com.skyblock.core;

import com.skyblock.core.accessory.AccessoryManager;
import com.skyblock.core.auction.AuctionHouseCommand;
import com.skyblock.core.auction.AuctionHouseManager;
import com.skyblock.core.auction.AuctionManager;
import com.skyblock.core.kuudra.KuudraCommand;
import com.skyblock.core.bank.BankManager;
import com.skyblock.core.bazaar.BazaarCommand;
import com.skyblock.core.bazaar.BazaarManager;
import com.skyblock.core.dungeon.DungeonManager;
import com.skyblock.core.enchanting.EnchantingCommand;
import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.garden.GardenCommand;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.guild.GuildManager;
import com.skyblock.core.hotm.HOTMCommand;
import com.skyblock.core.hotm.HOTMManager;
import com.skyblock.core.island.IslandCommand;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.mayor.MayorCommand;
import com.skyblock.core.mayor.MayorManager;
import com.skyblock.core.party.PartyManager;
import com.skyblock.core.pets.PetsManager;
import com.skyblock.core.profile.ProfileCommand;
import com.skyblock.core.profile.ProfileManager;
import com.skyblock.core.reforge.ReforgeManager;
import com.skyblock.core.skills.SkillsManager;
import com.skyblock.core.slayer.SlayerManager;
import com.skyblock.core.stats.StatsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyblockPlugin extends JavaPlugin {

    private static SkyblockPlugin instance;

    public static SkyblockPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        BankManager bankManager = BankManager.getInstance();
        bankManager.load(getDataFolder());
        MayorManager mayorManager = MayorManager.getInstance();
        mayorManager.load(getDataFolder());
        MayorCommand mayorCommand = new MayorCommand(mayorManager);
        getCommand("mayor").setExecutor(mayorCommand);
        getCommand("mayor").setTabCompleter(mayorCommand);
        AuctionManager.getInstance();
        AuctionHouseManager auctionHouseManager = AuctionHouseManager.getInstance();
        auctionHouseManager.load(getDataFolder());
        AuctionHouseCommand auctionHouseCommand = new AuctionHouseCommand(auctionHouseManager);
        getCommand("auctionhouse").setExecutor(auctionHouseCommand);
        getCommand("auctionhouse").setTabCompleter(auctionHouseCommand);
        BazaarManager bazaarManager = BazaarManager.getInstance();
        bazaarManager.load(getDataFolder());
        BazaarCommand bazaarCommand = new BazaarCommand(bazaarManager);
        getCommand("bazaar").setExecutor(bazaarCommand);
        getCommand("bazaar").setTabCompleter(bazaarCommand);
        DungeonManager dungeonManager = DungeonManager.getInstance();
        dungeonManager.load(getDataFolder());
        IslandManager islandManager = IslandManager.getInstance();
        IslandCommand islandCommand = new IslandCommand(islandManager);
        getCommand("island").setExecutor(islandCommand);
        getCommand("island").setTabCompleter(islandCommand);
        GuildManager.getInstance();
        PartyManager.getInstance();
        GardenManager gardenManager = GardenManager.getInstance();
        gardenManager.load(getDataFolder());
        GardenCommand gardenCommand = new GardenCommand(gardenManager);
        getCommand("garden").setExecutor(gardenCommand);
        getCommand("garden").setTabCompleter(gardenCommand);
        SlayerManager slayerManager = SlayerManager.getInstance();
        slayerManager.load(getDataFolder());
        FishingManager.getInstance();
        HOTMManager hotmManager = HOTMManager.getInstance();
        hotmManager.load(getDataFolder());
        HOTMCommand hotmCommand = new HOTMCommand(hotmManager);
        getCommand("hotmtree").setExecutor(hotmCommand);
        getCommand("hotmtree").setTabCompleter(hotmCommand);
        PetsManager petsManager = PetsManager.getInstance();
        petsManager.load(getDataFolder());
        KuudraManager kuudraManager = KuudraManager.getInstance();
        kuudraManager.load(getDataFolder());
        KuudraCommand kuudraCommand = new KuudraCommand(kuudraManager);
        getCommand("kuudra").setExecutor(kuudraCommand);
        getCommand("kuudra").setTabCompleter(kuudraCommand);
        EnchantingManager enchantingManager = EnchantingManager.getInstance();
        enchantingManager.load(getDataFolder());
        EnchantingCommand enchantingCommand = new EnchantingCommand(enchantingManager);
        getCommand("enchanting").setExecutor(enchantingCommand);
        getCommand("enchanting").setTabCompleter(enchantingCommand);
        ReforgeManager.getInstance();
        SkillsManager skillsManager = SkillsManager.getInstance();
        skillsManager.load(getDataFolder());
        AccessoryManager.getInstance();
        StatsCommand statsCommand = new StatsCommand(skillsManager, slayerManager);
        getCommand("stats").setExecutor(statsCommand);
        getCommand("stats").setTabCompleter(statsCommand);
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.load(getDataFolder());
        ProfileCommand profileCommand = new ProfileCommand(profileManager);
        getCommand("profile").setExecutor(profileCommand);
        getCommand("profile").setTabCompleter(profileCommand);
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        DungeonManager.getInstance().save(getDataFolder());
        SlayerManager.getInstance().save(getDataFolder());
        PetsManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        HOTMManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        SkillsManager.getInstance().save(getDataFolder());
        ProfileManager.getInstance().save(getDataFolder());
        instance = null;
    }
}
