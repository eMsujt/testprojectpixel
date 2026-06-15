package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.EconomyManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Reusable NPC shop base: a 54-slot chest framed by a gray glass-pane border
 * whose wares come from a caller-supplied {@link List} of {@link ShopItem}s.
 *
 * <p>Items are placed in the 21 centred inner slots (rows 2–4); clicking one
 * deducts its price from the buyer's purse via {@link EconomyManager} and
 * hands them the item.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * List<ShopMenu.ShopItem> wares = List.of(
 *     new ShopMenu.ShopItem(Material.IRON_INGOT, 10),
 *     new ShopMenu.ShopItem(Material.GOLD_INGOT, 25)
 * );
 * new ShopMenu("§aMy Shop", wares).open(player);
 * }</pre>
 * </p>
 */
public class ShopMenu extends Menu {

    /** A single purchasable item: the material given to the buyer and its coin price. */
    public record ShopItem(Material material, int price) {
        public ShopItem {
            Objects.requireNonNull(material, "material");
        }
    }

    /** Centred content slots across the middle rows, left-to-right, top-to-bottom. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final List<ShopItem> items;

    public ShopMenu(String title, List<ShopItem> items) {
        super(Objects.requireNonNull(title, "title"), 6);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    protected void build() {
        fillBorder();

        int count = Math.min(items.size(), SLOTS.length);
        for (int i = 0; i < count; i++) {
            ShopItem item = items.get(i);
            setItem(SLOTS[i], new ItemBuilder(item.material())
                            .displayName("§f" + formatName(item.material()))
                            .lore("§7Price: §6" + item.price() + " coins", "§eClick to buy!")
                            .build(),
                    event -> {
                        event.setCancelled(true);
                        HumanEntity who = event.getWhoClicked();
                        if (EconomyManager.getInstance().removeCoins(who.getUniqueId(), item.price())) {
                            who.getInventory().addItem(new ItemStack(item.material()));
                            who.sendMessage("§aPurchased §6" + formatName(item.material())
                                    + " §afor §6" + item.price() + " coins§a!");
                        } else {
                            who.sendMessage("§cYou don't have enough coins!");
                        }
                    });
        }
    }

    /** Fills the outer edge with gray glass panes. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }

    private static String formatName(Material material) {
        StringBuilder sb = new StringBuilder();
        for (String word : material.name().split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }
}
