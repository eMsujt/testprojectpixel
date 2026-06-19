package com.skyblock.core.menu;

import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ReforgeMenu extends Menu {

    /** Inner area: rows 1–4, columns 1–7, 28 slots per page. */
    private static final int PAGE_SIZE = 28;

    private static final int[] INNER_SLOTS = buildInnerSlots();

    private static int[] buildInnerSlots() {
        int[] slots = new int[PAGE_SIZE];
        int idx = 0;
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                slots[idx++] = row * 9 + col;
            }
        }
        return slots;
    }

    private static final ReforgeManager.ReforgeType[] REFORGES = buildReforgeList();

    private static ReforgeManager.ReforgeType[] buildReforgeList() {
        List<ReforgeManager.ReforgeType> list = new ArrayList<>();
        for (ReforgeManager.ReforgeType r : ReforgeManager.ReforgeType.values()) {
            if (r != ReforgeManager.ReforgeType.NONE) list.add(r);
        }
        return list.toArray(new ReforgeManager.ReforgeType[0]);
    }

    private int page;

    public ReforgeMenu() {
        super("Reforge Anvil", 6);
        this.page = 0;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            int row = slot / 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        int start = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE; i++) {
            int idx = start + i;
            if (idx >= REFORGES.length) break;
            ReforgeManager.ReforgeType reforge = REFORGES[idx];
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.ANVIL)
                    .displayName("§d" + reforge.getDisplayName())
                    .lore(buildStatLore(reforge))
                    .build());
        }

        int totalPages = (REFORGES.length + PAGE_SIZE - 1) / PAGE_SIZE;

        if (page > 0) {
            setItem(48, new ItemBuilder(Material.ARROW)
                    .displayName("§aPrevious Page")
                    .lore("§7Page " + page + " of " + totalPages)
                    .build(),
                    e -> {
                        page--;
                        if (e.getWhoClicked() instanceof Player p) open(p);
                    });
        }

        if (start + PAGE_SIZE < REFORGES.length) {
            setItem(50, new ItemBuilder(Material.ARROW)
                    .displayName("§aNext Page")
                    .lore("§7Page " + (page + 2) + " of " + totalPages)
                    .build(),
                    e -> {
                        page++;
                        if (e.getWhoClicked() instanceof Player p) open(p);
                    });
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        super.handleClick(event);
    }

    private static List<String> buildStatLore(ReforgeManager.ReforgeType reforge) {
        List<String> lore = new ArrayList<>();
        if (reforge.getStrengthBonus() != 0)
            lore.add("§cStrength: §a+" + reforge.getStrengthBonus());
        if (reforge.getDefenseBonus() != 0)
            lore.add("§aDefense: §a+" + reforge.getDefenseBonus());
        if (reforge.getSpeedBonus() != 0)
            lore.add("§fSpeed: §a+" + reforge.getSpeedBonus());
        if (lore.isEmpty())
            lore.add("§7No stat bonuses.");
        return lore;
    }
}
