package com.skyblock.core.menu;

import com.skyblock.core.manager.CrystalHollowsManager;
import com.skyblock.core.manager.CrystalHollowsManager.CrystalType;
import com.skyblock.core.manager.CrystalHollowsManager.PowderType;
import com.skyblock.core.util.SkyblockUtil.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Crystal Hollows overview menu. Row 3 (slots 20–24) shows the five
 * nucleus crystals as LIME_STAINED_GLASS_PANE (placed) or RED_STAINED_GLASS_PANE
 * (not yet placed). Row 4 (slots 29, 31, 33) shows the player's three powder
 * balances. Slot 49 summarises nucleus completion status.
 */
public final class CrystalHollowsMenu extends Menu {

    static final int[] CRYSTAL_SLOTS = {20, 21, 22, 23, 24};

    private static final int[] POWDER_SLOTS = {29, 31, 33};

    private static final int SUMMARY_SLOT = 49;

    private static final CrystalType[] NUCLEUS_ORDER = {
            CrystalType.JADE,
            CrystalType.AMBER,
            CrystalType.TOPAZ,
            CrystalType.SAPPHIRE,
            CrystalType.AMETHYST
    };

    private static final PowderType[] POWDER_ORDER = {
            PowderType.MITHRIL,
            PowderType.GEMSTONE,
            PowderType.GLACITE
    };

    private static final Material[] POWDER_ICONS = {
            Material.PRISMARINE_CRYSTALS,
            Material.AMETHYST_SHARD,
            Material.BLUE_ICE
    };

    private final UUID playerId;

    public CrystalHollowsMenu(UUID playerId) {
        super("§5Crystal Hollows", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        setItem(18, pane); setItem(19, pane);
        setItem(25, pane); setItem(26, pane);

        CrystalHollowsManager manager = CrystalHollowsManager.getInstance();

        for (int i = 0; i < NUCLEUS_ORDER.length; i++) {
            CrystalType crystal = NUCLEUS_ORDER[i];
            boolean placed = manager.isCrystalPlaced(playerId, crystal);
            int collected = manager.getCrystalCount(playerId, crystal);
            Material icon = placed ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;

            setItem(CRYSTAL_SLOTS[i], new ItemBuilder(icon)
                    .displayName("§5" + crystal.getDisplayName())
                    .lore(
                            "§7Collected: §e" + collected,
                            placed
                                ? "§aPlaced in nucleus"
                                : "§cNot placed",
                            crystal.getBuff() != null ? "§7Buff: §b" + crystal.getBuff() : "§r")
                    .build());
        }

        for (int i = 0; i < POWDER_ORDER.length; i++) {
            PowderType powder = POWDER_ORDER[i];
            long balance = manager.getPowder(playerId, powder);

            setItem(POWDER_SLOTS[i], new ItemBuilder(POWDER_ICONS[i])
                    .displayName("§5" + powder.getDisplayName())
                    .lore("§7Balance: §e" + balance)
                    .build());
        }

        boolean complete = manager.isNucleusComplete(playerId);
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§5Crystal Nucleus")
                .lore(
                        complete
                            ? "§aNucleus fully assembled!"
                            : "§cNucleus incomplete",
                        "§7Place all 5 crystals to",
                        "§7activate all buffs.")
                .build());
    }
}
