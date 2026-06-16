package com.skyblock.plugin.items;

import com.skyblock.items.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds formatted {@link ItemStack}s from {@link ItemStatConfig} definitions,
 * applying Hypixel-style stat lore and rarity colouring.
 */
public final class CustomItemFactory {

    private CustomItemFactory() {
    }

    /**
     * Creates a ready-to-use {@link ItemStack} from the given config.
     *
     * <p>The display name is coloured by rarity. Non-zero stats appear in the
     * lore, followed by a blank line and the rarity footer.</p>
     *
     * @param config the item config to materialise
     * @return a new {@link ItemStack} with display name and lore applied
     */
    public static ItemStack build(ItemStatConfig config) {
        ChatColor color = ItemBuilder.rarityColor(config.getRarity().name());
        List<String> lore = buildLore(config, color);
        return new ItemBuilder(config.getMaterial())
                .displayName(color + config.getDisplayName())
                .lore(lore)
                .build();
    }

    private static List<String> buildLore(ItemStatConfig config, ChatColor color) {
        List<String> lore = new ArrayList<>();
        ItemStatConfig.StatBlock s = config.getStats();
        if (s.getHealth() != 0) {
            lore.add(ChatColor.RED + "❤ Health: " + sign(s.getHealth()) + s.getHealth());
        }
        if (s.getDefense() != 0) {
            lore.add(ChatColor.GREEN + "❈ Defense: " + sign(s.getDefense()) + s.getDefense());
        }
        if (s.getStrength() != 0) {
            lore.add(ChatColor.RED + "⚔ Strength: " + sign(s.getStrength()) + s.getStrength());
        }
        if (s.getIntelligence() != 0) {
            lore.add(ChatColor.AQUA + "✎ Intelligence: " + sign(s.getIntelligence()) + s.getIntelligence());
        }
        if (s.getCritChance() != 0) {
            lore.add(ChatColor.BLUE + "☣ Crit Chance: " + sign(s.getCritChance()) + s.getCritChance() + "%");
        }
        if (s.getCritDamage() != 0) {
            lore.add(ChatColor.BLUE + "☠ Crit Damage: " + sign(s.getCritDamage()) + s.getCritDamage() + "%");
        }
        if (s.getSpeed() != 0) {
            lore.add(ChatColor.WHITE + "✦ Speed: " + sign(s.getSpeed()) + s.getSpeed());
        }
        lore.add("");
        lore.add(color + "" + ChatColor.BOLD + config.getRarity().getDisplayName().toUpperCase() + " ITEM");
        return lore;
    }

    private static String sign(int value) {
        return value >= 0 ? "+" : "";
    }
}
