package com.skyblock.core.menu;

import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.manager.RiftManager.RiftArea;
import com.skyblock.core.manager.RiftManager.RiftData;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public final class RiftMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    private static final int[] ZONE_SLOTS = {19, 20, 21, 22, 23, 24, 25};

    private final UUID playerId;

    public RiftMenu(Player player) {
        this(player.getUniqueId());
    }

    public RiftMenu(UUID playerId) {
        super("§5The Rift", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        RiftManager manager = RiftManager.getInstance();
        RiftData data = manager.getRiftData(playerId);

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.ENDER_EYE)
                .displayName("§dThe Rift")
                .lore(Arrays.asList(
                        "§7Motes: §d" + data.motes + " §7/ §d" + RiftManager.MOTES_PURSE_CAP,
                        "§7Enigma Souls: §5" + data.enigmaSouls + " §7/ §5" + RiftManager.ENIGMA_SOUL_TOTAL,
                        "§7Rift Souls: §5" + data.riftSouls,
                        "§7Timecharms: §e" + data.timecharms
                ))
                .build());

        RiftArea[] areas = RiftArea.values();
        for (int i = 0; i < ZONE_SLOTS.length && i < areas.length; i++) {
            RiftArea area = areas[i];
            boolean current = area == data.zone;
            String color = current ? "§d" : "§7";
            setItem(ZONE_SLOTS[i], new ItemBuilder(Material.PURPLE_DYE)
                    .displayName(color + toDisplayName(area.name()))
                    .lore(current ? "§aCurrently Here" : "§7Not Visited")
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static String toDisplayName(String enumName) {
        String[] words = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
