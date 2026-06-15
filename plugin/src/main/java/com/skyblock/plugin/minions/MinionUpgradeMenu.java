package com.skyblock.plugin.minions;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.minions.MinionManager.MinionData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Upgrade GUI for a single placed minion.
 *
 * <p>A 27-slot chest menu titled {@code §a<Type>} with a gray-glass border.
 * Slot 13 shows the minion's info (current tier and the resource it produces)
 * and slot 15 upgrades the minion to the next tier, up to {@link #MAX_TIER}.
 * Because {@link MinionData} is immutable, an upgrade replaces the tracked
 * minion in {@link MinionManager} with a higher-tier copy.</p>
 */
public class MinionUpgradeMenu extends Menu {

    /** The highest tier a minion can be upgraded to. */
    private static final int MAX_TIER = 11;

    private static final int INFO_SLOT = 13;
    private static final int UPGRADE_SLOT = 15;

    private final Player player;
    private MinionData minion;

    public MinionUpgradeMenu(Player player, MinionData minion) {
        super("§a" + minion.type(), 3);
        this.player = player;
        this.minion = minion;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(INFO_SLOT, new ItemBuilder(iconFor(minion.type()))
                .displayName("§a" + minion.type())
                .lore(
                        "§7Tier: §e" + minion.tier(),
                        "§7Produces: §e" + CobblestoneMinion.RESOURCE.name())
                .build());

        setItem(UPGRADE_SLOT, new ItemBuilder(Material.ANVIL)
                .displayName("§aUpgrade Minion")
                .lore("§7Current tier: §e" + minion.tier())
                .build(), event -> {
            if (minion.tier() >= MAX_TIER) {
                event.getWhoClicked().sendMessage("§cThis minion is already at the maximum tier.");
                return;
            }
            MinionData upgraded = new MinionData(
                    minion.owner(), minion.loc(), minion.type(), minion.tier() + 1);
            MinionManager manager = MinionManager.getInstance();
            manager.removeMinion(minion);
            manager.addMinion(upgraded);
            minion = upgraded;
            open(player);
        });
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 27; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 18 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }

    /** Resolves an icon material for the minion's resource, falling back to a player head. */
    private static Material iconFor(String type) {
        Material material = Material.matchMaterial(type.replace(' ', '_').toUpperCase());
        return material != null ? material : Material.PLAYER_HEAD;
    }
}
