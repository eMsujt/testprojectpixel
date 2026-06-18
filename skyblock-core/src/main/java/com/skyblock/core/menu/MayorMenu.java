package com.skyblock.core.menu;

import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.model.Stat;
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
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /mayor}. Shows the current active mayor as a
 * named {@link Material#PLAYER_HEAD} item and each perk they grant as a
 * {@link Material#PAPER} item. Stat bonuses are listed in the mayor item's lore.
 */
public final class MayorMenu extends Menu {

    static final int MAYOR_SLOT      = 4;
    static final int FIRST_PERK_SLOT = 10;
    private static final int CLOSE_SLOT = 49;

    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public MayorMenu() {
        super("§6Mayor", 6);
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

        MayorManager manager = MayorManager.getInstance();
        MayorCandidate mayor = manager.getCurrentMayor();

        if (mayor == null) {
            inventory.setItem(MAYOR_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Active Mayor")
                    .lore("§7No mayor is currently in office.",
                          "§7Check back after the next election.")
                    .build());
        } else {
            List<String> mayorLore = new ArrayList<>();
            mayorLore.add("§7The current mayor of SkyBlock.");
            mayorLore.add("");

            Map<Stat, Double> bonuses = MayorManager.MAYOR_STAT_PERKS.getOrDefault(mayor, java.util.Collections.emptyMap());
            if (!bonuses.isEmpty()) {
                mayorLore.add("§6Stat Bonuses:");
                for (Map.Entry<Stat, Double> entry : bonuses.entrySet()) {
                    mayorLore.add("§7 +" + entry.getValue().intValue() + " §e" + entry.getKey().getDisplayName());
                }
                mayorLore.add("");
            }

            mayorLore.add("§7Perks: §a" + mayor.getPerks().size());

            inventory.setItem(MAYOR_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§6" + mayor.getDisplayName())
                    .lore(mayorLore)
                    .build());

            List<String> perks = mayor.getPerks();
            for (int i = 0; i < perks.size(); i++) {
                int slot = FIRST_PERK_SLOT + i;
                String perkName = perks.get(i);
                inventory.setItem(slot, new ItemBuilder(Material.PAPER)
                        .displayName("§a" + perkName)
                        .lore("§7Perk granted by §6" + mayor.getDisplayName() + "§7.")
                        .build());
            }
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the mayor menu.")
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
