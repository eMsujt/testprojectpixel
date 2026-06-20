package com.skyblock.core.menu;

import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.HOTMManager.HotMNode;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 54-slot Heart of the Mountain perk tree menu.
 */
public final class HotmMenu extends AbstractMenu {

    private static final int SUMMARY_SLOT = 4;
    private static final int FIRST_PERK_SLOT = 9;
    private static final int CLOSE_SLOT = 49;

    public HotmMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§bHeart of the Mountain", 54);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        HOTMManager hotm = HOTMManager.getInstance();

        ItemBuilder pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r");
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane.build());
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane.build());

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.BEACON)
                .displayName("§bHeart of the Mountain")
                .lore(
                        "§7HOTM Tier: §e" + hotm.getHotmTier(playerId) + "§7/§e" + HOTMManager.MAX_TIER,
                        "§7Mithril Powder: §e" + hotm.getMithrilPowder(playerId),
                        "§7Gemstone Powder: §e" + hotm.getGemstonePowder(playerId))
                .build());

        HotMNode[] nodes = HotMNode.values();
        for (int i = 0; i < nodes.length; i++) {
            HotMNode node = nodes[i];
            int slot = FIRST_PERK_SLOT + i;
            if (slot >= CLOSE_SLOT) break;
            int level = hotm.getLevel(playerId, node);
            boolean maxed = level >= node.maxLevel;
            int cost = hotm.getUpgradeCost(node, level);

            List<String> lore = new ArrayList<>();
            lore.add("§7Level: §e" + level + "§7/§e" + node.maxLevel);
            if (maxed) {
                lore.add("§aMaxed out!");
            } else if (cost >= 0) {
                lore.add("§7Next level: §b" + cost + " Mithril Powder");
                lore.add("§eClick to upgrade!");
            } else {
                lore.add("§eClick to upgrade!");
            }

            setItem(slot, new ItemBuilder(Material.ENCHANTED_BOOK)
                    .displayName((maxed ? "§a" : "§e") + node.getDisplayName())
                    .lore(lore.toArray(new String[0]))
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        hotm.purchaseUpgrade(playerId, node);
                        open(player);
                    });
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the Heart of the Mountain menu.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
