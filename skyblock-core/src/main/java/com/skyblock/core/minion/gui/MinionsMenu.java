package com.skyblock.core.minion.gui;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionType;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Canonical "My Minions" overview menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §aMy Minions}, framed by a gray glass
 * pane border. The 28 inner slots (rows 1–4) show each placed minion as a
 * {@link Material#DISPENSER} icon, grouped by {@link Category} (Mining, Farming,
 * Combat, Foraging, Fishing). Each icon's lore lists the minion's tier, the
 * upgrade installed in each of its two slots, and its active fuel. Clicking a
 * minion opens its {@link MinionMenu}. An empty state shows a
 * {@link Material#BARRIER} at slot 22.</p>
 *
 * <p>All other MinionsMenu/MinionGui-as-overview classes in the project are
 * deprecated stubs that delegate here.</p>
 */
public class MinionsMenu extends Menu {

    private static final int[] INNER_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    /** Broad category each minion type belongs to; controls grouping order. */
    private enum Category {
        MINING("Mining"),
        FARMING("Farming"),
        COMBAT("Combat"),
        FORAGING("Foraging"),
        FISHING("Fishing"),
        OTHER("Other");

        private final String label;

        Category(String label) {
            this.label = label;
        }
    }

    private final Player player;

    public MinionsMenu(Player player) {
        super("§aMy Minions", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(player.getUniqueId());

        if (minionIds.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Minions Placed")
                    .lore("§7Place minions in the world", "§7to see them here.")
                    .build());
            return;
        }

        // Resolve and group by category so same-category minions sit together.
        List<MinionData> minions = new ArrayList<>();
        for (UUID id : minionIds) {
            MinionData data = manager.getMinion(id);
            if (data != null) minions.add(data);
        }
        minions.sort(Comparator
                .comparingInt((MinionData d) -> categoryOf(d.type).ordinal())
                .thenComparing(d -> d.type.getDisplayName()));

        for (int i = 0; i < minions.size() && i < INNER_SLOTS.length; i++) {
            MinionData data = minions.get(i);
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.DISPENSER)
                    .displayName("§a" + data.type.getDisplayName())
                    .lore(
                            "§7Category: §e" + categoryOf(data.type).label,
                            "§7Tier: §e" + roman(data.getTier()),
                            "§7Fuel: §e" + pretty(data.getFuel()),
                            "§7Upgrade Slot 1: §e" + pretty(data.getUpgrade(0)),
                            "§7Upgrade Slot 2: §e" + pretty(data.getUpgrade(1)),
                            "",
                            "§eClick to manage!")
                    .build(),
                    event -> new MinionMenu(data).open(player));
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

    private static Category categoryOf(MinionType type) {
        switch (type) {
            case COBBLESTONE: case COAL: case IRON: case GOLD: case DIAMOND:
            case LAPIS: case REDSTONE: case EMERALD: case QUARTZ: case OBSIDIAN:
            case GLOWSTONE: case GRAVEL: case SAND: case ICE: case SNOW:
            case MITHRIL: case HARD_STONE: case GEMSTONE:
                return Category.MINING;
            case WHEAT: case CARROT: case POTATO: case PUMPKIN: case MELON:
            case MUSHROOM: case CACTUS: case SUGAR_CANE: case NETHER_WART:
            case FLOWER: case CHICKEN: case COW: case PIG: case SHEEP: case RABBIT:
                return Category.FARMING;
            case ZOMBIE: case SKELETON: case SPIDER: case CREEPER: case ENDERMAN:
            case GHAST: case SLIME: case BLAZE: case MAGMA_CUBE: case TARANTULA:
                return Category.COMBAT;
            case OAK: case BIRCH: case SPRUCE: case DARK_OAK: case JUNGLE:
            case ACACIA: case LOG:
                return Category.FORAGING;
            case FISHING: case CLAY:
                return Category.FISHING;
            default:
                return Category.OTHER;
        }
    }

    /** Renders an enum constant as a human-readable, title-cased label. */
    private static String pretty(Enum<?> value) {
        String[] parts = value.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    private static String roman(MinionManager.MinionTier tier) {
        return SkyblockUtils.toRoman(tier.ordinal() + 1);
    }
}
