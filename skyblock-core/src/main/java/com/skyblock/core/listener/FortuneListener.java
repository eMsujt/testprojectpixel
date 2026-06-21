package com.skyblock.core.listener;

import com.skyblock.core.manager.FortuneManager;
import com.skyblock.core.manager.FortuneManager.FortuneType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Applies Mining / Farming / Foraging Fortune to block-break drops: every 100% fortune grants a
 * guaranteed extra drop, the remainder a chance for one more. Extra drops are added on top of the
 * vanilla drops, so the total is {@code base * (1 + fortune/100)}.
 */
public final class FortuneListener implements Listener {

    private static final FortuneListener INSTANCE = new FortuneListener();

    private static final Set<Material> FARMING_EXTRA = EnumSet.of(
            Material.MELON, Material.PUMPKIN, Material.SUGAR_CANE, Material.CACTUS,
            Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.COCOA);

    private static final Set<Material> MINING_EXTRA = EnumSet.of(
            Material.STONE, Material.COBBLESTONE, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE,
            Material.NETHERRACK, Material.END_STONE, Material.OBSIDIAN, Material.GRAVEL,
            Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.GLOWSTONE);

    private FortuneListener() {}

    public static FortuneListener getInstance() {
        return INSTANCE;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        FortuneType type = fortuneTypeFor(event.getBlock().getType());
        if (type == null) {
            return;
        }
        double fortune = FortuneManager.getInstance().getFortune(player.getUniqueId(), type);
        int extra = (int) (fortune / 100.0);
        if (Math.random() * 100.0 < fortune % 100.0) {
            extra++;
        }
        if (extra <= 0) {
            return;
        }
        ItemStack tool = player.getInventory().getItemInMainHand();
        Collection<ItemStack> drops = event.getBlock().getDrops(tool, player);
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        for (ItemStack drop : drops) {
            ItemStack bonus = drop.clone();
            bonus.setAmount(drop.getAmount() * extra);
            event.getBlock().getWorld().dropItemNaturally(loc, bonus);
        }
    }

    private static FortuneType fortuneTypeFor(Material type) {
        if (Tag.CROPS.isTagged(type) || FARMING_EXTRA.contains(type)) {
            return FortuneType.FARMING;
        }
        if (Tag.LOGS.isTagged(type)) {
            return FortuneType.FORAGING;
        }
        if (type.name().endsWith("_ORE") || MINING_EXTRA.contains(type)) {
            return FortuneType.MINING;
        }
        return null;
    }
}
