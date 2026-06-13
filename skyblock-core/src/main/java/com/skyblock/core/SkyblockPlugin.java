package com.skyblock.core;

import com.skyblock.core.accessory.AccessoryManager;
import com.skyblock.core.auction.AuctionHouseManager;
import com.skyblock.core.auction.AuctionManager;
import com.skyblock.core.bank.BankManager;
import com.skyblock.core.bazaar.BazaarManager;
import com.skyblock.core.dungeon.DungeonManager;
import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.hotm.HOTMManager;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.mayor.MayorManager;
import com.skyblock.core.pets.PetsManager;
import com.skyblock.core.profile.ProfileManager;
import com.skyblock.core.reforge.ReforgeManager;
import com.skyblock.core.skills.SkillsManager;
import com.skyblock.core.slayer.SlayerManager;
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
        MayorManager.getInstance();
        AuctionManager.getInstance();
        AuctionHouseManager auctionHouseManager = AuctionHouseManager.getInstance();
        auctionHouseManager.load(getDataFolder());
        BazaarManager bazaarManager = BazaarManager.getInstance();
        bazaarManager.load(getDataFolder());
        DungeonManager dungeonManager = DungeonManager.getInstance();
        dungeonManager.load(getDataFolder());
        IslandManager.getInstance();
        GardenManager gardenManager = GardenManager.getInstance();
        gardenManager.load(getDataFolder());
        SlayerManager slayerManager = SlayerManager.getInstance();
        slayerManager.load(getDataFolder());
        FishingManager.getInstance();
        HOTMManager hotmManager = HOTMManager.getInstance();
        hotmManager.load(getDataFolder());
        PetsManager petsManager = PetsManager.getInstance();
        petsManager.load(getDataFolder());
        KuudraManager kuudraManager = KuudraManager.getInstance();
        kuudraManager.load(getDataFolder());
        EnchantingManager enchantingManager = EnchantingManager.getInstance();
        enchantingManager.load(getDataFolder());
        ReforgeManager.getInstance();
        SkillsManager.getInstance();
        AccessoryManager.getInstance();
        SkillsManager.getInstance();
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.load(getDataFolder());
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        DungeonManager.getInstance().save(getDataFolder());
        SlayerManager.getInstance().save(getDataFolder());
        PetsManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        HOTMManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        ProfileManager.getInstance().save(getDataFolder());
        instance = null;
    }
}
