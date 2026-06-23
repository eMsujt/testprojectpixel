package com.skyblock.core.menu;

import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.SackManager.SackType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SackMenu extends AbstractSkyBlockMenu {

    private static final SackType[] TYPES = SackType.values();

    private static final Material[] TAB_MATERIALS = {
            Material.CYAN_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
    };

    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private SackType selectedType;

    public SackMenu(Player player) {
        super(player, "§2Sacks", 6);
        this.selectedType = TYPES[0];
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = TYPES.length; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        SackManager manager = SackManager.getInstance();

        for (int i = 0; i < TYPES.length; i++) {
            SackType type = TYPES[i];
            boolean selected = type == selectedType;
            ItemBuilder builder = new ItemBuilder(TAB_MATERIALS[i])
                    .displayName((selected ? "§e" : "§7") + type.getDisplayName())
                    .lore(selected ? "§aSelected" : "§7Click to view");
            if (selected) builder.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
            setItem(i, builder.build());
        }

        Map<String, Integer> contents = manager.getSackContents(player.getUniqueId(), selectedType);
        if (contents.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cEmpty Sack")
                    .lore("§7This sack contains no items.")
                    .build());
            return;
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(contents.entrySet());
        for (int i = 0; i < CONTENT_SLOTS.length && i < entries.size(); i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            String itemId = entry.getKey();
            int count = entry.getValue();
            int capacity = manager.getItemTier(itemId).getCapacity();
            setItem(CONTENT_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§f" + formatName(itemId))
                    .lore("§7Count: §e" + count + " §8/ §e" + capacity)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot >= 0 && slot < TYPES.length && TYPES[slot] != selectedType) {
            selectedType = TYPES[slot];
            if (event.getWhoClicked() instanceof Player clicker) {
                open(clicker);
            }
        }
    }

    private static String formatName(String itemId) {
        String[] parts = itemId.toLowerCase().split("[_\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (!sb.isEmpty()) sb.append(' ');
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }
}
