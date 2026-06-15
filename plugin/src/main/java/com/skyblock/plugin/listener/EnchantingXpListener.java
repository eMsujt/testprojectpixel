package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Awards Enchanting XP directly to the player's {@link SkyBlockProfile} whenever
 * a player enchants an item.
 */
public final class EnchantingXpListener implements Listener {

    private static final long ENCHANT_XP = 150L;

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!(event.getEnchanter() instanceof Player player)) return;

        double xp = ENCHANT_XP;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("enchanting", xp);
        XpActionBar.send(player, "enchanting", xp, profile.getSkillXp("enchanting"));
    }
}
