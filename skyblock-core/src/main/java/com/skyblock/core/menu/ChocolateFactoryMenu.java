package com.skyblock.core.menu;

import com.skyblock.core.manager.ChocolateFactoryManager;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Canonical "Chocolate Factory" menu. A 54-slot (6-row) chest GUI framed by a
 * {@code BLACK_STAINED_GLASS_PANE} border with a cookie at slot 4 summarising the
 * viewing player's chocolate balance and chocolate-per-second production rate
 * (from {@link ChocolateFactoryManager}), and one rabbit tile per rarity showing
 * how many rabbits of that rarity the player owns and the chocolate each produces.
 */
public final class ChocolateFactoryMenu extends Menu {

    private static final String TITLE = "§6Chocolate Factory";
    static final int SUMMARY_SLOT = 4;

    /** Rabbit rarity tiles laid out across the third interior row. */
    private static final int[] RABBIT_SLOTS = {
            19, 20, 21, 22, 23, 24, 25
    };

    private final Player player;

    public ChocolateFactoryMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        ChocolateFactoryManager factory = ChocolateFactoryManager.getInstance();

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Chocolate: §e" + String.format("%,d", factory.getChocolate(id)));
        summaryLore.add("§7Production: §e" + factory.getProductionRate(id) + " §7per second");
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.COOKIE)
                .displayName("§6" + player.getName() + "'s Chocolate Factory")
                .lore(summaryLore)
                .build(),
                e -> e.setCancelled(true));

        int i = 0;
        for (Rarity rarity : ChocolateFactoryManager.CHOCOLATE_PER_SECOND.keySet()) {
            if (i >= RABBIT_SLOTS.length) break;
            List<String> lore = new ArrayList<>();
            lore.add("§7Owned: §e" + factory.getRabbitCount(id, rarity));
            lore.add("§7Produces: §e"
                    + ChocolateFactoryManager.CHOCOLATE_PER_SECOND.get(rarity) + " §7chocolate/sec each");
            setItem(RABBIT_SLOTS[i], new ItemBuilder(Material.RABBIT_FOOT)
                    .displayName("§r" + rarity.getDisplayName() + " Rabbit")
                    .lore(lore)
                    .build(),
                    e -> e.setCancelled(true));
            i++;
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
