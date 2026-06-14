package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * The Accessory Bag menu.
 *
 * <p>A 45-slot (5-row) chest titled {@code §dAccessory Bag}. Slots 0–44 are
 * populated from the owning {@link SkyBlockProfile}'s accessory bag contents,
 * one accessory per slot, matching Hypixel's layout.</p>
 */
public class AccessoryBagMenu extends Menu {

    private final SkyBlockProfile profile;

    public AccessoryBagMenu(SkyBlockProfile profile) {
        super("§dAccessory Bag", 5);
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    @Override
    protected void build() {
        List<ItemStack> contents = profile.getAccessoryBagContents();
        for (int slot = 0; slot < 45 && slot < contents.size(); slot++) {
            ItemStack item = contents.get(slot);
            if (item != null) {
                setItem(slot, item);
            }
        }
    }
}
