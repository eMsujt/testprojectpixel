package com.skyblock.plugin.pets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton registry for each player's pet collection and active pet.
 *
 * <p>Registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}; on
 * {@link PlayerJoinEvent} the joining player's active pet is loaded so the rest
 * of the plugin can query it for an online player.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class PetManager implements Listener {

    public static final class PetEntry {
        private final UUID id;
        private final PetType type;
        private long xp;
        private int level;
        private String rarity;

        public PetEntry(PetType type, long xp, int level, String rarity) {
            this(UUID.randomUUID(), type, xp, level, rarity);
        }

        public PetEntry(UUID id, PetType type, long xp, int level, String rarity) {
            this.id = id;
            this.type = type;
            this.xp = xp;
            this.level = level;
            this.rarity = rarity;
        }

        public UUID getId()       { return id; }
        public PetType getType()  { return type; }
        public long getXp()       { return xp; }
        public int getLevel()     { return level; }
        public String getRarity() { return rarity; }

        public void setXp(long xp)          { this.xp = xp; }
        public void setLevel(int level)     { this.level = level; }
        public void setRarity(String rarity) { this.rarity = rarity; }
    }

    /**
     * An immutable pet type definition loaded from {@code pets.yml}: its id, its
     * rarity, the base stats it grants at full level and the cumulative pet-XP
     * thresholds required to reach each level.
     *
     * @param id           the pet type id (e.g. {@code WOLF})
     * @param rarity       the pet's rarity (e.g. {@code LEGENDARY})
     * @param baseStats    stat name to base value, in definition order
     * @param xpThresholds cumulative XP required to reach each level
     */
    public record PetDefinition(String id, String rarity, Map<String, Double> baseStats, List<Integer> xpThresholds) {
        public PetDefinition {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(rarity, "rarity");
            baseStats = Collections.unmodifiableMap(new LinkedHashMap<>(baseStats));
            xpThresholds = Collections.unmodifiableList(new ArrayList<>(xpThresholds));
        }
    }

    private static final PetManager INSTANCE = new PetManager();

    private final Map<String, PetDefinition> definitions = new LinkedHashMap<>();
    private final Map<UUID, List<PetEntry>> playerPets = new HashMap<>();
    private final Map<UUID, PetEntry> activePets = new HashMap<>();

    private PetManager() {}

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code pets.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every defined pet type.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "pets.yml");
        if (!file.exists() && plugin.getResource("pets.yml") != null) {
            plugin.saveResource("pets.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        // Support a "pets" wrapper section, falling back to root-level keys.
        ConfigurationSection root = cfg.isConfigurationSection("pets")
                ? cfg.getConfigurationSection("pets")
                : cfg;
        definitions.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            definitions.put(id, parse(id, root.getConfigurationSection(id)));
        }
        plugin.getLogger().info("Loaded " + definitions.size() + " pet definitions.");
    }

    /** Parses a single pet type section into a {@link PetDefinition}. */
    private PetDefinition parse(String id, ConfigurationSection section) {
        String rarity = section.getString("rarity", "COMMON");
        Map<String, Double> baseStats = new LinkedHashMap<>();
        ConfigurationSection stats = section.getConfigurationSection("baseStats");
        if (stats != null) {
            for (String stat : stats.getKeys(false)) {
                baseStats.put(stat, stats.getDouble(stat));
            }
        }
        List<Integer> xpThresholds = section.getIntegerList("xpThresholds");
        return new PetDefinition(id, rarity, baseStats, xpThresholds);
    }

    /** Returns the loaded pet type definition with the given id, or {@code null} if absent. */
    public PetDefinition getDefinition(String id) {
        return id == null ? null : definitions.get(id);
    }

    /** Returns an unmodifiable view of all loaded pet type definitions keyed by id. */
    public Map<String, PetDefinition> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }

    /** Returns an unmodifiable view of a player's owned pets. */
    public List<PetEntry> getPets(UUID playerId) {
        return Collections.unmodifiableList(playerPets.getOrDefault(playerId, Collections.emptyList()));
    }

    /** Adds a pet to the player's collection. */
    public void addPet(UUID playerId, PetEntry pet) {
        playerPets.computeIfAbsent(playerId, k -> new ArrayList<>()).add(pet);
    }

    /** Removes a pet from the player's collection by its UUID. Returns {@code true} if found and removed. */
    public boolean removePet(UUID playerId, UUID petId) {
        List<PetEntry> pets = playerPets.get(playerId);
        if (pets == null) return false;
        boolean removed = pets.removeIf(e -> e.getId().equals(petId));
        PetEntry active = activePets.get(playerId);
        if (removed && active != null && active.getId().equals(petId)) {
            activePets.remove(playerId);
        }
        return removed;
    }

    /** Returns the player's active pet, or {@code null} if none is summoned. */
    public PetEntry getActivePet(UUID playerId) {
        return activePets.get(playerId);
    }

    /**
     * Loads the given player's active pet, ensuring their pet collection is
     * initialised so it is ready for use.
     *
     * @param playerId unique identifier of the player
     * @return the player's active pet, or {@code null} if none is summoned
     */
    public PetEntry load(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerPets.computeIfAbsent(playerId, k -> new ArrayList<>());
        return activePets.get(playerId);
    }

    /**
     * Loads the joining player's active pet so it is ready for use.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        load(player.getUniqueId());
    }

    /**
     * Sets the player's active pet. The pet must already be in the player's
     * collection. Pass {@code null} to despawn the current pet.
     */
    public void setActivePet(UUID playerId, PetEntry pet) {
        if (pet == null) {
            activePets.remove(playerId);
        } else {
            activePets.put(playerId, pet);
        }
    }
}
