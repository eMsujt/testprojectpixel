package com.skyblock.plugin.reforge;

import com.skyblock.items.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.plugin.items.SkyBlockItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Reforge Anvil menu.
 *
 * <p>A 54-slot (6-row) menu listing every reforge registered with
 * {@link ReforgeManager}. Each reforge renders as an {@link Material#ANVIL}
 * icon in the inner slots, its lore summarising the stat bonuses it grants.</p>
 */
public class ReforgeAnvilMenu extends Menu {

    private final ReforgeManager reforgeManager;

    /** Creates a reforge anvil menu backed by the shared registry. */
    public ReforgeAnvilMenu() {
        this(ReforgeManager.getInstance());
    }

    /**
     * Creates a reforge anvil menu backed by the given registry.
     *
     * @param reforgeManager the source of available reforges
     */
    public ReforgeAnvilMenu(ReforgeManager reforgeManager) {
        super("§5Reforge Anvil", 6);
        this.reforgeManager = reforgeManager;
    }

    @Override
    protected void build() {
        fillBorder();

        int slot = 10;
        for (ReforgeManager.Reforge reforge : reforgeManager.getReforges().values()) {
            // Skip the border columns, wrapping to the next row's inner slots.
            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 45) {
                break;
            }
            setItem(slot, new ItemBuilder(Material.ANVIL)
                    .displayName("§d" + reforge.displayName())
                    .lore(statLore(reforge.statBlock()))
                    .build());
            slot++;
        }
    }

    /** Builds the lore lines describing a reforge's non-zero stat bonuses. */
    private List<String> statLore(SkyBlockItem.StatBlock stats) {
        List<String> lore = new ArrayList<>();
        addStat(lore, "§cHealth", stats.health());
        addStat(lore, "§aDefense", stats.defense());
        addStat(lore, "§cStrength", stats.strength());
        addStat(lore, "§bIntelligence", stats.intelligence());
        addStat(lore, "§9Crit Chance", stats.critChance());
        addStat(lore, "§9Crit Damage", stats.critDamage());
        addStat(lore, "§fSpeed", stats.speed());
        if (lore.isEmpty()) {
            lore.add("§7No stat bonuses.");
        }
        return lore;
    }

    /** Appends a stat line if {@code value} is non-zero. */
    private void addStat(List<String> lore, String label, int value) {
        if (value != 0) {
            lore.add(label + ": §a" + (value > 0 ? "+" : "") + value);
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
