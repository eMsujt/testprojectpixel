package com.skyblock.core.menu;

import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.KuudraManager.KuudraTier;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * GUI menu opened by {@code /kuudra}. Renders all five {@link KuudraTier}s as
 * colored banner items, showing for each tier its escalation number, the
 * contribution score needed for full loot, the essence/token/supplies data from
 * {@link KuudraManager#TIER_DATA} and the player's completion count.
 */
public final class KuudraMenu extends Menu {

    /** First slot of the centered tier row; the five tiers occupy {@code FIRST_TIER_SLOT .. +4}. */
    static final int FIRST_TIER_SLOT = 11;

    private static final Map<KuudraTier, Material> ICONS = new EnumMap<>(KuudraTier.class);

    static {
        ICONS.put(KuudraTier.BASIC,    Material.WHITE_BANNER);
        ICONS.put(KuudraTier.HOT,      Material.ORANGE_BANNER);
        ICONS.put(KuudraTier.BURNING,  Material.RED_BANNER);
        ICONS.put(KuudraTier.FIERY,    Material.MAGENTA_BANNER);
        ICONS.put(KuudraTier.INFERNAL, Material.BLACK_BANNER);
    }

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

        int index = 0;
        for (KuudraTier tier : KuudraTier.values()) {
            int[] data = KuudraManager.TIER_DATA.get(tier.name());
            int essenceCost = data == null ? 0 : data[0];
            int tokenReward = data == null ? 0 : data[1];
            int suppliesCost = data == null ? 0 : data[2];

            setItem(FIRST_TIER_SLOT + index, new ItemBuilder(ICONS.getOrDefault(tier, Material.WHITE_BANNER))
                    .displayName("§cKuudra §6" + tier.getDisplayName())
                    .lore(
                            "§7Tier: §e" + tier.getTier() + "§7/§e5",
                            "§7Contribution for loot: §a" + tier.getContributionThreshold(),
                            "",
                            "§7Essence cost: §d" + essenceCost,
                            "§7Token reward: §6" + tokenReward,
                            "§7Supplies cost: §b" + suppliesCost,
                            "",
                            "§7Completions: §e" + manager.getCompletionCount(playerId, tier))
                    .build());
            index++;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
