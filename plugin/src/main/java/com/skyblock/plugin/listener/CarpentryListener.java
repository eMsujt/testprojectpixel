package com.skyblock.plugin.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.plugin.profile.ProfileManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

/**
 * Awards Carpentry XP whenever a player crafts an item that uses wood planks
 * as ingredients, granting 1 XP per plank slot consumed in the crafting matrix.
 */
public final class CarpentryListener implements Listener {

    /** Carpentry storage key in {@link SkillManager}. */
    private static final String SKILL = "carpentry";

    /** Carpentry XP granted per plank ingredient slot. */
    private static final long XP_PER_PLANK = 1L;

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

    private final SkillManager skillsManager = SkillManager.getInstance();

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getRecipe() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        int plankCount = countPlanks(event);
        if (plankCount <= 0) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        long xp = XP_PER_PLANK * plankCount;
        skillsManager.addSkillXP(player.getUniqueId(), SKILL, xp);
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).addSkillXp(SKILL, xp);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a+" + xp + " Carpentry XP"));
    }

    /** Counts the total number of plank items across all slots in the crafting matrix. */
    private int countPlanks(CraftItemEvent event) {
        int total = 0;
        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (ingredient != null && PLANKS.contains(ingredient.getType())) {
                total += ingredient.getAmount();
            }
        }
        return total;
    }
}
