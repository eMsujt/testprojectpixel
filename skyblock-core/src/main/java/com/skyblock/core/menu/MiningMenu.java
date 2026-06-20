package com.skyblock.core.menu;

import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.HOTMManager.HotMNode;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 6-row Heart of the Mountain perk-tree GUI. Each perk node is clickable and
 * spends Mithril Powder to purchase the next upgrade level, then refreshes the
 * menu in place.
 *
 * <p>Layout (rows 0-5, 9 columns each):
 * <pre>
 *  0: [pane × 4][SUMMARY][pane × 4]
 *  1: [pane][MSB][pane][PICK][pane][SKYM][pane][MANI][pane]
 *  2: [MSPD][pane][MFRT][pane][DPOW][pane][EFMN][pane][QFRG]
 *  3: [pane][TINS][pane][LCAV][pane][PBUF][pane][MMAD][pane]
 *  4: [GOBK][pane][STPW][pane][MOLE][pane][PROF][pane][LONE]
 *  5: [GEXP][FORTU][MINE][SEAS][CLOSE][ANOM][VSEK][pane][pane]
 * </pre>
 */
public final class MiningMenu extends Menu {

    private static final String TITLE = "§bHeart of the Mountain";

    private static final int SLOT_SUMMARY = 4;
    private static final int SLOT_CLOSE   = 49;

    /** Maps inventory slot → perk node for the tree layout. */
    private static final Map<Integer, HotMNode> SLOT_TO_NODE;
    static {
        Map<Integer, HotMNode> m = new LinkedHashMap<>();
        // Row 1 (9-17): toggle/ability perks
        m.put(10, HotMNode.MINING_SPEED_BOOST);
        m.put(12, HotMNode.PICKOBULUS);
        m.put(14, HotMNode.SKY_MALL);
        m.put(16, HotMNode.MANIACAL_MINER);
        // Row 2 (18-26): high-tier passive perks
        m.put(18, HotMNode.MINING_SPEED);
        m.put(20, HotMNode.MINING_FORTUNE);
        m.put(22, HotMNode.DAILY_POWDER);
        m.put(24, HotMNode.EFFICIENT_MINER);
        m.put(26, HotMNode.QUICK_FORGE);
        // Row 3 (27-35): mid-tier passive perks
        m.put(28, HotMNode.TITANIUM_INSANITY);
        m.put(30, HotMNode.LUCK_OF_THE_CAVE);
        m.put(32, HotMNode.POWDER_BUFF);
        m.put(34, HotMNode.MINING_MADNESS);
        // Row 4 (36-44): lower-tier passive perks
        m.put(36, HotMNode.GOBLIN_KILLER);
        m.put(38, HotMNode.STAR_POWDER);
        m.put(40, HotMNode.MOLE);
        m.put(42, HotMNode.PROFESSIONAL);
        m.put(44, HotMNode.LONESOME_MINER);
        // Row 5 (45-53): base perks (close button at slot 49)
        m.put(45, HotMNode.GREAT_EXPLORER);
        m.put(46, HotMNode.FORTUNATE);
        m.put(47, HotMNode.MINING_EXPERIENCE_BOOST);
        m.put(48, HotMNode.SEASONED_MINEMAN);
        m.put(50, HotMNode.ANOMALOUS_DESIRE);
        m.put(51, HotMNode.VEIN_SEEKER);
        SLOT_TO_NODE = Collections.unmodifiableMap(m);
    }

    private final Player player;

    public MiningMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            if (!SLOT_TO_NODE.containsKey(slot) && slot != SLOT_SUMMARY && slot != SLOT_CLOSE) {
                setItem(slot, pane);
            }
        }

        UUID id = player.getUniqueId();
        HOTMManager hotm = HOTMManager.getInstance();

        setItem(SLOT_SUMMARY, new ItemBuilder(Material.HEART_OF_THE_SEA)
                .displayName("§dHeart of the Mountain")
                .lore(
                        "§7Tier: §e" + hotm.getHotmTier(id) + " §7/ §e" + HOTMManager.MAX_TIER,
                        "§7HOTM XP: §e" + String.format("%,d", hotm.getMiningXp(id)),
                        "§7Mithril Powder: §e" + String.format("%,d", hotm.getMithrilPowder(id)),
                        "§7Gemstone Powder: §e" + String.format("%,d", hotm.getGemstonePowder(id)))
                .build(),
                e -> e.setCancelled(true));

        for (Map.Entry<Integer, HotMNode> entry : SLOT_TO_NODE.entrySet()) {
            int slot = entry.getKey();
            HotMNode node = entry.getValue();
            int level = hotm.getLevel(id, node);
            boolean maxed = level >= node.maxLevel;
            int cost = hotm.getUpgradeCost(node, level);

            List<String> lore = new ArrayList<>();
            lore.add("§7Level: §e" + level + " §7/ §e" + node.maxLevel);
            if (maxed) {
                lore.add("§aMAXED");
            } else {
                if (cost > 0) {
                    lore.add("§7Cost: §e" + String.format("%,d", cost) + " Mithril Powder");
                }
                lore.add("§eClick to upgrade!");
            }

            setItem(slot, new ItemBuilder(Material.ENCHANTED_BOOK)
                    .displayName((maxed ? "§a" : "§e") + node.getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (!maxed) {
                            int result = hotm.purchaseUpgrade(id, node);
                            if (result == -2) {
                                player.sendMessage("§cNot enough Mithril Powder!");
                            }
                            open(player);
                        }
                    });
        }

        setItem(SLOT_CLOSE, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Click to close.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
