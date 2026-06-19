package com.skyblock.core.menu;

import com.skyblock.core.manager.HotMManager;
import com.skyblock.core.manager.HotMManager.HotMPerk;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class HotMMenu extends Menu {

    public HotMMenu() {
        super("§5Heart of the Mountain", 6);
    }

    @Override
    protected void build() {
        HotMManager manager = HotMManager.getInstance();
        HotMPerk[] perks = HotMPerk.values();
        for (int i = 0; i < perks.length && i < 54; i++) {
            HotMPerk perk = perks[i];
            int level = manager.getLevel(perk);
            String name = perk.name().replace('_', ' ');
            ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK)
                    .displayName("§e" + name)
                    .lore("§7Level: §a" + level)
                    .build();
            setItem(i, item);
        }
    }
}
