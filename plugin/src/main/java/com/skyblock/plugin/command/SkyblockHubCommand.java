package com.skyblock.plugin.command;

import com.skyblock.core.bank.BankManager;
import com.skyblock.core.collections.CollectionsManager;
import com.skyblock.core.fairy.FairyManager;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.mayor.MayorManager;
import com.skyblock.core.pets.PetsManager;
import com.skyblock.core.skills.SkillsManager;
import com.skyblock.core.slayer.SlayerManager;
import com.skyblock.core.warp.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
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
                case "island":
                    handleIsland(player);
                    return true;
                case "fairy":
                    handleFairy(player);
                    return true;
                case "kuudra":
                    handleKuudra(player);
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

    private void handleBank(Player player) {
        UUID id = player.getUniqueId();
        BankManager manager = BankManager.getInstance();
        double balance = manager.getBalance(id);
        BankManager.BankTier tier = manager.getTier(id);
        BankManager.BankType type = manager.getBankType(id);
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
        PetsManager manager = PetsManager.getInstance();
        PetsManager.Pet active = manager.getActivePet(id);
        if (active != null) {
            player.sendMessage("Active pet: " + active.type.name() + " [" + active.rarity.name() + "]");
        } else {
            player.sendMessage("Active pet: None");
        }
        List<PetsManager.Pet> pets = manager.getPets(id);
        if (pets.isEmpty()) {
            player.sendMessage("You have no pets.");
            return;
        }
        player.sendMessage("=== Your Pets ===");
        for (PetsManager.Pet pet : pets) {
            String marker = (active != null && pet.id.equals(active.id)) ? " *" : "";
            player.sendMessage("  " + pet.type.name() + " [" + pet.rarity.name() + "]" + marker);
        }
    }

    private void handleSkills(Player player) {
        UUID id = player.getUniqueId();
        SkillsManager manager = SkillsManager.getInstance();
        player.sendMessage("=== Your Skills ===");
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            int level = manager.getLevel(id, skill);
            double xp = manager.getXp(id, skill);
            player.sendMessage("  " + skill.getDisplayName() + ": Level " + level + " (" + (long) xp + " XP)");
        }
    }

    private void handleCollections(Player player) {
        UUID id = player.getUniqueId();
        CollectionsManager manager = CollectionsManager.getInstance();
        player.sendMessage("=== Your Collections ===");
        for (CollectionsManager.CollectionType type : CollectionsManager.CollectionType.values()) {
            long amount = manager.getItems(id, type);
            int tier = manager.getTier(id, type);
            player.sendMessage("  " + type.getDisplayName() + ": " + amount + " (Tier " + tier + ")");
        }
    }

    private void handleIsland(Player player) {
        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        java.util.Optional<IslandManager.SkyBlockIsland> opt = manager.getIsland(id);
        if (opt.isEmpty()) {
            player.sendMessage("You do not have an island. Use /island create.");
            return;
        }
        IslandManager.IslandData data = manager.getOrCreateIslandData(id);
        player.sendMessage("=== Your Island ===");
        player.sendMessage("Level: " + data.level());
        player.sendMessage("Blocks Placed: " + data.blocksPlaced());
        String warp = manager.getWarpName(id);
        player.sendMessage("Warp: " + (warp != null ? warp : "None"));
    }

    private void handleFairy(Player player) {
        UUID id = player.getUniqueId();
        FairyManager manager = FairyManager.getInstance();
        int count = manager.getCount(id);
        player.sendMessage("=== Fairy Souls ===");
        player.sendMessage("Collected: " + count + " / " + FairyManager.MAX_SOULS);
    }

    private void handleKuudra(Player player) {
        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();
        player.sendMessage("=== Your Kuudra Completions ===");
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            int count = manager.getCompletionCount(id, tier);
            player.sendMessage("  " + tier.getDisplayName() + ": " + count + " completions");
        }
        KuudraManager.KuudraRun run = manager.getActiveRun(id);
        if (run != null) {
            player.sendMessage("Active Run: " + run.getTier().getDisplayName() + " tier");
        }
    }
}
