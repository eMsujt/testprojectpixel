package com.skyblock.core.menu;

import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 3-row GUI where a player drops the item to reforge in slot 11,
 * clicks the anvil at slot 13 to apply a random reforge, and collects
 * the result from slot 15.
 */
public final class ReforgeMenu extends AbstractSkyBlockMenu {

    static final int ITEM_SLOT   = 11;
    static final int REFORGE_SLOT = 13;
    static final int RESULT_SLOT  = 15;

    private static final ReforgeManager.ReforgeType[] REFORGE_TYPES;

    static {
        ReforgeManager.ReforgeType[] all = ReforgeManager.ReforgeType.values();
        // skip NONE (index 0)
        REFORGE_TYPES = new ReforgeManager.ReforgeType[all.length - 1];
        System.arraycopy(all, 1, REFORGE_TYPES, 0, REFORGE_TYPES.length);
    }

    public ReforgeMenu(Player player) {
        super(player, "§5Reforge Anvil", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            if (slot != ITEM_SLOT && slot != REFORGE_SLOT && slot != RESULT_SLOT) {
                setItem(slot, pane);
            }
        }

        // ITEM_SLOT (place) and RESULT_SLOT (collect) are interactive — left empty
        // after open() so the player can drop an item in and take the reforged result.
        setItem(REFORGE_SLOT, new ItemBuilder(Material.ANVIL)
                .displayName("§6Reforge Item")
                .lore("§7Place an item in the left slot,",
                      "§7then click here to apply a",
                      "§7random reforge to it.",
                      "",
                      "§7Cost scales with the item's rarity.",
                      "§7Collect the result from the",
                      "§7right slot!")
                .build());
    }

    @Override
    public void open(Player player) {
        super.open(player);
        getInventory().setItem(ITEM_SLOT, null);
        getInventory().setItem(RESULT_SLOT, null);
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        return slot == ITEM_SLOT || slot == RESULT_SLOT;
    }

    @Override
    public void onClose(Player player) {
        returnItem(player, ITEM_SLOT);
        returnItem(player, RESULT_SLOT);
    }

    private void returnItem(Player player, int slot) {
        ItemStack item = getInventory().getItem(slot);
        if (item != null && item.getType() != Material.AIR) {
            for (ItemStack overflow : player.getInventory().addItem(item).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), overflow);
            }
            getInventory().setItem(slot, null);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == ITEM_SLOT || slot == RESULT_SLOT) {
            // allow the player to freely place / take the item being reforged or collected
            return;
        }
        event.setCancelled(true);
        if (slot == REFORGE_SLOT && event.getWhoClicked() instanceof Player player) {
            ItemStack item = getInventory().getItem(ITEM_SLOT);
            if (item == null || item.getType() == Material.AIR
                    || item.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                player.sendMessage("§cPlace an item in the left slot first.");
                return;
            }
            ItemStack pending = getInventory().getItem(RESULT_SLOT);
            if (pending != null && pending.getType() != Material.AIR) {
                player.sendMessage("§cTake your reforged item first.");
                return;
            }
            // Reforging costs coins, scaling with the item's rarity (wiki Reforging/Prices).
            Rarity rarity = Rarity.fromItem(item, Rarity.COMMON);
            int cost = ReforgeManager.getReforgeCost(rarity);
            if (!EconomyManager.getInstance().withdraw(player.getUniqueId(), (long) cost)) {
                player.sendMessage("§cReforging a " + rarity.getDisplayName() + " item costs §6"
                        + String.format("%,d", cost) + " coins§c — you can't afford it.");
                return;
            }
            ReforgeManager mgr = ReforgeManager.getInstance();
            ReforgeManager.ReforgeType chosen =
                    REFORGE_TYPES[(int) (Math.random() * REFORGE_TYPES.length)];
            // Stamp the reforge onto the item itself (PDC + renamed), so its stats follow the item
            // and reach combat via EquipmentListener.recompute once it's equipped/held.
            ItemStack reforged = item.clone();
            mgr.applyReforge(reforged, chosen);
            getInventory().setItem(RESULT_SLOT, reforged);
            getInventory().setItem(ITEM_SLOT, null);
            player.sendMessage("§aReforged to §6" + chosen.getDisplayName()
                    + " §afor §6" + String.format("%,d", cost) + " coins§a! §7(+"
                    + chosen.getStrengthBonus() + "❁ +" + chosen.getDefenseBonus()
                    + "❈ +" + chosen.getSpeedBonus() + "✦)");
        }
    }
}
