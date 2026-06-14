package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

/**
 * The Sacks menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §6Sacks}. Slots 0–44 are populated
 * from the owning {@link SkyBlockProfile}'s sack contents, one icon per stored
 * item, matching Hypixel's layout. Each key is an item name and its value is
 * the stored quantity.</p>
 */
public class SacksMenu extends Menu {

    private final Player player;

    public SacksMenu(Player player) {
        super("§6Sacks", 6);
        this.player = Objects.requireNonNull(player, "player");
    }

    @Override
    protected void build() {
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        int slot = 0;
        for (Map.Entry<String, Integer> entry : profile.getSackContents().entrySet()) {
            if (slot >= 45) {
                break;
            }
            String name = entry.getKey();
            int amount = entry.getValue() != null ? entry.getValue() : 0;
            Material material = Material.matchMaterial(name);
            if (material == null) {
                material = Material.PAPER;
            }
            setItem(slot, new ItemBuilder(material)
                    .displayName("§a" + name)
                    .lore("§7Stored: §e" + amount)
                    .build());
            slot++;
        }
    }
}
