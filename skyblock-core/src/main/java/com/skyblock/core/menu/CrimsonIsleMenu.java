package com.skyblock.core.menu;

import com.skyblock.core.manager.CrimsonIsleManager;
import com.skyblock.core.manager.KuudraManager.KuudraTier;
import com.skyblock.core.manager.ReputationManager;
import com.skyblock.core.manager.ReputationManager.Faction;
import com.skyblock.core.util.SkyblockUtils;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public final class CrimsonIsleMenu extends Menu {

    private static final int[] TIER_SLOTS = {20, 21, 22, 23, 24};

    private final UUID playerId;

    public CrimsonIsleMenu(UUID playerId) {
        super("Crimson Isle", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        SkyblockUtils.fillBorder(getRows(), this::setItem, pane);

        CrimsonIsleManager cim = CrimsonIsleManager.getInstance();
        ReputationManager rm = cim.reputation();

        Faction faction = rm.getFaction(playerId);
        String factionName = faction == null ? "None" : faction.getDisplayName();
        String repTier = faction == null
                ? "n/a"
                : rm.getReputationTier(playerId, faction).getDisplayName();
        int rep = faction == null ? 0 : rm.getReputation(playerId, faction);
        KuudraTier highestTier = cim.getHighestUnlockedTier(playerId);

        setItem(13, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§cCrimson Isle")
                .lore(Arrays.asList(
                        "§7Faction: §e" + factionName,
                        "§7Reputation: §e" + rep + " §7(" + repTier + ")",
                        "§7Highest Kuudra: §e" + highestTier.getDisplayName()
                ))
                .build());

        KuudraTier[] tiers = KuudraTier.values();
        for (int i = 0; i < TIER_SLOTS.length && i < tiers.length; i++) {
            KuudraTier tier = tiers[i];
            int completions = cim.kuudra().getCompletionCount(playerId, tier);
            boolean unlocked = cim.canJoinTier(playerId, tier);
            Material mat = unlocked ? Material.FIRE_CHARGE : Material.BARRIER;
            String color = unlocked ? "§e" : "§7";
            setItem(TIER_SLOTS[i], new ItemBuilder(mat)
                    .displayName(color + tier.getDisplayName() + " Kuudra")
                    .lore(Arrays.asList(
                            "§7Completions: §e" + completions,
                            unlocked ? "§aUnlocked" : "§cLocked"
                    ))
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
