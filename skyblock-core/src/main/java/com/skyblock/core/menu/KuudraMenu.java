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
 * 54-slot Kuudra menu opened by {@code /kuudra}. Renders all five
 * {@link KuudraTier}s as nether-star items in a centered row, showing for each
 * tier its escalation number, the contribution score needed for full loot, the
 * essence/token/supplies data from {@link KuudraManager#TIER_DATA} and the
 * player's completion count. Top and bottom edges are gray-pane borders.
 */
public final class KuudraMenu extends Menu {

    /** Inventory slots for the five Kuudra tiers (Basic → Infernal). */
    static final int[] TIER_SLOTS = {20, 21, 22, 23, 24};

    private static final int SUMMARY_SLOT = 49;

    /** Display-name color per tier, escalating with difficulty. */
    private static final Map<KuudraTier, String> COLORS = new EnumMap<>(KuudraTier.class);

    static {
        COLORS.put(KuudraTier.BASIC,    "§f");
        COLORS.put(KuudraTier.HOT,      "§6");
        COLORS.put(KuudraTier.BURNING,  "§c");
        COLORS.put(KuudraTier.FIERY,    "§d");
        COLORS.put(KuudraTier.INFERNAL, "§4");
    }

    private final UUID playerId;

    public KuudraMenu(Player player) {
        this(player.getUniqueId());
    }

    public KuudraMenu(UUID playerId) {
        super("§cKuudra", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        KuudraManager manager = KuudraManager.getInstance();

        int index = 0;
        for (KuudraTier tier : KuudraTier.values()) {
            int[] data = KuudraManager.TIER_DATA.get(tier.name());
            int essenceCost = data == null ? 0 : data[0];
            int tokenReward = data == null ? 0 : data[1];
            int suppliesCost = data == null ? 0 : data[2];

            setItem(TIER_SLOTS[index], new ItemBuilder(Material.NETHER_STAR)
                    .displayName(COLORS.getOrDefault(tier, "§f") + "Kuudra " + tier.getDisplayName())
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

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.COMPASS)
                .displayName("§cKuudra Overview")
                .lore(
                        "§7Defeat Kuudra in the Crimson Isle",
                        "§7to earn essence and tokens.")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
