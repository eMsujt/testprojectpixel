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
 * Trophy Fishing menu (opened from Odger), matching the wiki
 * {@code {{UI|Trophy Fishing}}} layout: the 18 trophy fish occupy rows 2–4
 * (cols 2–8, then cols 2–5); the bottom row holds Fillet Trophy Fish (slot 47),
 * Trophy Fishing Rewards (51) and the Lost Rewards Shop (52). Undiscovered fish
 * render as obfuscated Gray Dye, as on Hypixel.
 */
public final class TrophyFishMenu extends AbstractSkyBlockMenu {

    /** The 18 fish slots: rows 2–4, cols 2–8 then cols 2–5 (wiki layout). */
    static final int[] FISH_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31
    };

    static final int FILLET_SLOT = 47;
    static final int REWARDS_SLOT = 51;
    static final int LOST_REWARDS_SLOT = 52;

    public TrophyFishMenu(Player player) {
        super(player, "Trophy Fishing", 6);
    }

    @Override
    protected void populate() {
        UUID owner = player.getUniqueId();
        TrophyFishManager manager = TrophyFishManager.getInstance();

        ItemStack pane = SkyblockUtils.buildItem(Material.BLACK_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        TrophyFish[] fish = TrophyFish.values();
        for (int i = 0; i < FISH_SLOTS.length && i < fish.length; i++) {
            TrophyFish f = fish[i];
            int count = manager.getCatchCount(owner, f);
            if (count > 0) {
                TrophyTier tier = manager.getTier(owner, f);
                setItem(FISH_SLOTS[i], SkyblockUtils.buildItem(Material.COD,
                        "§a" + f.getDisplayName(),
                        "§7Caught: §e" + count,
                        "§7Highest tier: " + (tier == null ? "§8None" : "§6" + tier.name())));
            } else {
                setItem(FISH_SLOTS[i], SkyblockUtils.buildItem(Material.GRAY_DYE,
                        "§c§kxxxxxxxxxxx",
                        "§9How to catch",
                        "§7 ???",
                        "",
                        "§cUndiscovered"));
            }
        }

        setItem(FILLET_SLOT, SkyblockUtils.buildItem(Material.MAGMA_CREAM,
                "§aFillet Trophy Fish",
                "§7Convert duplicate trophy fish",
                "§7into Magmafish.",
                "",
                "§eClick to Fillet"),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§eFilleting is coming soon.");
                });

        setItem(REWARDS_SLOT, SkyblockUtils.buildItem(Material.GOLD_BLOCK,
                "§aTrophy Fishing Rewards",
                "§7View the rewards for reaching",
                "§7each Trophy Fishing tier.",
                "",
                "§7Total trophy points: §e" + manager.getTotalPoints(owner),
                "",
                "§eClick to view!"),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§eThe rewards view is coming soon.");
                });

        setItem(LOST_REWARDS_SLOT, SkyblockUtils.buildItem(Material.CHEST,
                "§aLost Rewards Shop",
                "§7Buy back rewards you missed",
                "§7from previous tiers.",
                "",
                "§eClick to view!"),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§eThe Lost Rewards Shop is coming soon.");
                });
    }
}
