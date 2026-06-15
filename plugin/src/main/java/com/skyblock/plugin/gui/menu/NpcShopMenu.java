package com.skyblock.plugin.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.items.CustomItemRegistry;
import com.skyblock.plugin.items.SkyBlockItem;
import com.skyblock.plugin.manager.EconomyManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/** @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead. */
@Deprecated
public class NpcShopMenu extends Menu {

    /**
     * A single shop entry. {@code materialOrCustomId} is resolved first against
     * {@link CustomItemRegistry}; if not found there it is matched against a
     * vanilla {@link Material} name.
     */
    public record ShopItem(String materialOrCustomId, String displayName, int price) {
        public ShopItem {
            Objects.requireNonNull(materialOrCustomId, "materialOrCustomId");
            Objects.requireNonNull(displayName, "displayName");
            if (price < 0) throw new IllegalArgumentException("price must not be negative");
        }
    }

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final List<ShopItem> items;

    public NpcShopMenu(String title, List<ShopItem> items) {
        super(Objects.requireNonNull(title, "title"), 6);
        this.items = List.copyOf(Objects.requireNonNull(items, "items"));
    }

    @Override
    protected void build() {
        fillBorder();

        int count = Math.min(items.size(), SLOTS.length);
        for (int i = 0; i < count; i++) {
            ShopItem shopItem = items.get(i);
            ItemStack icon = buildIcon(shopItem);
            setItem(SLOTS[i], icon, event -> {
                event.setCancelled(true);
                HumanEntity who = event.getWhoClicked();
                if (EconomyManager.getInstance().removeCoins(who.getUniqueId(), shopItem.price())) {
                    ItemStack give = resolveItem(shopItem.materialOrCustomId());
                    if (give != null) {
                        who.getInventory().addItem(give);
                    }
                    who.sendMessage("§aPurchased " + shopItem.displayName()
                            + " §afor §6" + shopItem.price() + " coins§a!");
                } else {
                    who.sendMessage("§cYou don't have enough coins!");
                }
            });
        }
    }

    private static ItemStack buildIcon(ShopItem item) {
        ItemStack base = resolveItem(item.materialOrCustomId());
        if (base == null) {
            base = new ItemStack(Material.BARRIER);
        }
        return new ItemBuilder(base)
                .displayName(item.displayName())
                .lore("§7Price: §6" + item.price() + " coins", "§eClick to buy!")
                .build();
    }

    /** Resolves a material-or-custom-id string to an ItemStack, or null if unknown. */
    private static ItemStack resolveItem(String id) {
        SkyBlockItem custom = CustomItemRegistry.getInstance().getItem(id);
        if (custom != null) {
            return new ItemStack(custom.getMaterial());
        }
        Material material = Material.matchMaterial(id);
        if (material != null) {
            return new ItemStack(material);
        }
        return null;
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
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
