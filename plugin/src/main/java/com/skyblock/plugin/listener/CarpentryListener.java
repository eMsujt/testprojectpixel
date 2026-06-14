package com.skyblock.plugin.listener;

import com.skyblock.plugin.managers.SkillsManager;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.skill.SkillActionBar;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Awards Carpentry XP whenever a player crafts an item, scaling the reward by the
 * number of items produced. A shift-click bulk craft (vanilla {@code QUICK_MOVE})
 * pays out for every crafted item, not just the single result shown in the slot.
 */
public final class CarpentryListener implements Listener {

    /** Carpentry storage key in {@link SkillsManager}. */
    private static final String SKILL = "carpentry";

    /** Carpentry XP granted per crafted item. */
    private static final long XP_PER_ITEM = 1L;

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getRecipe() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        int crafted = craftedAmount(event);
        if (crafted <= 0) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        long xp = XP_PER_ITEM * crafted;
        skillsManager.addSkillXP(player.getUniqueId(), SKILL, xp);
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).addSkillXp(SKILL, xp);
        SkillActionBar.getInstance().queue(player, "§a+" + xp + " Carpentry XP");
    }

    /**
     * The number of items produced by the craft. A normal click yields a single
     * recipe output; a shift-click ({@code QUICK_MOVE}) crafts repeatedly until an
     * ingredient runs out, so the count is bounded by the smallest ingredient stack
     * in the crafting matrix.
     */
    private int craftedAmount(CraftItemEvent event) {
        int perCraft = event.getRecipe().getResult().getAmount();
        if (!event.isShiftClick()) {
            return perCraft;
        }
        int crafts = Integer.MAX_VALUE;
        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (ingredient != null && ingredient.getType() != Material.AIR) {
                crafts = Math.min(crafts, ingredient.getAmount());
            }
        }
        return crafts == Integer.MAX_VALUE ? perCraft : perCraft * crafts;
    }
}
