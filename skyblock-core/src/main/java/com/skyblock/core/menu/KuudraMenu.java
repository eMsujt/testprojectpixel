package com.skyblock.core.menu;

import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.KuudraManager.KuudraTier;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class KuudraMenu extends Menu {

    static final int FIRST_TIER_SLOT = 11;

    private static final Material[] TIER_MATERIALS = {
        Material.WHITE_BANNER,
        Material.ORANGE_BANNER,
        Material.RED_BANNER,
        Material.MAGENTA_BANNER,
        Material.PURPLE_BANNER
    };

    private final UUID playerId;

    public KuudraMenu(Player player) {
        this(player.getUniqueId());
    }

    public KuudraMenu(UUID playerId) {
        super("§cKuudra", 3);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 18; slot < 27; slot++) setItem(slot, pane);

        KuudraManager manager = KuudraManager.getInstance();
        KuudraTier[] tiers = KuudraTier.values();
        for (int i = 0; i < tiers.length; i++) {
            KuudraTier tier = tiers[i];
            int completions = manager.getCompletionCount(playerId, tier);
            List<String> lore = new ArrayList<>();
            lore.add("§7Completions: §e" + completions);
            lore.add("§7Contribution threshold: §e" + tier.getContributionThreshold());
            setItem(FIRST_TIER_SLOT + i, new ItemBuilder(TIER_MATERIALS[i])
                    .displayName("§c" + tier.getDisplayName())
                    .lore(lore)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
