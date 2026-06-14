package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * The Fishing Bag menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §9Fishing Bag}. Slots 0–44 are
 * populated from the owning {@link SkyBlockProfile}'s fishing bag contents, one
 * stack per slot, matching Hypixel's layout.</p>
 */
public class FishingBagMenu extends Menu {

    private final Player player;

    public FishingBagMenu(Player player) {
        super("§9Fishing Bag", 6);
        this.player = Objects.requireNonNull(player, "player");
    }

    @Override
    protected void build() {
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getFishingBagContents();
        for (int slot = 0; slot < 45 && slot < contents.size(); slot++) {
            ItemStack item = contents.get(slot);
            if (item != null) {
                setItem(slot, item);
            }
        }
    }
}
