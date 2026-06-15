package com.skyblock.plugin.listener;

import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Awards Alchemy XP directly to the player's {@link PlayerProfile} whenever
 * a player picks up a brewed potion from a brewing stand output slot.
 */
public final class AlchemyXpListener implements Listener {

    private static final double POTION_XP = 22.5;

    @EventHandler
    public void onAlchemy(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;
        // Slots 0-2 are the three output potion slots of a brewing stand
        if (event.getRawSlot() < 0 || event.getRawSlot() > 2) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("alchemy", POTION_XP);
        XpActionBar.send(player, "alchemy", POTION_XP, profile.getSkillXp("alchemy"));
    }
}
