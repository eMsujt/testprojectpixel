package com.skyblock.core.menu;

import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.manager.RiftManager.RiftData;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
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
 * GUI menu opened by {@code /rift}. Renders the player's Rift dimension progress
 * directly from {@link RiftManager}: their Motes purse balance, the number of
 * timecharms and Rift souls collected, and Enigma soul progress toward the total
 * hidden across the Rift.
 */
public final class RiftMenu extends Menu {

    private static final int SUMMARY_SLOT   = 4;
    private static final int MOTES_SLOT      = 11;
    private static final int TIMER_SLOT      = 13;
    private static final int TIMECHARM_SLOT  = 15;
    private static final int ENIGMA_SLOT     = 29;
    private static final int RIFT_SOUL_SLOT  = 33;
    private static final int CLOSE_SLOT      = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public RiftMenu(Player player) {
        this(player.getUniqueId());
    }

    public RiftMenu(UUID playerId) {
        super("§5The Rift", 6);
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

        RiftData data = RiftManager.getInstance().getRiftData(playerId);

        inventory.setItem(SUMMARY_SLOT, new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName("§dRift Status")
                .lore(
                        "§7In Rift: " + (data.inRift ? "§aYes" : "§cNo"),
                        "§7Zone: §e" + (data.zone != null ? data.zone.name() : "—"))
                .build());

        inventory.setItem(MOTES_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Motes")
                .lore(
                        "§7Balance: §e" + data.motes,
                        "§7Purse cap: §e" + RiftManager.MOTES_PURSE_CAP)
                .build());

        inventory.setItem(TIMER_SLOT, new ItemBuilder(Material.CLOCK)
                .displayName("§dRift Timer")
                .lore("§7Time remaining: §e" + data.timeRemainingSeconds + "s")
                .build());

        inventory.setItem(TIMECHARM_SLOT, new ItemBuilder(Material.TOTEM_OF_UNDYING)
                .displayName("§aTimecharms")
                .lore("§7Collected: §e" + data.timecharms)
                .build());

        inventory.setItem(ENIGMA_SLOT, new ItemBuilder(Material.ENDER_EYE)
                .displayName("§5Enigma Souls")
                .lore("§7Collected: §e" + data.enigmaSouls
                        + "§7/§e" + RiftManager.ENIGMA_SOUL_TOTAL)
                .build());

        inventory.setItem(RIFT_SOUL_SLOT, new ItemBuilder(Material.GHAST_TEAR)
                .displayName("§bRift Souls")
                .lore("§7Collected: §e" + data.riftSouls)
                .build());

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the Rift menu.")
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
