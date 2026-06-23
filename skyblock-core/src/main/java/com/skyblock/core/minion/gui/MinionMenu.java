package com.skyblock.core.minion.gui;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Canonical management GUI for a single placed minion.
 *
 * <p>A 27-slot (3-row) chest framed by a {@code BLACK_STAINED_GLASS_PANE} border.
 * Slot 13 shows the minion's type and tier; slot 15 upgrades the minion to the
 * next tier; slot 22 closes the inventory.</p>
 *
 * <p>All other MinionMenu/MinionGui classes in the project are deprecated stubs
 * that delegate here.</p>
 */
public class MinionMenu extends Menu {

    private static final int INFO_SLOT    = 13;
    private static final int UPGRADE_SLOT = 15;
    private static final int CLOSE_SLOT   = 22;

    private final MinionData data;

    public MinionMenu(MinionData data) {
        super("§a" + data.type.getDisplayName(), 3);
        this.data = data;
    }

    @Override
    protected void build() {
        fillBorder();

        String tier = roman(data.getTier());
        setItem(INFO_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + data.type.getDisplayName() + " " + tier)
                .lore(
                        "§7Type: §e" + data.type.getDisplayName(),
                        "§7Tier: §e" + tier)
                .build());

        MinionTier[] tiers = MinionTier.values();
        int nextOrdinal = data.getTier().ordinal() + 1;
        if (nextOrdinal < tiers.length) {
            String nextTier = roman(tiers[nextOrdinal]);
            setItem(UPGRADE_SLOT, new ItemBuilder(Material.ANVIL)
                    .displayName("§aUpgrade Minion")
                    .lore(
                            "§7Current tier: §e" + tier,
                            "§7Next tier: §e" + nextTier,
                            "",
                            "§eClick to upgrade!")
                    .build(), event -> {
                MinionManager.getInstance().upgradeMinion(data.id);
                ((Player) event.getWhoClicked()).closeInventory();
                new MinionMenu(MinionManager.getInstance().getMinion(data.id) != null
                        ? MinionManager.getInstance().getMinion(data.id) : data)
                        .open((Player) event.getWhoClicked());
            });
        } else {
            setItem(UPGRADE_SLOT, new ItemBuilder(Material.ANVIL)
                    .displayName("§aUpgrade Minion")
                    .lore("§cAlready at maximum tier!")
                    .build());
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 27; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 18 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }

    private static String roman(MinionTier tier) {
        return SkyblockUtils.toRoman(tier.ordinal() + 1);
    }
}
