package com.skyblock.core.menu;

import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import com.skyblock.core.util.ItemBuilder;
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
 * Canonical 54-slot Essence menu. Gray-pane border on all four edges; one tile
 * per {@link EssenceType} in the top rows showing the player's current balance,
 * and one tile per {@link EssenceShopPerk} below showing its level and next
 * upgrade cost. Clicking a perk tile purchases the next level, deducting the
 * cost from the player's matching essence balance.
 *
 * <p>Mirrors {@link BankMenu}: the menu manages its own inventory via
 * {@link #open(Player)} and routes clicks through {@link #handleClick}.</p>
 */
public final class EssenceMenu extends Menu {

    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public EssenceMenu(Player player) {
        this(player.getUniqueId());
    }

    public EssenceMenu(UUID playerId) {
        super("§dEssence", 6);
        this.playerId = playerId;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();

        EssenceManager essence = EssenceManager.getInstance();
        inventory = org.bukkit.Bukkit.createInventory(this, 54, "§dEssence");

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        List<Integer> inner = new ArrayList<>();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            } else {
                inner.add(slot);
            }
        }

        // Top rows: one balance tile per essence type.
        EssenceType[] types = EssenceType.values();
        for (int i = 0; i < types.length; i++) {
            EssenceType type = types[i];
            int balance = essence.getBalance(playerId, type);
            inventory.setItem(inner.get(i), new ItemBuilder(materialFor(type))
                    .displayName("§d" + type.getDisplayName() + " Essence")
                    .lore("§7Balance: §d" + String.format("%,d", balance))
                    .build());
        }

        // Lower rows: one upgrade tile per essence-shop perk.
        EssenceShopPerk[] perks = EssenceShopPerk.values();
        for (int i = 0; i < perks.length; i++) {
            EssenceShopPerk perk = perks[i];
            int slot = inner.get(14 + i);
            inventory.setItem(slot, perkItem(essence, perk));
            handlers.put(slot, e -> {
                if (essence.purchasePerk(playerId, perk)) {
                    player.sendMessage("§aUpgraded §d" + perk.getDisplayName()
                            + " §ato level " + essence.getPerkLevel(playerId, perk) + ".");
                } else if (essence.getPerkLevel(playerId, perk) >= perk.getMaxLevel()) {
                    player.sendMessage("§e" + perk.getDisplayName() + " is already maxed.");
                } else {
                    player.sendMessage("§cNot enough " + perk.getEssenceType().getDisplayName()
                            + " Essence to upgrade " + perk.getDisplayName() + ".");
                }
                open(player);
            });
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the essence menu.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    private ItemStack perkItem(EssenceManager essence, EssenceShopPerk perk) {
        int level = essence.getPerkLevel(playerId, perk);
        ItemBuilder builder = new ItemBuilder(Material.ENCHANTED_BOOK)
                .displayName("§d" + perk.getDisplayName())
                .addLore("§7Level: §a" + level + "§7/§a" + perk.getMaxLevel())
                .addLore("§7Essence: §d" + perk.getEssenceType().getDisplayName());
        if (level >= perk.getMaxLevel()) {
            builder.addLore("§eMaxed");
        } else {
            builder.addLore("§7Upgrade cost: §d" + String.format("%,d", perk.getUpgradeCost(level)));
            builder.addLore("§eClick to upgrade");
        }
        return builder.build();
    }

    /** Returns a representative icon material for the given essence type. */
    private static Material materialFor(EssenceType type) {
        return switch (type) {
            case WITHER  -> Material.COAL_BLOCK;
            case SPIDER  -> Material.COBWEB;
            case DRAGON  -> Material.DRAGON_BREATH;
            case GOLD    -> Material.GOLD_INGOT;
            case DIAMOND -> Material.DIAMOND;
            case ICE     -> Material.PACKED_ICE;
            case UNDEAD  -> Material.ROTTEN_FLESH;
            case CRIMSON -> Material.NETHER_WART;
        };
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
