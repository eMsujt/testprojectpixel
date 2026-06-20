package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Grants XP for Taming (wolf kills), Fishing (fish/sea-creature catches), and
 * Carpentry (plank-based crafts) — skills not covered by SkillProgressionListener.
 */
public final class SkillXpListener implements Listener {

    // --- Taming (wolf kills) ---
    private static final long TAMING_KILL_XP = 10L;

    // --- Fishing ---
    private static final long CAUGHT_FISH_XP = 50L;

    private static final Map<EntityType, Long> SEA_CREATURE_XP = Map.ofEntries(
            Map.entry(EntityType.SQUID,          8L),
            Map.entry(EntityType.GUARDIAN,       10L),
            Map.entry(EntityType.ELDER_GUARDIAN, 50L),
            Map.entry(EntityType.ZOMBIE,          5L),
            Map.entry(EntityType.DROWNED,         5L),
            Map.entry(EntityType.SKELETON,        8L),
            Map.entry(EntityType.WITCH,          18L),
            Map.entry(EntityType.IRON_GOLEM,    120L)
    );

    // --- Carpentry (plank crafts) ---
    private static final long CARPENTRY_XP_PER_PLANK = 1L;

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

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Wolf wolf)) {
            return;
        }
        AnimalTamer owner = wolf.getOwner();
        if (owner == null) {
            return;
        }
        Player player = Bukkit.getPlayer(owner.getUniqueId());
        if (player == null) {
            return;
        }
        grantXP(player, Skill.TAMING, TAMING_KILL_XP);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity caught = event.getCaught();

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (!(caught instanceof Item item)) return;
            Material type = item.getItemStack().getType();
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
            profile.addSkillXp("fishing", CAUGHT_FISH_XP);
            XpActionBar.send(player, "fishing", CAUGHT_FISH_XP, profile.getSkillXp("fishing"));
            CollectionManager.getInstance().addItems(player.getUniqueId(), type.name(), 1);

        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && caught != null) {
            Long xp = SEA_CREATURE_XP.get(caught.getType());
            if (xp == null) return;
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
            profile.addSkillXp("fishing", xp);
            XpActionBar.send(player, "fishing", xp, profile.getSkillXp("fishing"));
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getRecipe() == null || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int plankCount = 0;
        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (ingredient != null && PLANKS.contains(ingredient.getType())) {
                plankCount += ingredient.getAmount();
            }
        }
        if (plankCount <= 0) {
            return;
        }
        grantXP(player, Skill.CARPENTRY, CARPENTRY_XP_PER_PLANK * plankCount);
    }

    private void grantXP(Player player, Skill skill, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, skill);
        skillManager.addXP(id, skill, amount);
        int after = skillManager.getLevel(id, skill);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, skill, before, after);
            String name = skill.name().charAt(0) + skill.name().substring(1).toLowerCase();
            player.sendTitle("§aSkill Level Up!", "§e" + name + " §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
