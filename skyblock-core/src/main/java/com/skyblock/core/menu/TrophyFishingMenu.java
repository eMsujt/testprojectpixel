package com.skyblock.core.menu;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.manager.TrophyFishManager.TrophyTier;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * GUI menu opened by {@code /trophyfishing}. Renders every
 * {@link FishingManager.TrophyFish} type as a fish item whose name colour
 * reflects the fish's rarity; the lore reports the player's catch count, the
 * trophy tier reached, and how many more catches reach the next tier. The
 * summary head at the top reports total trophy points and types caught.
 */
public final class TrophyFishingMenu extends Menu {

    static final int SUMMARY_SLOT = 4;

    /** First inner slot; the trophy fish fill rows 2–6 (slots 9 onward). */
    private static final int FIRST_SLOT = 9;

    private final UUID playerId;

    public TrophyFishingMenu(Player player) {
        this(player.getUniqueId());
    }

    public TrophyFishingMenu(UUID playerId) {
        super("§3Trophy Fishing", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        TrophyFishManager manager = TrophyFishManager.getInstance();

        FishingManager.TrophyFish[] fishes = FishingManager.TrophyFish.values();
        int caught = 0;
        for (FishingManager.TrophyFish fish : fishes) {
            if (manager.getCatchCount(playerId, fish) > 0) caught++;
        }

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.FISHING_ROD)
                .displayName("§3Trophy Fishing")
                .lore(
                        "§7Trophy points: §e" + manager.getTotalPoints(playerId),
                        "§7Types caught: §a" + caught + "§7/§a" + fishes.length)
                .build());

        for (int i = 0; i < fishes.length; i++) {
            FishingManager.TrophyFish fish = fishes[i];
            String color = SkyblockUtils.rarityColor(fish.rarity).toString();
            int count = manager.getCatchCount(playerId, fish);
            TrophyTier tier = manager.getTier(playerId, fish);
            setItem(FIRST_SLOT + i, new ItemBuilder(Material.PUFFERFISH)
                    .displayName(color + fish.displayName)
                    .lore(
                            "§7Rarity: " + color + fish.rarity.getDisplayName(),
                            "§7Caught: §e" + count,
                            "§7Tier: " + (tier == null ? "§7None" : "§6" + capitalize(tier.name())),
                            tier == TrophyTier.DIAMOND
                                    ? "§aMaxed out!"
                                    : "§7Next tier in §e" + toNext(count, tier) + " §7catches")
                    .build());
        }
    }

    /** Catches remaining to reach the tier above {@code tier}, or 0 if maxed. */
    private static int toNext(int count, TrophyTier tier) {
        TrophyTier next = nextTier(tier);
        return next == null ? 0 : Math.max(0, next.threshold - count);
    }

    private static TrophyTier nextTier(TrophyTier tier) {
        if (tier == null) return TrophyTier.BRONZE;
        switch (tier) {
            case BRONZE: return TrophyTier.SILVER;
            case SILVER: return TrophyTier.GOLD;
            case GOLD:   return TrophyTier.DIAMOND;
            case DIAMOND:
            default:     return null;
        }
    }

    private static String capitalize(String name) {
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
