package com.skyblock.core.menu;

import com.skyblock.core.manager.FishingManager.TrophyFish;
import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.manager.TrophyFishManager.TrophyTier;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical 54-slot Trophy Fish menu opened by {@code /trophyfish}. Row 0 is a
 * blue-pane border with an overall trophy-points summary at slot 4; the inner
 * region lists each trophy fish backed by {@link TrophyFishManager}, showing the
 * player's catch count and the highest {@link TrophyTier} reached.
 */
public final class TrophyFishMenu extends AbstractSkyBlockMenu {

    /** Inner content slots (rows 1–4, bordered left and right). */
    static final int[] FISH_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public TrophyFishMenu(Player player) {
        super(player, "§bTrophy Fish", 6);
    }

    @Override
    protected void populate() {
        UUID owner = player.getUniqueId();
        TrophyFishManager manager = TrophyFishManager.getInstance();

        ItemStack pane = SkyblockUtils.buildItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        setItem(4, SkyblockUtils.buildItem(Material.TROPICAL_FISH,
                "§bTrophy Fishing",
                "§7Total trophy points: §e" + manager.getTotalPoints(owner)));

        TrophyFish[] fish = TrophyFish.values();
        for (int i = 0; i < FISH_SLOTS.length && i < fish.length; i++) {
            TrophyFish f = fish[i];
            int count = manager.getCatchCount(owner, f);
            TrophyTier tier = manager.getTier(owner, f);
            setItem(FISH_SLOTS[i], SkyblockUtils.buildItem(
                    count > 0 ? Material.COD : Material.GRAY_DYE,
                    (count > 0 ? "§a" : "§7") + f.getDisplayName(),
                    "§7Caught: §e" + count,
                    "§7Tier: " + (tier == null ? "§8None" : "§6" + tier.name())));
        }
    }
}
