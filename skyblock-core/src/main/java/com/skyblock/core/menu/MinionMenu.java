package com.skyblock.core.menu;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Canonical 54-slot Minion management menu. The top and bottom rows are
 * gray-pane borders; rows 1–2 hold the 12 minion slots (6 per row, bordered
 * left and right); row 3 is a gray-pane separator; row 4 is unused; slot 53
 * shows a slot-count summary. Occupied slots render as a colored-terracotta
 * item labeled with the minion's display name and key stats; empty unlocked
 * slots are light-gray panes; locked slots (beyond the player's current cap)
 * are red panes.
 */
public final class MinionMenu extends AbstractSkyBlockMenu {

    static final int[] MINION_SLOTS = {
            10, 11, 12, 13, 14, 15,
            19, 20, 21, 22, 23, 24
    };

    private static final Material[] TERRACOTTA_COLORS = {
            Material.WHITE_TERRACOTTA,
            Material.ORANGE_TERRACOTTA,
            Material.MAGENTA_TERRACOTTA,
            Material.LIGHT_BLUE_TERRACOTTA,
            Material.YELLOW_TERRACOTTA,
            Material.LIME_TERRACOTTA,
            Material.PINK_TERRACOTTA,
            Material.GRAY_TERRACOTTA,
            Material.LIGHT_GRAY_TERRACOTTA,
            Material.CYAN_TERRACOTTA,
            Material.PURPLE_TERRACOTTA,
            Material.BLUE_TERRACOTTA,
            Material.BROWN_TERRACOTTA,
            Material.GREEN_TERRACOTTA,
            Material.RED_TERRACOTTA,
            Material.BLACK_TERRACOTTA
    };

    public MinionMenu(Player player) {
        super(player, "§6§lYour Minions", 6);
    }

    @Override
    protected void populate() {
        UUID owner = player.getUniqueId();
        ItemStack pane = SkyblockUtils.buildItem(Material.ORANGE_STAINED_GLASS_PANE, "§r");

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        setItem(9, pane);
        setItem(16, pane);
        setItem(17, pane);
        setItem(18, pane);
        setItem(25, pane);
        setItem(26, pane);
        for (int slot = 27; slot < 36; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 53; slot++) setItem(slot, pane);

        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(owner);
        int maxSlots = manager.getMaxSlots(owner);

        for (int i = 0; i < MINION_SLOTS.length; i++) {
            int slot = MINION_SLOTS[i];
            if (i < minionIds.size()) {
                UUID minionId = minionIds.get(i);
                MinionData data = manager.getMinion(minionId);
                if (data != null) {
                    Material mat = TERRACOTTA_COLORS[data.type.ordinal() % TERRACOTTA_COLORS.length];
                    setItem(slot, SkyblockUtils.buildItem(mat,
                            "§a" + data.type.getDisplayName(),
                            "§7Tier: §e" + (data.getTier().ordinal() + 1),
                            "§7Stored: §f" + data.getStoredResources(),
                            "§7Fuel: §f" + data.getFuel().name().replace('_', ' '),
                            "",
                            "§eClick to collect!"),
                            e -> collect(manager, minionId, data.type));
                }
            } else if (i < maxSlots) {
                setItem(slot, SkyblockUtils.buildItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                        "§7Empty Minion Slot",
                        "§7Place a minion to fill this slot."));
            } else {
                setItem(slot, SkyblockUtils.buildItem(Material.RED_STAINED_GLASS_PANE,
                        "§cLocked Slot",
                        "§7Upgrade your island to unlock more slots."));
            }
        }

        setItem(53, SkyblockUtils.buildItem(Material.PAPER,
                "§fMinion Slots",
                "§7Used: §e" + minionIds.size() + " §7/ §e" + maxSlots));
    }

    /** Empties a minion's storage into the player's inventory as its real resource. */
    private void collect(MinionManager manager, UUID minionId, MinionManager.MinionType type) {
        int amount = manager.collectResources(minionId);
        if (amount <= 0) {
            player.sendMessage("§7That minion has nothing to collect yet.");
            return;
        }
        Material resource = MinionManager.resourceOf(type);
        int remaining = amount;
        while (remaining > 0) {
            int stack = Math.min(remaining, resource.getMaxStackSize());
            for (ItemStack leftover : player.getInventory().addItem(new ItemStack(resource, stack)).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            remaining -= stack;
        }
        player.sendMessage("§aCollected §e" + amount + "x §a"
                + type.getDisplayName().replace(" Minion", "") + "§a!");
        open(player);
    }
}
