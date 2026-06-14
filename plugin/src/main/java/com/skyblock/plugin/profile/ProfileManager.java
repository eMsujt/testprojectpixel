package com.skyblock.plugin.profile;

import com.skyblock.plugin.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton in-memory registry of {@link PlayerProfile} instances keyed by
 * player UUID.
 *
 * <p>On {@link PlayerJoinEvent} the joining player's profile is loaded from the
 * registry, or a new empty one is created and registered if none exists yet.</p>
 *
 * <p>The profile map is mutated only on the server main thread; access it from
 * the main thread or guard it externally.</p>
 */
public final class ProfileManager implements Listener {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the profile for the given player, creating and registering a new
     * empty one if none exists yet.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, never {@code null}
     */
    public PlayerProfile getOrCreate(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.computeIfAbsent(uuid, PlayerProfile::new);
    }

    /**
     * Returns the profile for the given player, or {@code null} if none has
     * been registered.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, or {@code null}
     */
    public PlayerProfile getProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.get(uuid);
    }

    /**
     * Returns whether a profile is registered for the given player.
     *
     * @param uuid unique identifier of the player
     * @return {@code true} if a profile exists
     */
    public boolean hasProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.containsKey(uuid);
    }

    /**
     * Removes the profile for the given player.
     *
     * @param uuid unique identifier of the player
     * @return the removed profile, or {@code null} if none existed
     */
    public PlayerProfile removeProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.remove(uuid);
    }

    /**
     * Returns an immutable snapshot of all registered profiles keyed by UUID.
     *
     * @return the registered profiles
     */
    public Map<UUID, PlayerProfile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    /**
     * Loads the joining player's profile, creating a new one if none exists and
     * hydrating it from {@code plugins/SkyBlock/profiles/<uuid>.yml} when a
     * persisted snapshot is present.
     *
     * <p>The YAML file is read off the main thread; the parsed values are then
     * applied back to the profile on the main thread so the profile map is only
     * ever mutated there.</p>
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerProfile profile = getOrCreate(uuid);

        SkyBlockPlugin plugin = SkyBlockPlugin.getInstance();
        if (plugin == null) {
            return;
        }
        File file = new File(new File(plugin.getDataFolder(), "profiles"), uuid + ".yml");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!file.exists()) {
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            Bukkit.getScheduler().runTask(plugin, () -> applyFromDisk(profile, cfg));
        });
    }

    /**
     * Persists the given player's profile to
     * {@code plugins/SkyBlock/profiles/<uuid>.yml} without blocking the main
     * thread.
     *
     * <p>An immutable YAML snapshot is built on the calling (main) thread from
     * the profile accessors, then written to disk on an async scheduler thread so
     * the profile is only ever read on the main thread. No-op if no profile is
     * registered for the UUID or the plugin is not enabled.</p>
     *
     * @param uuid unique identifier of the player to persist
     */
    public void saveAsync(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) {
            return;
        }

        SkyBlockPlugin plugin = SkyBlockPlugin.getInstance();
        if (plugin == null) {
            return;
        }

        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("purse", profile.getPurse());
        cfg.set("bank", profile.getBank());
        for (Map.Entry<String, Long> entry : profile.getSkillXp().entrySet()) {
            cfg.set("skills." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Long> entry : profile.getCollectionXp().entrySet()) {
            cfg.set("collections." + entry.getKey(), entry.getValue());
        }

        File dir = new File(plugin.getDataFolder(), "profiles");
        File file = new File(dir, uuid + ".yml");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning(
                        "Failed to save profile for " + uuid + ": " + e.getMessage());
            }
        });
    }

    /**
     * Applies a parsed YAML snapshot to the given profile in place. The file
     * layout mirrors what {@link ProfileSaveTask} writes. Must run on the main
     * thread.
     *
     * @param profile the profile to populate in place
     * @param cfg     the parsed snapshot
     */
    private void applyFromDisk(PlayerProfile profile, YamlConfiguration cfg) {
        profile.setPurse(cfg.getLong("purse", profile.getPurse()));
        profile.setBank(cfg.getLong("bank", profile.getBank()));

        ConfigurationSection skills = cfg.getConfigurationSection("skills");
        if (skills != null) {
            for (String skill : skills.getKeys(false)) {
                profile.setSkillXp(skill, skills.getLong(skill));
            }
        }

        ConfigurationSection collections = cfg.getConfigurationSection("collections");
        if (collections != null) {
            for (String collection : collections.getKeys(false)) {
                profile.setCollectionXp(collection, collections.getLong(collection));
            }
        }
    }

    /**
     * Persisted SkyBlock profile for a single player.
     *
     * <p>Tracks the player's identity and accumulated experience keyed by skill
     * name. Instances are not thread-safe; access them from the server main
     * thread or guard them externally.</p>
     */
    public static final class SkyBlockProfile {

        private final UUID uuid;
        private final Map<String, Long> skillXp = new HashMap<>();

        /**
         * Creates a new profile with no accumulated skill experience.
         *
         * @param uuid unique identifier of the player
         */
        public SkyBlockProfile(UUID uuid) {
            this.uuid = Objects.requireNonNull(uuid, "uuid");
        }

        /**
         * Returns the unique identifier of the player this profile belongs to.
         *
         * @return the player's UUID
         */
        public UUID getUuid() {
            return uuid;
        }

        /**
         * Returns an immutable snapshot of the player's skill experience keyed by
         * skill name.
         *
         * @return the skill experience totals
         */
        public Map<String, Long> getSkillXp() {
            return Map.copyOf(skillXp);
        }

        /**
         * Returns the player's accumulated experience in the given skill.
         *
         * @param skill the skill name
         * @return the experience, or 0 if the skill has never been trained
         */
        public long getSkillXp(String skill) {
            Objects.requireNonNull(skill, "skill");
            return skillXp.getOrDefault(skill, 0L);
        }

        /**
         * Adds experience to the given skill.
         *
         * @param skill the skill name
         * @param amount the experience to add, must not be negative
         * @throws IllegalArgumentException if {@code amount} is negative
         */
        public void addSkillXp(String skill, long amount) {
            Objects.requireNonNull(skill, "skill");
            if (amount < 0) {
                throw new IllegalArgumentException("amount must not be negative, got " + amount);
            }
            skillXp.merge(skill, amount, Long::sum);
        }

        /**
         * Sets the player's accumulated experience in the given skill.
         *
         * @param skill the skill name
         * @param amount the new experience total, must not be negative
         * @throws IllegalArgumentException if {@code amount} is negative
         */
        public void setSkillXp(String skill, long amount) {
            Objects.requireNonNull(skill, "skill");
            if (amount < 0) {
                throw new IllegalArgumentException("amount must not be negative, got " + amount);
            }
            skillXp.put(skill, amount);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SkyBlockProfile other && uuid.equals(other.uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }

        @Override
        public String toString() {
            return "SkyBlockProfile{uuid=" + uuid + ", skills=" + skillXp.size() + '}';
        }
    }
}
