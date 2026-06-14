package com.skyblock.plugin.minions;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.minions.MinionManager.MinionTier;
import com.skyblock.plugin.minions.MinionManager.PlacedMinion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Management GUI for a single placed minion.
 *
 * <p>A 27-slot chest menu titled {@code §a<Type> Minion} with a gray-glass
 * border. Slot 13 shows the minion's info (tier, speed and stored resources),
 * slot 11 collects everything the minion has produced and slot 15 upgrades the
 * minion to the next tier.</p>
 */
public class MinionMenu extends Menu {

    private static final int INFO_SLOT = 13;
    private static final int COLLECT_SLOT = 11;
    private static final int UPGRADE_SLOT = 15;

    private final PlacedMinion minion;

    public MinionMenu(PlacedMinion minion) {
        super("§a" + minion.getType().getDisplayName(), 3);
        this.minion = minion;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(INFO_SLOT, new ItemBuilder(iconFor(minion.getType()))
                .displayName("§a" + minion.getType().getDisplayName())
                .lore(
                        "§7Tier: §e" + (minion.getTier().ordinal() + 1),
                        "§7Speed: §eone every " + minion.getTier().getIntervalSeconds() + "s",
                        "§7Stored: §e" + minion.getStored())
                .build());

        setItem(COLLECT_SLOT, new ItemBuilder(Material.CHEST)
                .displayName("§aCollect All")
                .lore("§7Stored: §e" + minion.getStored())
                .build(), event -> {
            int collected = minion.collect();
            event.getWhoClicked().sendMessage("§aCollected §e" + collected + " §aresources.");
            ((Player) event.getWhoClicked()).closeInventory();
        });

        setItem(UPGRADE_SLOT, new ItemBuilder(Material.ANVIL)
                .displayName("§aUpgrade Minion")
                .lore("§7Current tier: §e" + (minion.getTier().ordinal() + 1))
                .build(), event -> {
            MinionTier[] tiers = MinionTier.values();
            int next = minion.getTier().ordinal() + 1;
            if (next >= tiers.length) {
                event.getWhoClicked().sendMessage("§cThis minion is already at the maximum tier.");
                return;
            }
            minion.setTier(tiers[next]);
            open((Player) event.getWhoClicked());
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

    /** Resolves an icon material for the minion type, falling back to a player head. */
    private static Material iconFor(MinionManager.MinionType type) {
        Material material = Material.matchMaterial(type.name());
        return material != null ? material : Material.PLAYER_HEAD;
    }
}
