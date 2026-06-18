package com.skyblock.core.menu;

import com.skyblock.core.manager.HotmManager;
import com.skyblock.core.manager.HotmManager.HotmPerk;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /hotm}. Renders the Heart of the Mountain tree as a
 * grid of clickable perk nodes read from {@link HotmManager}: each node shows its
 * current level, max level, and next-level Mithril Powder cost. Clicking a node
 * attempts to purchase the next level and refreshes the menu. A summary node shows
 * the player's HOTM tier and powder balances.
 */
public final class HotmMenu extends Menu {

    private static final int SUMMARY_SLOT = 4;
    private static final int FIRST_PERK_SLOT = 9;
    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public HotmMenu(Player player) {
        this(player.getUniqueId());
    }

    public HotmMenu(UUID playerId) {
        super("§2Heart of the Mountain", 6);
        this.playerId = playerId;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();
        inventory = Bukkit.createInventory(this, 54, getTitle());

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) inventory.setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) inventory.setItem(slot, pane);

        HotmManager hotm = HotmManager.getInstance();

        inventory.setItem(SUMMARY_SLOT, new ItemBuilder(Material.BEACON)
                .displayName("§aHeart of the Mountain")
                .lore(
                        "§7HOTM Tier: §e" + hotm.getHotmTier(playerId) + "§7/§e" + HotmManager.MAX_TIER,
                        "§7Mithril Powder: §e" + hotm.getMithrilPowder(playerId),
                        "§7Gemstone Powder: §e" + hotm.getGemstonePowder(playerId))
                .build());

        HotmPerk[] perks = HotmPerk.values();
        for (int i = 0; i < perks.length; i++) {
            HotmPerk perk = perks[i];
            int slot = FIRST_PERK_SLOT + i;
            if (slot >= CLOSE_SLOT) break;
            int level = hotm.getLevel(playerId, perk);
            boolean maxed = level >= perk.maxLevel;
            int cost = hotm.getUpgradeCost(perk, level);

            List<String> lore = new ArrayList<>();
            lore.add("§7Level: §e" + level + "§7/§e" + perk.maxLevel);
            if (maxed) {
                lore.add("§aMaxed out!");
            } else if (cost >= 0) {
                lore.add("§7Next level: §b" + cost + " Mithril Powder");
                lore.add("§eClick to upgrade!");
            } else {
                lore.add("§eClick to upgrade!");
            }

            ItemStack icon = new ItemBuilder(maxed ? Material.LIME_DYE : Material.GRAY_DYE)
                    .displayName((maxed ? "§a" : "§e") + perk.getDisplayName())
                    .lore(lore.toArray(new String[0]))
                    .build();
            inventory.setItem(slot, icon);
            handlers.put(slot, e -> {
                hotm.purchaseUpgrade(playerId, perk);
                open(player);
            });
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the Heart of the Mountain menu.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
