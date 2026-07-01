package com.skyblock.core.manager;

/**
 * Deprecated stub — delegates to the canonical implementation.
 *
 * @deprecated Use {@link com.skyblock.core.profile.manager.ProfileManager} directly.
 */
@Deprecated
public final class ProfileManager {

    private static final com.skyblock.core.profile.manager.ProfileManager DELEGATE =
            com.skyblock.core.profile.manager.ProfileManager.getInstance();

    private ProfileManager() {}

    public static com.skyblock.core.profile.manager.ProfileManager getInstance() {
        return DELEGATE;
    }
}
