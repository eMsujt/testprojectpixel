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

public final class HotmMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§bHeart of the Mountain";

    // Hypixel anchors the info/powder bar along the bottom row.
    private static final int HEADER_SLOT   = 48;
    private static final int POWDER_SLOT   = 45;
    private static final int GEMSTONE_SLOT = 46;
    private static final int CLOSE_SLOT    = 53;

    private static final int[] NODE_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39
    };

    public HotmMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        setItem(9, pane);  setItem(17, pane);
        setItem(18, pane); setItem(26, pane);
        setItem(27, pane); setItem(35, pane);
        setItem(36, pane); setItem(44, pane);

        UUID uuid = player.getUniqueId();
        HOTMManager manager = HOTMManager.getInstance();

        int tier = manager.getHotmTier(uuid);
        long xp = manager.getMiningXp(uuid);

        setItem(HEADER_SLOT, new ItemBuilder(Material.DIAMOND_PICKAXE)
                .displayName("§bHeart of the Mountain")
                .lore(
                        "§7Tier: §b" + tier + "§7/§b" + HOTMManager.MAX_TIER,
                        "§7Mining XP: §e" + String.format("%,d", xp),
                        tier >= HOTMManager.MAX_TIER ? "§aMax tier reached!" : "§7Keep mining to reach the next tier!")
                .build());

        setItem(POWDER_SLOT, new ItemBuilder(Material.PRISMARINE_CRYSTALS)
                .displayName("§3Mithril Powder")
                .lore("§7Amount: §3" + String.format("%,d", manager.getMithrilPowder(uuid)))
                .build());

        setItem(GEMSTONE_SLOT, new ItemBuilder(Material.AMETHYST_SHARD)
                .displayName("§dGemstone Powder")
                .lore("§7Amount: §d" + String.format("%,d", manager.getGemstonePowder(uuid)))
                .build());

        setItem(50, new ItemBuilder(Material.AMETHYST_CLUSTER)
                .displayName("§dCrystal Hollows Crystals")
                .lore("§7Track the crystals you've", "§7placed in the Crystal Nucleus.")
                .build());

        setItem(51, new ItemBuilder(Material.PAINTING)
                .displayName("§dCrystal Nucleus RNG Meter")
                .lore("§7Track your progress toward", "§7a guaranteed crystal reward.")
                .build());

        setItem(52, new ItemBuilder(Material.REDSTONE)
                .displayName("§cReset Heart of the Mountain")
                .lore("§7Reset all your perks and", "§7refund your spent powder.")
                .build());

        HotMNode[] nodes = HotMNode.values();
        for (int i = 0; i < NODE_SLOTS.length && i < nodes.length; i++) {
            HotMNode node = nodes[i];
            int level = manager.getLevel(uuid, node);
            boolean maxed = level >= node.maxLevel;
            int cost = manager.getUpgradeCost(node, level);

            Material mat = maxed ? Material.GOLD_BLOCK
                    : level > 0 ? Material.EMERALD
                    : Material.COAL_BLOCK;

            List<String> lore = new ArrayList<>();
            lore.add("§7Level: §e" + level + "§7/§e" + node.maxLevel);
            if (maxed) {
                lore.add("§aMaxed out!");
            } else {
                if (cost > 0) lore.add("§7Next level: §3" + String.format("%,d", cost) + " Mithril Powder");
                lore.add("");
                lore.add("§eClick to upgrade!");
            }

            int nodeSlot = NODE_SLOTS[i];
            setItem(nodeSlot, new ItemBuilder(mat)
                    .displayName((maxed ? "§a" : level > 0 ? "§e" : "§7") + node.getDisplayName())
                    .lore(lore.toArray(new String[0]))
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        int result = manager.purchaseUpgrade(uuid, node);
                        if (result == -1) {
                            player.sendMessage("§c" + node.getDisplayName() + " is already maxed.");
                        } else if (result == -2) {
                            player.sendMessage("§cNot enough Mithril Powder to upgrade " + node.getDisplayName() + ".");
                        } else {
                            player.sendMessage("§aUpgraded §b" + node.getDisplayName() + " §ato level §e" + result + "§a!");
                        }
                        open(player);
                    });
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Click to close.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
