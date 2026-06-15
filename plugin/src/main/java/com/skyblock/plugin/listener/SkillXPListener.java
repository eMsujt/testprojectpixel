package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.Location;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Awards skill XP directly to a player's {@link SkyBlockProfile} in response to
 * real in-world actions. Breaking a fully grown crop grants Farming XP;
 * {@link Ageable} crops only count once they reach their maximum age, while
 * non-ageable produce (pumpkins, melons, sugar cane, …) always counts.
 */
public final class SkillXPListener implements Listener {

    private final Map<Location, UUID> brewingStandOwners = new HashMap<>();

    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,                4L),
            Map.entry(Material.CARROTS,              4L),
            Map.entry(Material.POTATOES,             4L),
            Map.entry(Material.BEETROOTS,            4L),
            Map.entry(Material.NETHER_WART,          3L),
            Map.entry(Material.PUMPKIN,              6L),
            Map.entry(Material.MELON,                6L),
            Map.entry(Material.SUGAR_CANE,           2L),
            Map.entry(Material.CACTUS,               2L),
            Map.entry(Material.COCOA,                3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   6L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 6L)
    );

    private static final Map<Material, Long> MINING_XP = Map.of(
            Material.COAL_ORE,      5L,
            Material.IRON_ORE,      6L,
            Material.GOLD_ORE,      7L,
            Material.DIAMOND_ORE,  10L,
            Material.LAPIS_ORE,     8L,
            Material.EMERALD_ORE,  12L,
            Material.REDSTONE_ORE,  7L
    );

    private static final Set<Material> COMMON_FISH = Set.of(
            Material.RAW_COD,
            Material.RAW_SALMON,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH
    );

    private static final Map<Material, Long> FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L)
    );

    private static final Map<EntityType, Long> COMBAT_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,         5L),
            Map.entry(EntityType.SKELETON,       5L),
            Map.entry(EntityType.CREEPER,        6L),
            Map.entry(EntityType.SPIDER,         4L),
            Map.entry(EntityType.CAVE_SPIDER,    5L),
            Map.entry(EntityType.ENDERMAN,      12L),
            Map.entry(EntityType.BLAZE,         10L),
            Map.entry(EntityType.GHAST,         15L),
            Map.entry(EntityType.WITCH,         10L),
            Map.entry(EntityType.SLIME,          3L),
            Map.entry(EntityType.MAGMA_CUBE,     5L),
            Map.entry(EntityType.WITHER_SKELETON,15L)
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        Long farmingXp = FARMING_XP.get(block.getType());
        if (farmingXp != null && isMature(block)) {
            profile.addSkillXp("farming", farmingXp);
            return;
        }
        Long miningXp = MINING_XP.get(block.getType());
        if (miningXp != null) {
            profile.addSkillXp("mining", miningXp);
            return;
        }
        Long foragingXp = FORAGING_XP.get(block.getType());
        if (foragingXp != null) {
            profile.addSkillXp("foraging", foragingXp);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;

        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        Material type = caught.getItemStack().getType();
        long xp = COMMON_FISH.contains(type) ? 5L : 20L;
        profile.addSkillXp("fishing", xp);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        Long xp = COMBAT_XP.get(event.getEntityType());
        if (xp == null) return;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(killer.getUniqueId());
        profile.addSkillXp("combat", xp);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(event.getEnchanter().getUniqueId());
        profile.addSkillXp("enchanting", (long) event.getExpLevelCost() * 3L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() != 3) return; // ingredient slot
        Location loc = event.getInventory().getLocation();
        if (loc == null) return;
        brewingStandOwners.put(loc, player.getUniqueId());
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        UUID uuid = brewingStandOwners.remove(event.getBlock().getLocation());
        if (uuid == null) return;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(uuid);
        profile.addSkillXp("alchemy", 8L);
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player player)) return;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("taming", 10L);
    }

    /**
     * Returns whether the crop block has finished growing. {@link Ageable} crops
     * are mature only at their maximum age; all other produce is always mature.
     */
    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }
}
