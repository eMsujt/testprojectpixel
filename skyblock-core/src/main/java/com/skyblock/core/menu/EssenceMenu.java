package com.skyblock.core.menu;

import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Essence Shop hub, matching the wiki {@code {{UI|Essence Shop}}}: the two
 * essence <em>perk</em> shops — Undead (slot 20) and Wither (slot 24) — open
 * their per-essence {@link EssenceShopMenu} sub-shops, with an Essence Guide
 * (slot 51) and a balance summary (slot 4). The other essences are item-upgrade
 * currencies, not perk shops, so they have no sub-shop tile.
 */
public final class EssenceMenu extends AbstractSkyBlockMenu {

    public EssenceMenu(Player player) {
        super(player, "Essence Shop", 6);
    }

    @Override
    protected void populate() {
        EssenceManager essence = EssenceManager.getInstance();
        UUID playerId = player.getUniqueId();

        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        // Balance summary (slot 4).
        List<String> balances = new ArrayList<>();
        for (EssenceType t : EssenceType.values()) {
            balances.add("§7" + t.getDisplayName() + " Essence: §d"
                    + String.format("%,d", essence.getBalance(playerId, t)));
        }
        setItem(4, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§dYour Essence")
                .lore(balances)
                .build());

        // 3,3 — Undead Essence Shop
        setItem(20, new ItemBuilder(Material.ROTTEN_FLESH)
                .displayName("§aUndead Essence Shop")
                .lore("§7Spend Undead Essence on Catacombs", "§7stat perks and dungeon upgrades.",
                        "", "§7Balance: §d" + String.format("%,d", essence.getBalance(playerId, EssenceType.UNDEAD)),
                        "", "§eClick to view!")
                .build(),
                e -> new EssenceShopMenu(player, EssenceType.UNDEAD).open(player));

        // 3,7 — Wither Essence Shop
        setItem(24, new ItemBuilder(Material.COAL_BLOCK)
                .displayName("§aWither Essence Shop")
                .lore("§7Spend Wither Essence on the", "§7Forbidden stat perks.",
                        "", "§7Balance: §d" + String.format("%,d", essence.getBalance(playerId, EssenceType.WITHER)),
                        "", "§eClick to view!")
                .build(),
                e -> new EssenceShopMenu(player, EssenceType.WITHER).open(player));

        // 6,6 — Essence Guide
        setItem(51, new ItemBuilder(Material.REDSTONE_TORCH)
                .displayName("§aEssence Guide")
                .lore("§7Learn how to earn and spend", "§7each type of Essence.")
                .build());

        // 6,5 — Close
        setItem(49, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> player.closeInventory());
    }
}
