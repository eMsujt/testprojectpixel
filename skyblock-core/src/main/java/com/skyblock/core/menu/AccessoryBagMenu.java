package com.skyblock.core.menu;

import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.talisman.manager.TalismanManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class AccessoryBagMenu extends AbstractSkyBlockMenu {

    public AccessoryBagMenu(Player player) {
        super(player, "§6Accessory Bag", 4);
    }

    @Override
    protected void populate() {
        UUID id = player.getUniqueId();
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 36; slot++) setItem(slot, pane);

        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        List<TalismanManager.TalismanType> types = new ArrayList<>(mgr.getContents(id));
        for (int i = 0; i < 27 && i < types.size(); i++) {
            TalismanManager.TalismanType type = types.get(i);
            setItem(i, new ItemBuilder(Material.EMERALD)
                    .displayName("§6" + formatName(type.name()))
                    .lore("§7+" + type.bonus + " " + type.stat.getDisplayName())
                    .build());
        }

        int power = mgr.getTotalMagicPower(id);
        setItem(31, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§6Magic Power")
                .lore("§7Total: §e" + power)
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static String formatName(String enumName) {
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase(java.util.Locale.ROOT));
        }
        return sb.toString();
    }
}
