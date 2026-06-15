package com.skyblock.core.manager;

import com.skyblock.core.profile.ProfileManager.GameMode;
import com.skyblock.core.profile.ProfileManager.ProfileData;
import com.skyblock.core.profile.ProfileManager.SkyBlockProfile;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton in {@code com.skyblock.core.manager} for SkyBlock profile management.
 *
 * <p>All logic lives in {@link com.skyblock.core.profile.ProfileManager}; this class
 * is a thin forwarding facade so callers in the manager package have a consistent
 * access point alongside {@link SkillManager} and {@link CollectionManager}.</p>
 *
 * <p>All other ProfileManager/PlayerProfileManager copies in this repository are
 * deprecated stubs that delegate here.</p>
 */
public final class ProfileManager {

    public static final int MAX_PROFILES =
            com.skyblock.core.profile.ProfileManager.MAX_PROFILES;

    public static final Map<String, int[]> PROFILE_TYPE_DATA =
            com.skyblock.core.profile.ProfileManager.PROFILE_TYPE_DATA;

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final com.skyblock.core.profile.ProfileManager delegate =
            com.skyblock.core.profile.ProfileManager.getInstance();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Profile CRUD
    // -------------------------------------------------------------------------

    public SkyBlockProfile createProfile(UUID ownerId, String name, GameMode gameMode) {
        return delegate.createProfile(ownerId, name, gameMode);
    }

    public SkyBlockProfile getProfile(UUID profileId) {
        return delegate.getProfile(profileId);
    }

    public List<SkyBlockProfile> getProfilesForOwner(UUID ownerId) {
        return delegate.getProfilesForOwner(ownerId);
    }

    public boolean deleteProfile(UUID profileId) {
        return delegate.deleteProfile(profileId);
    }

    // -------------------------------------------------------------------------
    // Player account data
    // -------------------------------------------------------------------------

    public void setPlayerData(UUID uuid, ProfileData data) {
        delegate.setPlayerData(uuid, data);
    }

    public ProfileData getPlayerData(UUID uuid) {
        return delegate.getPlayerData(uuid);
    }

    // -------------------------------------------------------------------------
    // Fairy souls / SkyBlock XP
    // -------------------------------------------------------------------------

    public void addFairySouls(UUID uuid, int amount) {
        delegate.addFairySouls(uuid, amount);
    }

    public int getFairySouls(UUID uuid) {
        return delegate.getFairySouls(uuid);
    }

    public void addSkyBlockXp(UUID uuid, long amount) {
        delegate.addSkyBlockXp(uuid, amount);
    }

    public long getSkyBlockXp(UUID uuid) {
        return delegate.getSkyBlockXp(uuid);
    }

    // -------------------------------------------------------------------------
    // Profile history / stats
    // -------------------------------------------------------------------------

    public void recordProfileEvent(UUID uuid, String summary) {
        delegate.recordProfileEvent(uuid, summary);
    }

    public List<String> getProfileHistory(UUID uuid) {
        return delegate.getProfileHistory(uuid);
    }

    public Map<UUID, List<String>> getAllProfileHistory() {
        return delegate.getAllProfileHistory();
    }

    public String getProfileStats(UUID uuid) {
        return delegate.getProfileStats(uuid);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }

    public void clear() {
        delegate.clear();
    }
}
