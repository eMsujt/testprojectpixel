package com.skyblock.core.menu;

import com.skyblock.core.manager.CommissionManager;
import com.skyblock.core.manager.CommissionManager.Commission;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The "Commissions" menu (Emissary/King). A 4-row chest laid out 1:1 with
 * Hypixel: the active commission tiles on row 2 (slots 11, 12, 14, 15), with
 * Commission Milestones (Map, 30), Close (Barrier, 31) and Switch Type (Paper,
 * 32) on the bottom row. The auto-framed border fills the rest.
 */
public final class MiningCommissionMenu extends Menu {

    private static final String TITLE = "Commissions";

    /** Commission tiles (row 2, cols 3-4 and 6-7). */
    private static final int[] COMMISSION_SLOTS = {11, 12, 14, 15};

    private final Player player;

    public MiningCommissionMenu(Player player) {
        super(TITLE, 4);
        this.player = player;
    }

    @Override
    protected void build() {
        UUID id = player.getUniqueId();
        CommissionManager commissions = CommissionManager.getInstance();

        List<Commission> active = commissions.getActiveCommissions(id);
        for (int i = 0; i < active.size() && i < COMMISSION_SLOTS.length; i++) {
            Commission commission = active.get(i);
            int target = commission.getType().getTarget();
            List<String> lore = new ArrayList<>();
            lore.add("§7Area: §e" + commission.getType().getLocation().getDisplayName());
            lore.add("§7Progress: §e" + commission.getProgress() + "§7/§e" + target);
            lore.add(commission.isComplete() ? "§aComplete!" : "§7In progress");
            setItem(COMMISSION_SLOTS[i], new ItemBuilder(Material.WRITABLE_BOOK)
                    .displayName("§e" + commission.getType().getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> e.setCancelled(true));
        }

        setItem(30, new ItemBuilder(Material.MAP)
                .displayName("§aCommission Milestones")
                .lore("§7View your commission milestones.")
                .build(), e -> e.setCancelled(true));

        setItem(31, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> { e.setCancelled(true); player.closeInventory(); });

        setItem(32, new ItemBuilder(Material.PAPER)
                .displayName("§aSwitch Type")
                .lore("§7Switch commission type.")
                .build(), e -> e.setCancelled(true));
    }
}
