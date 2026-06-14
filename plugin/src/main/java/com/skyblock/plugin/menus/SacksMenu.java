package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.CollectionsManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SacksMenu extends Menu {

    private enum Resource {
        WHEAT("Wheat", "WHEAT", Material.WHEAT),
        CARROT("Carrot", "CARROT_ITEM", Material.CARROT),
        POTATO("Potato", "POTATO_ITEM", Material.POTATO),
        PUMPKIN("Pumpkin", "PUMPKIN", Material.PUMPKIN),
        MELON("Melon", "MELON", Material.MELON_SLICE),
        SEEDS("Seeds", "SEEDS", Material.WHEAT_SEEDS),
        SUGAR_CANE("Sugar Cane", "SUGAR_CANE", Material.SUGAR_CANE),
        COBBLESTONE("Cobblestone", "COBBLESTONE", Material.COBBLESTONE),
        COAL("Coal", "COAL", Material.COAL),
        IRON("Iron Ingot", "IRON_INGOT", Material.IRON_INGOT),
        GOLD("Gold Ingot", "GOLD_INGOT", Material.GOLD_INGOT),
        DIAMOND("Diamond", "DIAMOND", Material.DIAMOND),
        LAPIS("Lapis Lazuli", "INK_SACK:4", Material.LAPIS_LAZULI),
        EMERALD("Emerald", "EMERALD", Material.EMERALD),
        REDSTONE("Redstone", "REDSTONE", Material.REDSTONE),
        NETHERRACK("Netherrack", "NETHERRACK", Material.NETHERRACK),
        OAK_LOG("Oak Wood", "LOG", Material.OAK_LOG),
        STRING("String", "STRING", Material.STRING),
        ROTTEN_FLESH("Rotten Flesh", "ROTTEN_FLESH", Material.ROTTEN_FLESH),
        BONE("Bone", "BONE", Material.BONE),
        GUNPOWDER("Gunpowder", "SULPHUR", Material.GUNPOWDER),
        ENDER_PEARL("Ender Pearl", "ENDER_PEARL", Material.ENDER_PEARL);

        private final String displayName;
        private final String key;
        private final Material icon;

        Resource(String displayName, String key, Material icon) {
            this.displayName = displayName;
            this.key = key;
            this.icon = icon;
        }
    }

    private final UUID playerId;

    public SacksMenu(UUID playerId) {
        super("§7Sacks", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        CollectionsManager collections = CollectionsManager.getInstance();
        Resource[] values = Resource.values();
        for (int i = 0; i < values.length; i++) {
            Resource resource = values[i];
            int slot = slotFor(i);
            long stored = collections.getCollectionCount(playerId, resource.key);
            setItem(slot, new ItemBuilder(resource.icon)
                    .displayName("§a" + resource.displayName)
                    .lore(
                            "§7Stored: §e" + stored,
                            "§7Resource type for this sack.")
                    .build());
        }
    }

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

    private static int slotFor(int index) {
        int row = index / 7;
        int column = index % 7;
        return 10 + row * 9 + column;
    }
}
