package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public final class CompactListeners implements Listener {

    private static final CompactListeners INSTANCE = new CompactListeners();

    private static final long FISHING_SKILL_XP  = 50L;
    private static final long TAMING_KILL_XP     = 10L;
    private static final long CARPENTRY_XP_PLANK =  1L;

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

    private CompactListeners() {}

    public static CompactListeners getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int before = skillManager.getLevel(uuid, Skill.FISHING);
        skillManager.addXP(uuid, Skill.FISHING, FISHING_SKILL_XP);
        int after = skillManager.getLevel(uuid, Skill.FISHING);
        if (after > before) {
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
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
