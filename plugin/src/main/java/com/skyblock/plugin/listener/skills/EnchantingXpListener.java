package com.skyblock.plugin.listener.skills;

import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Awards Enchanting XP directly to the player's {@link PlayerProfile} whenever
 * a player enchants an item. XP equals the enchantment level cost of the operation.
 */
public final class EnchantingXpListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!(event.getEnchanter() instanceof Player player)) return;

        double xp = event.getExpLevelCost();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("enchanting", xp);
        XpActionBar.send(player, "enchanting", xp, profile.getSkillXp("enchanting"));
    }
}
