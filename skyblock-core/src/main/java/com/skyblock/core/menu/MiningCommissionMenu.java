package com.skyblock.core.menu;

import com.skyblock.core.manager.CommissionManager;
import com.skyblock.core.manager.CommissionManager.Commission;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Canonical "King's Commissions" menu. A 54-slot (6-row) chest GUI framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border with a clipboard at slot 4 summarising the
 * viewing player's total completed commissions (from {@link CommissionManager}),
 * and one tile per currently assigned commission showing its progress toward the
 * target and whether it can be claimed.
 */
public final class MiningCommissionMenu extends Menu {

    private static final String TITLE = "§6King's Commissions";
    private static final int SUMMARY_SLOT = 4;

    /** Commission tiles laid out across the third interior row. */
    private static final int[] COMMISSION_SLOTS = {
            20, 22, 24
    };

    private final Player player;

    public MiningCommissionMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        CommissionManager commissions = CommissionManager.getInstance();

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Completed: §e" + String.format("%,d", commissions.getCompletedCount(id)));
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName("§6" + player.getName() + "'s Commissions")
                .lore(summaryLore)
                .build(),
                e -> e.setCancelled(true));

        List<Commission> active = commissions.getActiveCommissions(id);
        for (int i = 0; i < active.size() && i < COMMISSION_SLOTS.length; i++) {
            Commission commission = active.get(i);
            int target = commission.getType().getTarget();
            List<String> lore = new ArrayList<>();
            lore.add("§7Area: §e" + commission.getType().getLocation().getDisplayName());
            lore.add("§7Progress: §e" + commission.getProgress() + "§7/§e" + target);
            lore.add(commission.isComplete() ? "§aComplete!" : "§7In progress");
            setItem(COMMISSION_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§e" + commission.getType().getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
