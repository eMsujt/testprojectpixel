package com.skyblock.core.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * GUI menu opened by {@code /collection}. Renders the five
 * {@link CollectionCategory} values as selector items (pickaxe/sword/wheat/
 * sapling/fishing rod), showing for each the player's total items gathered, the
 * number of collections in the category and the tiers unlocked across them.
 */
public final class CollectionMenu extends Menu {

    /** First slot of the centered category row; the five categories occupy {@code FIRST_CATEGORY_SLOT .. +4}. */
    static final int FIRST_CATEGORY_SLOT = 20;

    private static final Map<CollectionCategory, Material> ICONS = new EnumMap<>(CollectionCategory.class);

    static {
        ICONS.put(CollectionCategory.FARMING,  Material.WHEAT);
        ICONS.put(CollectionCategory.MINING,   Material.IRON_PICKAXE);
        ICONS.put(CollectionCategory.COMBAT,   Material.IRON_SWORD);
        ICONS.put(CollectionCategory.FORAGING, Material.OAK_SAPLING);
        ICONS.put(CollectionCategory.FISHING,  Material.FISHING_ROD);
    }

    private final UUID playerId;

    public CollectionMenu(Player player) {
        this(player.getUniqueId());
    }

    public CollectionMenu(UUID playerId) {
        super("§eCollections", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        CollectionManager manager = CollectionManager.getInstance();

        int index = 0;
        for (CollectionCategory category : CollectionCategory.values()) {
            Collection[] collections = category.getCollections();
            int tiersUnlocked = 0;
            for (Collection c : collections) tiersUnlocked += manager.getTier(playerId, c);

            setItem(FIRST_CATEGORY_SLOT + index, new ItemBuilder(ICONS.getOrDefault(category, Material.BOOK))
                    .displayName("§a" + category.getDisplayName() + " Collection")
                    .lore(
                            "§7Collections: §e" + collections.length,
                            "§7Items gathered: §a" + manager.getTotalForCategory(playerId, category),
                            "§7Tiers unlocked: §6" + tiersUnlocked,
                            "",
                            "§eClick to view")
                    .build());
            index++;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
