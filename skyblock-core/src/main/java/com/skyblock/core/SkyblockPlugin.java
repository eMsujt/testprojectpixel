package com.skyblock.core;

import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.booster.BoosterManager;
import com.skyblock.core.command.CommandRegistry;
import com.skyblock.core.coop.CoopManager;
import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.dungeon.manager.RunManager;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.forge.ForgeManager;
import com.skyblock.core.friend.FriendManager;
import com.skyblock.core.guild.GuildManager;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.mail.MailManager;
import com.skyblock.core.mailbox.MailboxManager;
import com.skyblock.core.manager.AccessoryManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.HotmManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.QuestManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.manager.ReputationManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.title.TitleManager;
import com.skyblock.core.trade.TradeListener;
import com.skyblock.core.trade.TradeManager;
import com.skyblock.core.vault.VaultManager;
import com.skyblock.core.warp.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyblockPlugin extends JavaPlugin {

    private static SkyblockPlugin instance;
    private BankManager bankManager;
    private MayorManager mayorManager;

    public static SkyblockPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        bankManager = BankManager.getInstance();
        bankManager.load(getDataFolder());
        mayorManager = MayorManager.getInstance();
        mayorManager.load(getDataFolder());
        AuctionHouseManager.getInstance().load(getDataFolder());
        BazaarManager.getInstance().load(getDataFolder());
        DungeonManager.getInstance().load(getDataFolder());
        GuildManager.getInstance().load(getDataFolder());
        GardenManager.getInstance().load(getDataFolder());
        SlayerManager.getInstance().load(getDataFolder());
        FishingManager.getInstance().load(getDataFolder());
        HotmManager.getInstance().load(getDataFolder());
        KuudraManager.getInstance().load(getDataFolder());
        EnchantingManager.getInstance().load(getDataFolder());
        ReforgeManager.getInstance().load(getDataFolder());
        SkillManager.getInstance().load(getDataFolder());
        AccessoryManager.getInstance();
        ProfileManager.getInstance().load(getDataFolder());
        MinionManager.getInstance().load(getDataFolder());
        CraftingManager.getInstance().load(getDataFolder());
        QuestManager.getInstance().load(getDataFolder());
        TradeManager tradeManager = TradeManager.getInstance();
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);
        BackpackManager.getInstance().load(getDataFolder());
        ForagingManager.getInstance().load(getDataFolder());
        CollectionManager.getInstance().load(getDataFolder());
        MailManager.getInstance().load(getDataFolder());
        CoopManager.getInstance().load(getDataFolder());
        ReputationManager.getInstance().load(getDataFolder());
        VaultManager.getInstance().load(getDataFolder());
        FriendManager.getInstance().load(getDataFolder());
        BoosterManager.getInstance().load(getDataFolder());
        MailboxManager.getInstance().load(getDataFolder());
        WarpManager.getInstance().load(getDataFolder());
        TitleManager.getInstance().load(getDataFolder());
        RunManager.getInstance().load(getDataFolder());
        AlchemyManager.getInstance().load(getDataFolder());
        CommandRegistry.registerAll(this);
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        DungeonManager.getInstance().save(getDataFolder());
        SlayerManager.getInstance().save(getDataFolder());
        FishingManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        HotmManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        ReforgeManager.getInstance().save(getDataFolder());
        MinionManager.getInstance().save(getDataFolder());
        SkillManager.getInstance().save(getDataFolder());
        ProfileManager.getInstance().save(getDataFolder());
        CraftingManager.getInstance().save(getDataFolder());
        QuestManager.getInstance().save(getDataFolder());
        BackpackManager.getInstance().save(getDataFolder());
        ForagingManager.getInstance().save(getDataFolder());
        CollectionManager.getInstance().save(getDataFolder());
        MailManager.getInstance().save(getDataFolder());
        CoopManager.getInstance().save(getDataFolder());
        ReputationManager.getInstance().save(getDataFolder());
        VaultManager.getInstance().save(getDataFolder());
        GuildManager.getInstance().save(getDataFolder());
        FriendManager.getInstance().save(getDataFolder());
        BoosterManager.getInstance().save(getDataFolder());
        MailboxManager.getInstance().save(getDataFolder());
        TitleManager.getInstance().save(getDataFolder());
        RunManager.getInstance().save(getDataFolder());
        AlchemyManager.getInstance().save(getDataFolder());
        try {
            WarpManager.getInstance().save(getDataFolder());
        } catch (java.io.IOException e) {
            getLogger().severe("Failed to save warp data: " + e.getMessage());
        }
        instance = null;
    }
}
