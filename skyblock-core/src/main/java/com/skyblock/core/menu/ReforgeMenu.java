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
 * The Blacksmith "Reforge Item" menu, laid out 1:1 with the wiki {@code Blacksmith/UI}:
 * a 5-row GUI with red border columns, an empty input slot at 13, the Anvil
 * "Reforge Item" button directly below it at 22, and a Close button at 40. The
 * player drops an item in slot 13 and clicks the anvil to apply a random reforge
 * for coins — the reforged item stays in slot 13 (there is no separate result slot).
 */
public final class ReforgeMenu extends AbstractSkyBlockMenu {

    static final int ITEM_SLOT    = 13;
    static final int REFORGE_SLOT = 22;
    static final int CLOSE_SLOT   = 40;

    /** Left/right border columns (1 and 9), filled with red panes per the wiki. */
    private static final int[] BORDER_SLOTS = {0, 9, 18, 27, 36, 8, 17, 26, 35, 44};

    private static final ReforgeManager.ReforgeType[] REFORGE_TYPES;

    static {
        ReforgeManager.ReforgeType[] all = ReforgeManager.ReforgeType.values();
        // skip NONE (index 0)
        REFORGE_TYPES = new ReforgeManager.ReforgeType[all.length - 1];
        System.arraycopy(all, 1, REFORGE_TYPES, 0, REFORGE_TYPES.length);
    }

    public ReforgeMenu(Player player) {
        super(player, "Reforge Item", 5);
    }

    @Override
    protected void populate() {
        ItemStack black = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        ItemStack red = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).displayName(" ").build();
        for (int slot = 0; slot < 45; slot++) {
            if (slot != ITEM_SLOT && slot != REFORGE_SLOT && slot != CLOSE_SLOT) {
                setItem(slot, black);
            }
        }
        for (int slot : BORDER_SLOTS) {
            setItem(slot, red);
        }

        // ITEM_SLOT is interactive — cleared after open() so the player can drop an item in.
        setItem(REFORGE_SLOT, new ItemBuilder(Material.ANVIL)
                .displayName("§eReforge Item")
                .lore("§7Place an item above to reforge",
                      "§7it! Reforging items adds a",
                      "§7random modifier to the item that",
                      "§7grants stat boosts.",
                      "",
                      "§eClick to reforge!")
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build());
    }

    @Override
    public void open(Player player) {
        super.open(player);
        getInventory().setItem(ITEM_SLOT, null);
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        return slot == ITEM_SLOT;
    }

    @Override
    public void onClose(Player player) {
        ItemStack item = getInventory().getItem(ITEM_SLOT);
        if (item != null && item.getType() != Material.AIR) {
            for (ItemStack overflow : player.getInventory().addItem(item).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), overflow);
            }
            getInventory().setItem(ITEM_SLOT, null);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == ITEM_SLOT) {
            // Let the player freely place / take the item being reforged.
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        if (slot == REFORGE_SLOT) {
            ItemStack item = getInventory().getItem(ITEM_SLOT);
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("§cPlace an item above the anvil to reforge it.");
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
            // Stamp the reforge onto the item in place (PDC + renamed), so its stats follow the item
            // and reach combat via EquipmentListener.recompute once it's equipped/held.
            mgr.applyReforge(item, chosen);
            getInventory().setItem(ITEM_SLOT, item);
            player.sendMessage("§aReforged to §6" + chosen.getDisplayName()
                    + " §afor §6" + String.format("%,d", cost) + " coins§a! §7(+"
                    + chosen.getStrengthBonus() + "❁ +" + chosen.getDefenseBonus()
                    + "❈ +" + chosen.getSpeedBonus() + "✦)");
        }
    }
}
