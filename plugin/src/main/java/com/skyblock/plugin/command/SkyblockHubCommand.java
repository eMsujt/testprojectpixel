package com.skyblock.plugin.command;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.fairy.FairyManager;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.hotm.HOTMManager;
import com.skyblock.core.island.manager.IslandManager;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.mayor.MayorManager;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.ProfileManager;
import com.skyblock.core.manager.ProfileManager.SkyBlockProfile;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.SkillManager.SkillType;
import com.skyblock.core.slayer.SlayerManager;
import com.skyblock.core.warp.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
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
                case "garden":
                    handleGarden(player);
                    return true;
                case "slayer":
                    handleSlayer(player);
                    return true;
                case "pets":
                    handlePets(player);
                    return true;
                case "skills":
                    handleSkills(player);
                    return true;
                case "collections":
                    handleCollections(player);
                    return true;
                case "hotm":
                    handleHotm(player);
                    return true;
                case "profile":
                    handleProfile(player);
                    return true;
                case "enchanting":
                    handleEnchanting(player);
                    return true;
                case "bazaar":
                    handleBazaar(player);
                    return true;
                case "auctionhouse":
                    handleAuctionHouse(player);
                    return true;
                case "island":
                    handleIsland(player);
                    return true;
                case "dungeon":
                    handleDungeon(player);
                    return true;
                case "fairy":
                    handleFairy(player);
                    return true;
                case "kuudra":
                    handleKuudra(player);
                    return true;
                case "fishing":
                    handleFishing(player);
                    return true;
                case "alchemy":
                    handleAlchemy(player);
                    return true;
                case "hub":
                    handleStatusPanel(player);
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

    private void handleStatusPanel(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("§8§m--------------------§r §6SkyBlock Status §8§m--------------------");

        // Economy
        BankManager bank = BankManager.getInstance();
        BazaarManager bazaar = BazaarManager.getInstance();
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        player.sendMessage("§e[Economy] §fBank: §a" + (long) bank.getBalance(id)
                + " §7| Bazaar orders: §a" + bazaar.getOrderCount(id)
                + " §7| AH listings: §a" + ah.getAuctionCount(id));

        // Progression
        ProfileManager profiles = ProfileManager.getInstance();
        FairyManager fairy = FairyManager.getInstance();
        HOTMManager hotm = HOTMManager.getInstance();
        int profileCount = profiles.getProfilesForOwner(id).size();
        player.sendMessage("§e[Progression] §fProfiles: §a" + profileCount
                + " §7| Fairy souls: §a" + fairy.getSouls(id) + "§7/§a" + FairyManager.MAX_SOULS
                + " §7| Mithril powder: §a" + hotm.getMithrilPowder(id));

        // Combat
        SlayerManager slayer = SlayerManager.getInstance();
        DungeonManager dungeon = DungeonManager.getInstance();
        KuudraManager kuudra = KuudraManager.getInstance();
        int slayerLevels = 0;
        for (SlayerManager.SlayerType t : SlayerManager.SlayerType.values()) {
            slayerLevels += slayer.getLevel(id, t);
        }
        DungeonManager.DungeonClass dungeonClass = dungeon.getClass(id);
        int kuudraClears = 0;
        for (KuudraManager.KuudraTier t : KuudraManager.KuudraTier.values()) {
            kuudraClears += kuudra.getCompletionCount(id, t);
        }
        player.sendMessage("§e[Combat] §fSlayer levels: §a" + slayerLevels
                + " §7| Dungeon class: §a" + (dungeonClass != null ? dungeonClass.getDisplayName() : "None")
                + " §7| Kuudra clears: §a" + kuudraClears);

        // Activities
        SkillManager skills = SkillManager.getInstance();
        FishingManager fishing = FishingManager.getInstance();
        AlchemyManager alchemy = AlchemyManager.getInstance();
        GardenManager garden = GardenManager.getInstance();
        int avgSkillLevel = 0;
        SkillType[] skillTypes = SkillType.values();
        for (SkillManager.SkillType s : skillTypes) {
            avgSkillLevel += skills.getLevel(id, s);
        }
        if (skillTypes.length > 0) avgSkillLevel /= skillTypes.length;
        player.sendMessage("§e[Activities] §fAvg skill level: §a" + avgSkillLevel
                + " §7| Fish caught: §a" + fishing.getTotalFishCaught(id)
                + " §7| Alchemy level: §a" + alchemy.getLevel(id)
                + " §7| Garden level: §a" + garden.getPlotLevel(id));

        // Personal
        PetManager pets = PetManager.getInstance();
        IslandManager islands = IslandManager.getInstance();
        MayorManager mayor = MayorManager.getInstance();
        EnchantingManager enchanting = EnchantingManager.getInstance();
        PetManager.Pet activePet = pets.getActivePet(id);
        String petName = activePet != null ? activePet.type.name() + " [" + activePet.rarity.name() + "]" : "None";
        String islandWarp = islands.getIsland(id).map(i -> i.getWarpName() != null ? i.getWarpName() : "None").orElse("No island");
        MayorManager.MayorCandidate currentMayor = mayor.getCurrentMayor();
        player.sendMessage("§e[Personal] §fPet: §a" + petName
                + " §7| Island: §a" + islandWarp
                + " §7| Mayor: §a" + (currentMayor != null ? currentMayor.getDisplayName() : "None")
                + " §7| Enchants: §a" + enchanting.getEnchantments(id).size());

        player.sendMessage("§8§m-------------------------------------------------------");
    }

    private void handleBank(Player player) {
        UUID id = player.getUniqueId();
        BankManager manager = BankManager.getInstance();
        double balance = manager.getBalance(id);
        BankTier tier = manager.getTier(id);
        BankType type = manager.getBankType(id);
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

    private void handleGarden(Player player) {
        UUID id = player.getUniqueId();
        GardenManager manager = GardenManager.getInstance();
        int level = manager.getPlotLevel(id);
        player.sendMessage("=== Garden ===");
        player.sendMessage("Garden Level: " + level);
        java.util.Set<GardenManager.GardenPlot> plots = manager.getUnlockedPlots(id);
        player.sendMessage("Unlocked Plots: " + plots.size());
    }

    private void handleSlayer(Player player) {
        UUID id = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        player.sendMessage("=== Your Slayers ===");
        for (SlayerManager.SlayerType type : SlayerManager.SlayerType.values()) {
            int level = manager.getLevel(id, type);
            long xp = manager.getExperience(id, type);
            int kills = manager.getKillCount(id, type);
            player.sendMessage("  " + type.getDisplayName() + ": Level " + level + " (" + xp + " XP, " + kills + " kills)");
        }
        SlayerManager.SlayerQuest quest = manager.getActiveQuest(id);
        if (quest != null) {
            player.sendMessage("Active Quest: " + quest.type.getDisplayName() + " T" + (quest.tier.ordinal() + 1) + " — " + quest.getKills() + " kills");
        }
    }

    private void handlePets(Player player) {
        UUID id = player.getUniqueId();
        PetManager manager = PetManager.getInstance();
        PetManager.Pet active = manager.getActivePet(id);
        if (active != null) {
            player.sendMessage("Active pet: " + active.type.name() + " [" + active.rarity.name() + "]");
        } else {
            player.sendMessage("Active pet: None");
        }
        List<PetManager.Pet> pets = manager.getPets(id);
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
            return;
        }
        player.sendMessage("=== Your Pets ===");
        for (PetManager.Pet pet : pets) {
            String marker = (active != null && pet.id.equals(active.id)) ? " *" : "";
            player.sendMessage("  " + pet.type.name() + " [" + pet.rarity.name() + "]" + marker);
        }
    }

    private void handleSkills(Player player) {
        UUID id = player.getUniqueId();
        SkillManager manager = SkillManager.getInstance();
        player.sendMessage("=== Your Skills ===");
        for (SkillType skill : SkillType.values()) {
            int level = manager.getLevel(id, skill);
            double xp = manager.getXp(id, skill);
            player.sendMessage("  " + skill.getDisplayName() + ": Level " + level + " (" + (long) xp + " XP)");
        }
    }

    private void handleCollections(Player player) {
        UUID id = player.getUniqueId();
        CollectionManager manager = CollectionManager.getInstance();
        player.sendMessage("=== Your Collections ===");
        for (Collection type : Collection.values()) {
            long amount = manager.getItems(id, type);
            int tier = manager.getTier(id, type);
            player.sendMessage("  " + type.getDisplayName() + ": " + amount + " (Tier " + tier + ")");
        }
    }

    private void handleHotm(Player player) {
        UUID id = player.getUniqueId();
        HOTMManager manager = HOTMManager.getInstance();
        player.sendMessage("=== Heart of the Mountain ===");
        player.sendMessage("Mithril Powder: " + manager.getMithrilPowder(id));
        player.sendMessage("Gemstone Powder: " + manager.getGemstonePowder(id));
        for (HOTMManager.HOTMPerk perk : HOTMManager.HOTMPerk.values()) {
            int level = manager.getLevel(id, perk);
            if (level > 0) {
                player.sendMessage("  " + perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
            }
        }
    }

    private void handleProfile(Player player) {
        UUID id = player.getUniqueId();
        ProfileManager manager = ProfileManager.getInstance();
        player.sendMessage("=== Your Profiles ===");
        List<SkyBlockProfile> profiles = manager.getProfilesForOwner(id);
        if (profiles.isEmpty()) {
            player.sendMessage("No profiles found.");
            return;
        }
        for (SkyBlockProfile profile : profiles) {
            player.sendMessage("  " + profile.name() + " [" + profile.gameMode().getDisplayName() + "]");
        }
    }

    private void handleEnchanting(Player player) {
        UUID id = player.getUniqueId();
        EnchantingManager manager = EnchantingManager.getInstance();
        player.sendMessage("=== Your Enchantments ===");
        Map<EnchantingManager.SkyBlockEnchantment, Integer> enchants = manager.getEnchantments(id);
        if (enchants.isEmpty()) {
            player.sendMessage("No enchantments applied.");
            return;
        }
        for (Map.Entry<EnchantingManager.SkyBlockEnchantment, Integer> entry : enchants.entrySet()) {
            player.sendMessage("  " + entry.getKey().getDisplayName() + ": " + entry.getValue());
        }
    }

    private void handleBazaar(Player player) {
        UUID id = player.getUniqueId();
        BazaarManager manager = BazaarManager.getInstance();
        player.sendMessage("=== Bazaar ===");
        player.sendMessage("Active Orders: " + manager.getOrderCount(id));
    }

    private void handleAuctionHouse(Player player) {
        UUID id = player.getUniqueId();
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        player.sendMessage("=== Auction House ===");
        player.sendMessage("Active Listings: " + manager.getAuctionCount(id));
    }

    private void handleIsland(Player player) {
        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        player.sendMessage("=== Your Island ===");
        manager.getIsland(id).ifPresentOrElse(island -> {
            player.sendMessage("Members: " + island.getMembers().size());
            player.sendMessage("Warp: " + (island.getWarpName() != null ? island.getWarpName() : "None"));
            for (IslandManager.IslandUpgrade upgrade : IslandManager.IslandUpgrade.values()) {
                int level = island.getUpgradeLevel(upgrade);
                if (level > 0) {
                    player.sendMessage("  " + upgrade.getDisplayName() + ": " + level);
                }
            }
        }, () -> player.sendMessage("You do not have an island."));
    }

    private void handleDungeon(Player player) {
        UUID id = player.getUniqueId();
        DungeonManager manager = DungeonManager.getInstance();
        player.sendMessage("=== Dungeons ===");
        DungeonManager.DungeonClass dungeonClass = manager.getClass(id);
        player.sendMessage("Class: " + (dungeonClass != null ? dungeonClass.getDisplayName() : "None"));
        for (DungeonManager.DungeonFloor floor : DungeonManager.DungeonFloor.values()) {
            int completions = manager.getFloorCompletionCount(id, floor);
            if (completions > 0) {
                player.sendMessage("  " + floor.getDisplayName() + ": " + completions + " clears");
            }
        }
    }

    private void handleFairy(Player player) {
        UUID id = player.getUniqueId();
        FairyManager manager = FairyManager.getInstance();
        int souls = manager.getSouls(id);
        player.sendMessage("=== Fairy Souls ===");
        player.sendMessage("Souls: " + souls + "/" + FairyManager.MAX_SOULS);
    }

    private void handleFishing(Player player) {
        UUID id = player.getUniqueId();
        FishingManager manager = FishingManager.getInstance();
        player.sendMessage("=== Fishing ===");
        player.sendMessage("Level: " + manager.getLevel(id));
        player.sendMessage("XP: " + (long) manager.getXp(id));
        player.sendMessage("Fish Caught: " + manager.getTotalFishCaught(id));
    }

    private void handleAlchemy(Player player) {
        UUID id = player.getUniqueId();
        AlchemyManager manager = AlchemyManager.getInstance();
        player.sendMessage("=== Alchemy ===");
        player.sendMessage("Level: " + manager.getLevel(id));
        player.sendMessage("XP: " + (long) manager.getXp(id));
        AlchemyManager.BrewJob job = manager.getActiveJob(id);
        if (job != null) {
            player.sendMessage("Active Brew: " + job.getRecipe().getId());
        }
    }

    private void handleKuudra(Player player) {
        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();
        player.sendMessage("=== Kuudra ===");
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            int count = manager.getCompletionCount(id, tier);
            if (count > 0) {
                player.sendMessage("  " + tier.getDisplayName() + ": " + count + " clears");
            }
        }
        KuudraManager.KuudraRun activeRun = manager.getActiveRun(id);
        if (activeRun != null) {
            player.sendMessage("Active Run: " + activeRun.getTier().getDisplayName());
        }
    }
}
