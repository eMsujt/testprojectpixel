package com.skyblock.core.menu;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.BestiaryManager.BestiaryMob;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Shows every mob in a single {@link BestiaryCategory} along with the player's
 * kill count and tier/milestone progress for each, read directly from the
 * canonical {@link BestiaryManager}.
 *
 * <p>A 54-slot chest titled {@code §6Bestiary › <Category>} with a gray
 * glass-pane top/bottom border. Mob icons fill slots 9–44; a back arrow at
 * slot 4 returns the player to {@link BestiaryMenu}.</p>
 */
public final class BestiaryCategoryMenu extends Menu {

    private final UUID playerId;
    private final BestiaryCategory category;

    public BestiaryCategoryMenu(UUID playerId, BestiaryCategory category) {
        super("§6Bestiary › " + category.displayName, 6);
        this.playerId = playerId;
        this.category = category;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(4, new ItemBuilder(Material.ARROW)
                        .displayName("§7Back to Bestiary")
                        .build(),
                event -> {
                    event.setCancelled(true);
                    new BestiaryMenu(playerId).open((Player) event.getWhoClicked());
                });

        BestiaryManager manager = BestiaryManager.getInstance();
        int slot = 9;
        for (String mobKey : categoryMobKeys()) {
            if (slot >= 45) break;
            BestiaryMob mob = findMob(mobKey);
            String displayName = mob != null ? mob.displayName : mobKey;
            int kills = manager.getKills(playerId, mobKey);
            int tier = manager.getTier(playerId, mobKey);
            int toNext = manager.getKillsToNextTier(playerId, mobKey);
            String nextLine = tier >= BestiaryManager.MAX_TIER
                    ? "§7Progress: §aMaxed"
                    : "§7Next tier in: §e" + toNext + " kills";
            setItem(slot++, new ItemBuilder(iconFor(mobKey))
                    .displayName("§a" + displayName)
                    .lore(
                            "§7Kills: §e" + kills,
                            "§7Tier: §e" + tier + "§7/§e" + BestiaryManager.MAX_TIER,
                            nextLine)
                    .build());
        }
    }

    /** Distinct mob keys across all families in this category, in declared order. */
    private Set<String> categoryMobKeys() {
        Set<String> keys = new LinkedHashSet<>();
        for (BestiaryFamily family : category.families) {
            for (String mobType : family.mobTypes) {
                keys.add(mobType);
            }
        }
        return keys;
    }

    private static BestiaryMob findMob(String mobKey) {
        for (BestiaryMob mob : BestiaryMob.values()) {
            if (mob.mobKey.equals(mobKey)) return mob;
        }
        return null;
    }

    /** Best-effort spawn-egg icon for a mob, falling back to a spawner. */
    private static Material iconFor(String mobKey) {
        try {
            return Material.valueOf(mobKey.toUpperCase() + "_SPAWN_EGG");
        } catch (IllegalArgumentException ex) {
            return Material.SPAWNER;
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
