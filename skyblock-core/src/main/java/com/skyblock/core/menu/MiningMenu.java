package com.skyblock.core.menu;

import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mining overview GUI — displays mining skill XP/level, Mithril/Gemstone powder,
 * and Heart of the Mountain tier in a 5-row chest inventory.
 */
public final class MiningMenu extends Menu {

    private static final String TITLE = "§8Mining Overview";

    private static final int SLOT_MINING   = 13;
    private static final int SLOT_MITHRIL  = 20;
    private static final int SLOT_HOTM     = 22;
    private static final int SLOT_GEMSTONE = 24;

    private final Player player;

    public MiningMenu(Player player) {
        super(TITLE, 5);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        MiningManager mining = MiningManager.getInstance();
        HOTMManager hotm = HOTMManager.getInstance();

        // Mining skill summary
        int level = mining.getLevel(id);
        double xp = mining.getXp(id);
        int speedBonus = mining.getSpeedBonusForPlayer(id);
        List<String> miningLore = new ArrayList<>();
        miningLore.add("§7Level: §e" + level + " §7/ §e" + 50);
        miningLore.add("§7XP: §e" + String.format("%,.1f", xp));
        miningLore.add("§7Speed Bonus: §e+" + speedBonus);
        setItem(SLOT_MINING, new ItemBuilder(Material.IRON_PICKAXE)
                .displayName("§bMining Skill")
                .lore(miningLore)
                .build(),
                e -> e.setCancelled(true));

        // Mithril Powder
        long mithril = hotm.getMithrilPowder(id);
        List<String> mithrilLore = new ArrayList<>();
        mithrilLore.add("§7Amount: §e" + String.format("%,d", mithril));
        mithrilLore.add("§8Used to upgrade HOTM perks");
        setItem(SLOT_MITHRIL, new ItemBuilder(Material.PRISMARINE_CRYSTALS)
                .displayName("§aMithril Powder")
                .lore(mithrilLore)
                .build(),
                e -> e.setCancelled(true));

        // HOTM Tier
        int tier = hotm.getHotmTier(id);
        long hotmXp = hotm.getMiningXp(id);
        List<String> hotmLore = new ArrayList<>();
        hotmLore.add("§7Tier: §e" + tier + " §7/ §e" + HOTMManager.MAX_TIER);
        hotmLore.add("§7HOTM XP: §e" + String.format("%,d", hotmXp));
        setItem(SLOT_HOTM, new ItemBuilder(Material.HEART_OF_THE_SEA)
                .displayName("§dHeart of the Mountain")
                .lore(hotmLore)
                .build(),
                e -> e.setCancelled(true));

        // Gemstone Powder
        long gemstone = hotm.getGemstonePowder(id);
        List<String> gemstoneLore = new ArrayList<>();
        gemstoneLore.add("§7Amount: §e" + String.format("%,d", gemstone));
        gemstoneLore.add("§8Used to upgrade Crystal Hollows perks");
        setItem(SLOT_GEMSTONE, new ItemBuilder(Material.AMETHYST_SHARD)
                .displayName("§5Gemstone Powder")
                .lore(gemstoneLore)
                .build(),
                e -> e.setCancelled(true));
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 45; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 36 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
