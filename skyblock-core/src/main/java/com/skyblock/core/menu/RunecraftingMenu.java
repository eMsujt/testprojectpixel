package com.skyblock.core.menu;

import com.skyblock.core.item.RuneItem;
import com.skyblock.core.item.RuneItem.RuneRef;
import com.skyblock.core.manager.RuneManager.RuneType;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The Runic Pedestal, matching the wiki {@code {{UI|Runic Pedestal}}}: place a
 * target item in the left slot (11) and a rune in the right sacrifice slot (15),
 * then click the End Portal (13) to either <em>apply</em> the rune to the item
 * or <em>fuse</em> two matching runes into the next level. The Cauldron (44)
 * removes a rune from the item in the left slot.
 *
 * <p>Dupe-safe like {@link ReforgeMenu}: only the two input slots are
 * interactive, they are cleared after the background fill, and {@link #onClose}
 * returns anything left in them.</p>
 */
public final class RunecraftingMenu extends AbstractSkyBlockMenu {

    static final int TARGET_SLOT = 11;    // 2,3 — Item To Upgrade
    static final int APPLY_SLOT = 13;     // 2,5 — End Portal
    static final int SACRIFICE_SLOT = 15; // 2,7 — Rune to Sacrifice
    static final int INFO_SLOT = 31;      // 4,5 — Barrier instructions
    static final int REMOVAL_SLOT = 44;   // 5,9 — Cauldron, Rune Removal

    public RunecraftingMenu(Player player) {
        super(player, "Runic Pedestal", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        ItemStack left = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).displayName("§6Item To Upgrade").build();
        setItem(10, left);
        setItem(12, left);
        ItemStack right = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).displayName("§6Rune to Sacrifice").build();
        setItem(14, right);
        setItem(16, right);

        // TARGET_SLOT and SACRIFICE_SLOT are interactive — left empty (re-cleared in open()).
        setItem(TARGET_SLOT, null);
        setItem(SACRIFICE_SLOT, null);

        setItem(APPLY_SLOT, new ItemBuilder(Material.END_PORTAL_FRAME)
                .displayName("§aApply a Rune or Fuse Two Runes")
                .lore("§7Place a target item on the left and",
                        "§7a rune on the right, then click here.",
                        "",
                        "§7Two matching runes fuse into the",
                        "§7next level instead.")
                .build());

        setItem(INFO_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cRunic Pedestal")
                .lore("§7Place a target item in the left slot",
                        "§7and a sacrifice rune in the right slot",
                        "§7to add the rune's effects to the item,",
                        "§7or add two runes to fuse them.")
                .build());

        setItem(REMOVAL_SLOT, new ItemBuilder(Material.CAULDRON)
                .displayName("§eRune Removal")
                .lore("§7Click to remove the rune from the",
                        "§7item in the left slot.")
                .build());
    }

    @Override
    public void open(Player viewer) {
        super.open(viewer);
        getInventory().setItem(TARGET_SLOT, null);
        getInventory().setItem(SACRIFICE_SLOT, null);
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        return slot == TARGET_SLOT || slot == SACRIFICE_SLOT;
    }

    @Override
    public void onClose(Player viewer) {
        returnItem(viewer, TARGET_SLOT);
        returnItem(viewer, SACRIFICE_SLOT);
    }

    private void returnItem(Player viewer, int slot) {
        ItemStack item = getInventory().getItem(slot);
        if (item != null && item.getType() != Material.AIR) {
            for (ItemStack overflow : viewer.getInventory().addItem(item).values()) {
                viewer.getWorld().dropItemNaturally(viewer.getLocation(), overflow);
            }
            getInventory().setItem(slot, null);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == TARGET_SLOT || slot == SACRIFICE_SLOT) {
            return; // interactive — allow place/pickup
        }
        event.setCancelled(true);
        if (slot == APPLY_SLOT) {
            doApplyOrFuse();
        } else if (slot == REMOVAL_SLOT) {
            doRemoval();
        }
    }

    private void doApplyOrFuse() {
        ItemStack target = getInventory().getItem(TARGET_SLOT);
        ItemStack sacrifice = getInventory().getItem(SACRIFICE_SLOT);

        RuneRef sacrificeRune = RuneItem.readRuneItem(sacrifice);
        if (sacrificeRune == null) {
            player.sendMessage("§cPlace a rune in the right (sacrifice) slot.");
            return;
        }
        if (target == null || target.getType() == Material.AIR
                || target.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            player.sendMessage("§cPlace an item or a rune in the left slot.");
            return;
        }

        RuneRef targetRune = RuneItem.readRuneItem(target);
        if (targetRune != null) {
            // Fuse two runes of the same type into the next level.
            if (targetRune.type() != sacrificeRune.type()) {
                player.sendMessage("§cYou can only fuse two runes of the same type.");
                return;
            }
            RuneType type = targetRune.type();
            int newLevel = Math.max(targetRune.level(), sacrificeRune.level()) + 1;
            if (newLevel > type.getMaxLevel()) {
                player.sendMessage("§e" + type.getDisplayName() + " Rune is already at its maximum level.");
                return;
            }
            // Consume one rune from each slot and hand the player the fused result,
            // so stacks in either slot are never overwritten.
            consumeOne(TARGET_SLOT);
            consumeOne(SACRIFICE_SLOT);
            ItemStack fused = RuneItem.createRuneItem(type, newLevel);
            for (ItemStack overflow : player.getInventory().addItem(fused).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), overflow);
            }
            player.sendMessage("§aFused into §r" + type.getDisplayName() + " Rune " + SkyblockUtils.toRoman(newLevel) + "§a!");
            return;
        }

        // Apply the rune to the target item.
        RuneItem.applyRuneToItem(target, sacrificeRune.type(), sacrificeRune.level());
        getInventory().setItem(TARGET_SLOT, target);
        consumeOne(SACRIFICE_SLOT);
        player.sendMessage("§aApplied §d" + sacrificeRune.type().getDisplayName() + " Rune "
                + SkyblockUtils.toRoman(sacrificeRune.level()) + " §ato your item.");
    }

    private void doRemoval() {
        ItemStack target = getInventory().getItem(TARGET_SLOT);
        if (target == null || target.getType() == Material.AIR) {
            player.sendMessage("§cPlace an item with a rune in the left slot.");
            return;
        }
        RuneRef removed = RuneItem.removeItemRune(target);
        if (removed == null) {
            player.sendMessage("§7That item has no rune to remove.");
            return;
        }
        getInventory().setItem(TARGET_SLOT, target);
        player.sendMessage("§aRemoved §d" + removed.type().getDisplayName() + " Rune §afrom your item.");
    }

    /** Decrements the stack in {@code slot} by one (clearing it at zero). */
    private void consumeOne(int slot) {
        ItemStack item = getInventory().getItem(slot);
        if (item == null) {
            return;
        }
        int amount = item.getAmount();
        if (amount <= 1) {
            getInventory().setItem(slot, null);
        } else {
            item.setAmount(amount - 1);
            getInventory().setItem(slot, item);
        }
    }
}
