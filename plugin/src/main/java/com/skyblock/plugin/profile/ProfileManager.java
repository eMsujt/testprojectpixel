package com.skyblock.plugin.profile;

import com.skyblock.plugin.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
/** @deprecated Use {@link com.skyblock.core.manager.ProfileManager} instead. */
@Deprecated
public final class ProfileManager implements Listener {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    private SkyBlockPlugin plugin;

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the registry with the owning plugin instance used for
     * scheduling profile I/O. Must be called once during plugin enable before
     * any profile is loaded or persisted.
     *
     * @param plugin the owning plugin instance
     */
    public void init(SkyBlockPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
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

        SkyBlockPlugin plugin = this.plugin;
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
     * Persists the quitting player's profile to disk and discards it from the
     * in-memory registry.
     *
     * <p>The YAML snapshot is built synchronously on the main thread (inside
     * {@link #saveAsync(UUID)}) before the profile is removed, so the write is
     * unaffected by the subsequent removal.</p>
     *
     * @param event the quit event
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        saveAsync(uuid);
        removeProfile(uuid);
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

        SkyBlockPlugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }

        YamlConfiguration cfg = buildSnapshot(profile);

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
     * Persists the given player's profile to
     * {@code plugins/SkyBlock/profiles/<uuid>.yml} without blocking the main
     * thread, returning a future that completes once the write has finished.
     *
     * <p>An immutable YAML snapshot is built on the calling (main) thread from
     * the profile accessors, then written to disk on an async scheduler thread so
     * the profile is only ever read on the main thread. The returned future
     * completes normally when the snapshot is written, exceptionally if the write
     * fails, and immediately (with {@code null}) when no profile is registered for
     * the UUID or the plugin is not enabled.</p>
     *
     * @param uuid unique identifier of the player to persist
     * @return a future completed when the profile has been written to disk
     */
    public CompletableFuture<Void> saveProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) {
            return CompletableFuture.completedFuture(null);
        }

        SkyBlockPlugin plugin = this.plugin;
        if (plugin == null) {
            return CompletableFuture.completedFuture(null);
        }

        YamlConfiguration cfg = buildSnapshot(profile);

        File dir = new File(plugin.getDataFolder(), "profiles");
        File file = new File(dir, uuid + ".yml");

        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                cfg.save(file);
                future.complete(null);
            } catch (IOException e) {
                plugin.getLogger().warning(
                        "Failed to save profile for " + uuid + ": " + e.getMessage());
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Builds an immutable YAML snapshot of every persisted field in the given
     * profile. Must be called on the main thread so that profile accessors are
     * read under the same thread guarantee as the profile map itself.
     *
     * @param profile the profile to snapshot
     * @return a fully populated configuration ready for async I/O
     */
    private static YamlConfiguration buildSnapshot(PlayerProfile profile) {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("purse", profile.getPurse());
        cfg.set("bank", profile.getBank());
        cfg.set("activeProfileName", profile.getActiveProfileName());
        cfg.set("activePet", profile.getActivePet());
        cfg.set("pets", profile.getOwnedPets());
        for (Map.Entry<String, Long> entry : profile.getSkillXp().entrySet()) {
            cfg.set("skills." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Long> entry : profile.getCollectionXp().entrySet()) {
            cfg.set("collections." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Long> entry : profile.getCollectionCounts().entrySet()) {
            cfg.set("collectionCounts." + entry.getKey(), entry.getValue());
        }
        serializeInventory(cfg, "enderChest", profile.getEnderChestContents());
        serializeInventory(cfg, "potionBag", profile.getPotionBagContents());
        serializeInventory(cfg, "quiver", profile.getQuiverContents());
        serializeInventory(cfg, "fishingBag", profile.getFishingBagContents());
        serializeInventory(cfg, "islandStorage", profile.getIslandStorageContents());
        serializeInventory(cfg, "wardrobe", profile.getWardrobeContents());
        return cfg;
    }

    /**
     * Applies a parsed YAML snapshot to the given profile in place. The file
     * layout mirrors what {@link #buildSnapshot} writes. Must run on the main
     * thread.
     *
     * @param profile the profile to populate in place
     * @param cfg     the parsed snapshot
     */
    private void applyFromDisk(PlayerProfile profile, YamlConfiguration cfg) {
        profile.setPurse(cfg.getLong("purse", profile.getPurse()));
        profile.setBank(cfg.getLong("bank", profile.getBank()));

        String savedProfileName = cfg.getString("activeProfileName");
        if (savedProfileName != null) {
            profile.setActiveProfileName(savedProfileName);
        }
        profile.setActivePet(cfg.getString("activePet", null));

        List<String> pets = cfg.getStringList("pets");
        if (!pets.isEmpty()) {
            profile.setOwnedPets(pets);
        }

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

        ConfigurationSection collectionCounts = cfg.getConfigurationSection("collectionCounts");
        if (collectionCounts != null) {
            for (String collection : collectionCounts.getKeys(false)) {
                profile.setCollectionCount(collection, collectionCounts.getLong(collection));
            }
        }

        profile.setEnderChestContents(deserializeInventory(cfg, "enderChest"));
        profile.setPotionBagContents(deserializeInventory(cfg, "potionBag"));
        profile.setQuiverContents(deserializeInventory(cfg, "quiver"));
        profile.setFishingBagContents(deserializeInventory(cfg, "fishingBag"));
        profile.setIslandStorageContents(deserializeInventory(cfg, "islandStorage"));
        profile.setWardrobeContents(deserializeInventory(cfg, "wardrobe"));
    }

    private static void serializeInventory(YamlConfiguration cfg, String key, ItemStack[] contents) {
        if (contents == null) {
            return;
        }
        cfg.set(key + ".size", contents.length);
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                cfg.set(key + "." + i, contents[i]);
            }
        }
    }

    private static ItemStack[] deserializeInventory(YamlConfiguration cfg, String key) {
        int size = cfg.getInt(key + ".size", -1);
        if (size < 0) {
            return null;
        }
        ItemStack[] contents = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            contents[i] = cfg.getItemStack(key + "." + i);
        }
        return contents;
    }

}
