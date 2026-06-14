package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * The Quiver menu.
 *
 * <p>A 36-slot (4-row) chest titled {@code §eQuiver}. Slots 0–35 are populated
 * from the owning {@link SkyBlockProfile}'s quiver contents, one stack per slot,
 * matching Hypixel's layout.</p>
 */
public class QuiverMenu extends Menu {

    private final SkyBlockProfile profile;

    public QuiverMenu(SkyBlockProfile profile) {
        super("§eQuiver", 4);
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    @Override
    protected void build() {
        List<ItemStack> contents = profile.getQuiverContents();
        for (int slot = 0; slot < 36 && slot < contents.size(); slot++) {
            ItemStack item = contents.get(slot);
            if (item != null) {
                setItem(slot, item);
            }
        }
    }
}
