package com.skyblock.core.menu;

import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /essence}. Renders each {@link EssenceType}'s current
 * balance in the top section and a per-perk upgrade list below; clicking a perk
 * purchases its next level via {@link EssenceManager#purchasePerk}, deducting the
 * matching essence balance.
 */
public final class EssenceMenu extends Menu {

    /** Inner slots for the essence-balance tiles (one row of eight). */
    private static final int[] BALANCE_SLOTS = {10, 11, 12, 13, 14, 15, 16, 17};

    /** Inner slots for the perk upgrade tiles. */
    private static final int[] PERK_SLOTS = {29, 30, 31, 32, 33, 34};

    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public EssenceMenu(Player player) {
        this(player.getUniqueId());
    }

    public EssenceMenu(UUID playerId) {
        super("§dEssence Shop", 6);
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

        EssenceManager manager = EssenceManager.getInstance();

        EssenceType[] types = EssenceType.values();
        for (int i = 0; i < BALANCE_SLOTS.length && i < types.length; i++) {
            EssenceType type = types[i];
            inventory.setItem(BALANCE_SLOTS[i], new ItemBuilder(Material.GLOWSTONE_DUST)
                    .displayName("§d" + type.getDisplayName() + " Essence")
                    .lore("§7Balance: §a" + manager.getBalance(playerId, type))
                    .build());
        }

        EssenceShopPerk[] perks = EssenceShopPerk.values();
        for (int i = 0; i < PERK_SLOTS.length && i < perks.length; i++) {
            EssenceShopPerk perk = perks[i];
            int slot = PERK_SLOTS[i];
            int level = manager.getPerkLevel(playerId, perk);
            boolean maxed = level >= perk.getMaxLevel();
            int balance = manager.getBalance(playerId, perk.getEssenceType());

            if (maxed) {
                inventory.setItem(slot, new ItemBuilder(Material.ENCHANTED_BOOK)
                        .displayName("§a" + perk.getDisplayName())
                        .lore(
                                "§7Level: §e" + level + "§7/§e" + perk.getMaxLevel(),
                                "§aMaxed out!")
                        .build());
                continue;
            }

            int cost = perk.getUpgradeCost(level);
            boolean affordable = balance >= cost;
            inventory.setItem(slot, new ItemBuilder(Material.BOOK)
                    .displayName("§d" + perk.getDisplayName())
                    .lore(
                            "§7Level: §e" + level + "§7/§e" + perk.getMaxLevel(),
                            "§7Cost: " + (affordable ? "§a" : "§c") + cost + " "
                                    + perk.getEssenceType().getDisplayName() + " Essence",
                            "",
                            affordable ? "§eClick to upgrade!" : "§cInsufficient essence")
                    .build());
            handlers.put(slot, e -> {
                if (manager.purchasePerk(playerId, perk)) {
                    player.sendMessage("§aUpgraded §d" + perk.getDisplayName()
                            + " §ato level §e" + manager.getPerkLevel(playerId, perk) + "§a.");
                } else {
                    player.sendMessage("§cYou cannot upgrade " + perk.getDisplayName() + " right now.");
                }
                open(player);
            });
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the essence shop.")
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
