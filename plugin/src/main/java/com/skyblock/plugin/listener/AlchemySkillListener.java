package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Awards Alchemy XP through {@link SkillManager} whenever a brewing stand
 * finishes brewing. XP = number of filled output slots × per-ingredient XP
 * loaded from the bundled {@code alchemy_ingredients.yml} resource.
 */
public final class AlchemySkillListener implements Listener {

    /** Fallback XP per filled output slot when the ingredient is unlisted. */
    private static final long DEFAULT_XP = 3L;

    /** Maximum distance (blocks) to credit the nearest player for a brew. */
    private static final double CREDIT_RADIUS = 16.0D;

    /** Built-in fallback ingredient → XP table. */
    private static final Map<Material, Long> DEFAULT_INGREDIENT_XP = Map.ofEntries(
            Map.entry(Material.NETHER_WART,            3L),
            Map.entry(Material.SUGAR,                  3L),
            Map.entry(Material.BLAZE_POWDER,           3L),
            Map.entry(Material.GHAST_TEAR,             5L),
            Map.entry(Material.MAGMA_CREAM,            3L),
            Map.entry(Material.GLISTERING_MELON_SLICE, 3L),
            Map.entry(Material.SPIDER_EYE,             3L),
            Map.entry(Material.FERMENTED_SPIDER_EYE,   3L),
            Map.entry(Material.PUFFERFISH,             3L),
            Map.entry(Material.RABBIT_FOOT,            3L),
            Map.entry(Material.GOLDEN_CARROT,          3L),
            Map.entry(Material.PHANTOM_MEMBRANE,       5L),
            Map.entry(Material.DRAGON_BREATH,          5L),
            Map.entry(Material.GUNPOWDER,              3L),
            Map.entry(Material.REDSTONE,               3L),
            Map.entry(Material.GLOWSTONE_DUST,         3L)
    );

    private final Map<Material, Long> ingredientXp;
    private final SkillManager skillManager = SkillManager.getInstance();

    public AlchemySkillListener(JavaPlugin plugin) {
        this.ingredientXp = loadIngredientXp(plugin);
    }

    private static Map<Material, Long> loadIngredientXp(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("alchemy_ingredients.yml");
        if (resource == null) {
            return DEFAULT_INGREDIENT_XP;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read alchemy_ingredients.yml: " + e.getMessage());
            return DEFAULT_INGREDIENT_XP;
        }
        ConfigurationSection section = cfg.getConfigurationSection("ingredients");
        if (section == null) {
            return DEFAULT_INGREDIENT_XP;
        }
        Map<Material, Long> table = new EnumMap<>(Material.class);
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) {
                plugin.getLogger().warning("Unknown material in alchemy_ingredients.yml: " + key);
                continue;
            }
            table.put(material, section.getLong(key));
        }
        if (table.isEmpty()) {
            return DEFAULT_INGREDIENT_XP;
        }
        plugin.getLogger().info("Loaded Alchemy XP for " + table.size() + " ingredients.");
        return table;
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory contents = event.getContents();
        ItemStack ingredient = contents.getIngredient();
        if (ingredient == null || ingredient.getType().isAir()) {
            return;
        }
        long xpPerSlot = ingredientXp.getOrDefault(ingredient.getType(), DEFAULT_XP);

        int filledSlots = 0;
        for (ItemStack result : event.getResults()) {
            if (result != null && !result.getType().isAir()) {
                filledSlots++;
            }
        }
        if (filledSlots == 0) {
            return;
        }

        long xp = filledSlots * xpPerSlot;
        Player player = nearestPlayer(event.getBlock());
        if (player != null) {
            grantXP(player, xp);
        }
    }

    private Player nearestPlayer(Block block) {
        Location origin = block.getLocation();
        Player closest = null;
        double best = Double.MAX_VALUE;
        for (Player player : block.getWorld().getNearbyPlayers(origin, CREDIT_RADIUS)) {
            double distance = player.getLocation().distanceSquared(origin);
            if (distance < best) {
                best = distance;
                closest = player;
            }
        }
        return closest;
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.ALCHEMY);
        skillManager.addXP(id, SkillType.ALCHEMY, amount);
        int after = skillManager.getLevel(id, SkillType.ALCHEMY);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.ALCHEMY, before, after);
            player.sendTitle("§aSkill Level Up!", "§eAlchemy §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
