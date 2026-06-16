package com.skyblock.core.profile.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        ProfileManager a = ProfileManager.getInstance();
        ProfileManager b = ProfileManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(ProfileManager.getInstance());
    }

    @Test
    void maxProfiles_IsPositive() {
        assertTrue(ProfileManager.MAX_PROFILES > 0);
    }
}
