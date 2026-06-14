package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * The Potion Bag menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §5Potion Bag}. Slots 0–44 are
 * populated from the owning {@link SkyBlockProfile}'s potion bag contents, one
 * stack per slot, matching Hypixel's layout.</p>
 */
public class PotionBagMenu extends Menu {

    private final Player player;

    public PotionBagMenu(Player player) {
        super("§5Potion Bag", 6);
        this.player = Objects.requireNonNull(player, "player");
    }

    @Override
    protected void build() {
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getPotionBagContents();
        for (int slot = 0; slot < 45 && slot < contents.size(); slot++) {
            ItemStack item = contents.get(slot);
            if (item != null) {
                setItem(slot, item);
            }
        }
    }
}
