package com.skyblock.core.listener;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.ScoreboardManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import com.skyblock.core.manager.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public final class CompactListeners implements Listener {

    private static final CompactListeners INSTANCE = new CompactListeners();

    private static final long TAMING_KILL_XP     = 10L;
    private static final long CARPENTRY_XP_PLANK =  1L;
    private static final long FARMING_CROP_XP    =  3L;
    private static final long FORAGING_LOG_XP    =  6L;
    private static final long PET_XP_PER_KILL    =  5L;

    // All farmable materials; Ageable ones require max age to award XP.
    private static final Set<Material> CROP_MATERIALS = EnumSet.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.NETHER_WART,
            Material.COCOA,
            Material.MELON,
            Material.PUMPKIN,
            Material.SUGAR_CANE
    );

    private static final Set<Material> LOGS = EnumSet.of(
            Material.OAK_LOG,         Material.STRIPPED_OAK_LOG,
            Material.BIRCH_LOG,       Material.STRIPPED_BIRCH_LOG,
            Material.SPRUCE_LOG,      Material.STRIPPED_SPRUCE_LOG,
            Material.JUNGLE_LOG,      Material.STRIPPED_JUNGLE_LOG,
            Material.ACACIA_LOG,      Material.STRIPPED_ACACIA_LOG,
            Material.DARK_OAK_LOG,    Material.STRIPPED_DARK_OAK_LOG,
            Material.MANGROVE_LOG,    Material.STRIPPED_MANGROVE_LOG,
            Material.CHERRY_LOG,      Material.STRIPPED_CHERRY_LOG
    );

    private static final Set<Material> PLANKS = EnumSet.of(
            Material.OAK_PLANKS,
            Material.BIRCH_PLANKS,
            Material.SPRUCE_PLANKS,
            Material.JUNGLE_PLANKS,
            Material.ACACIA_PLANKS,
            Material.DARK_OAK_PLANKS,
            Material.MANGROVE_PLANKS,
            Material.CHERRY_PLANKS
    );

    private final SkillManager skillManager = SkillManager.getInstance();
    private final StatsManager statsManager = StatsManager.getInstance();
    private final PetManager petManager = PetManager.getInstance();

    private CompactListeners() {}

    public static CompactListeners getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        double strength = statsManager.get(player.getUniqueId(), Stat.STRENGTH);
        long xp = Math.max(1L, (long) (event.getDamage() * (1.0 + strength / 100.0)));
        grantXP(player, Skill.COMBAT, xp);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();

        if (CROP_MATERIALS.contains(type)) {
            org.bukkit.block.data.BlockData bd = event.getBlock().getBlockData();
            if (bd instanceof Ageable ageable && ageable.getAge() != ageable.getMaximumAge()) return;
            grantXP(player, Skill.FARMING, FARMING_CROP_XP);
            return;
        }
        if (LOGS.contains(type)) {
            grantXP(player, Skill.FORAGING, FORAGING_LOG_XP);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Wolf wolf)) return;
        AnimalTamer owner = wolf.getOwner();
        if (owner == null) return;
        Player player = Bukkit.getPlayer(owner.getUniqueId());
        if (player == null) return;
        grantXP(player, Skill.TAMING, TAMING_KILL_XP);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int plankCount = 0;
        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (ingredient != null && PLANKS.contains(ingredient.getType())) {
                plankCount += ingredient.getAmount();
            }
        }
        if (plankCount <= 0) return;
        grantXP(player, Skill.CARPENTRY, CARPENTRY_XP_PLANK * plankCount);
    }

    @EventHandler
    public void onEntityDeathBestiary(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        String mobType = event.getEntity().getType().name().toLowerCase();
        BestiaryManager.getInstance().recordKill(killer.getUniqueId(), mobType);
    }

    @EventHandler
    public void onEntityDeathPet(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        UUID uuid = killer.getUniqueId();
        if (petManager.getActivePet(uuid) == null) return;
        petManager.addPetXp(uuid, PET_XP_PER_KILL);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        StatsManager.getInstance().getAll(player.getUniqueId());
        // Persisted pets are loaded directly (bypassing equipPet), so re-apply the bonus on join.
        petManager.reapplyActivePetBonus(player.getUniqueId());
        com.skyblock.core.manager.BestiaryManager.getInstance().reapplyMilestoneStats(player.getUniqueId());
        ScoreboardManager.getInstance().initPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        java.util.UUID id = player.getUniqueId();
        // StatsManager.remove resets StatManager, so drop the balanced-bonus tracking too,
        // otherwise the on-join re-apply would subtract a phantom bonus and zero these stats.
        petManager.clearAppliedBonus(id);
        com.skyblock.core.manager.AccessoryManager.getInstance().clearAppliedTuning(id);
        com.skyblock.core.manager.BestiaryManager.getInstance().clearAppliedMilestoneStats(id);
        StatsManager.getInstance().remove(id);
        ScoreboardManager.getInstance().stopForPlayer(player);
    }

    private void grantXP(Player player, Skill skill, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, skill);
        skillManager.addXP(id, skill, amount);
        int after = skillManager.getLevel(id, skill);
        if (after > before) {
            String name = skill.name().charAt(0) + skill.name().substring(1).toLowerCase();
            player.sendTitle("§aSkill Level Up!", "§e" + name + " §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
