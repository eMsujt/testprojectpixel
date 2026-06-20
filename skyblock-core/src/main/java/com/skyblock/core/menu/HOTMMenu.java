package com.skyblock.core.menu;

import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.HOTMManager.HotMNode;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 54-slot Heart of the Mountain perk tree GUI. Gray-pane border on all four
 * edges; three info tiles in the first inner row (Mithril Powder, Gemstone
 * Powder, HOTM Tier) followed by one clickable tile per {@link HotMNode}.
 * Clicking a node tile spends Mithril Powder to purchase the next level.
 */
public final class HOTMMenu extends AbstractSkyBlockMenu {

    private static final int CLOSE_SLOT = 49;

    public HOTMMenu(Player player) {
        super(player, "§5Heart of the Mountain", 6);
    }

    @Override
    protected void populate() {
        HOTMManager hotm = HOTMManager.getInstance();
        UUID playerId = player.getUniqueId();

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        List<Integer> inner = new ArrayList<>();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            } else {
                inner.add(slot);
            }
        }

        // Info tiles: mithril powder, gemstone powder, HOTM tier.
        setItem(inner.get(0), new ItemBuilder(Material.LAPIS_LAZULI)
                .displayName("§9Mithril Powder")
                .lore("§7Balance: §9" + String.format("%,d", hotm.getMithrilPowder(playerId)))
                .build());

        setItem(inner.get(1), new ItemBuilder(Material.AMETHYST_SHARD)
                .displayName("§dGemstone Powder")
                .lore("§7Balance: §d" + String.format("%,d", hotm.getGemstonePowder(playerId)))
                .build());

        setItem(inner.get(2), new ItemBuilder(Material.EMERALD)
                .displayName("§aHOTM Tier")
                .lore("§7Tier: §a" + hotm.getHotmTier(playerId) + "§7/§a" + HOTMManager.MAX_TIER)
                .lore("§7Mining XP: §a" + String.format("%,d", hotm.getMiningXp(playerId)))
                .build());

        setItem(inner.get(3), pane);

        // Node tiles starting at inner[4] — one per HotMNode (24 nodes, 24 slots).
        HotMNode[] nodes = HotMNode.values();
        for (int i = 0; i < nodes.length; i++) {
            HotMNode node = nodes[i];
            int slot = inner.get(4 + i);
            setItem(slot, nodeItem(hotm, node, playerId), e -> {
                int result = hotm.purchaseUpgrade(playerId, node);
                if (result > 0) {
                    player.sendMessage("§aUpgraded §5" + node.getDisplayName()
                            + " §ato level " + result + ".");
                } else if (result == -1) {
                    player.sendMessage("§e" + node.getDisplayName() + " is already maxed.");
                } else {
                    player.sendMessage("§cNot enough Mithril Powder to upgrade " + node.getDisplayName() + ".");
                }
                open(player);
            });
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the HOTM menu.")
                .build(), e -> player.closeInventory());
    }

    private ItemStack nodeItem(HOTMManager hotm, HotMNode node, UUID playerId) {
        int level = hotm.getLevel(playerId, node);
        boolean maxed = level >= node.maxLevel;
        ItemBuilder builder = new ItemBuilder(maxed ? Material.ENCHANTED_BOOK : Material.BOOK)
                .displayName("§5" + node.getDisplayName())
                .addLore("§7Level: §a" + level + "§7/§a" + node.maxLevel);
        if (maxed) {
            builder.addLore("§eMaxed");
        } else {
            int cost = hotm.getUpgradeCost(node, level);
            if (cost > 0) {
                builder.addLore("§7Upgrade cost: §9" + String.format("%,d", cost) + " Mithril Powder");
            }
            builder.addLore("§eClick to upgrade");
        }
        return builder.build();
    }
}
