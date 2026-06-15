package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Awards Enchanting XP directly to the player's {@link SkyBlockProfile} whenever
 * a player enchants an item. XP equals the enchantment level cost of the operation.
 */
public final class EnchantingXpListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!(event.getEnchanter() instanceof Player player)) return;

        double xp = event.getExpLevelCost();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("enchanting", xp);
        XpActionBar.send(player, "enchanting", xp, profile.getSkillXp("enchanting"));
    }
}
