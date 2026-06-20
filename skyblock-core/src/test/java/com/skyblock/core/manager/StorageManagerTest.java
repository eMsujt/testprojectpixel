package com.skyblock.core.manager;

import com.skyblock.core.manager.BackpackManager;
import com.skyblock.core.manager.BackpackManager.BackpackTier;
import com.skyblock.core.vault.VaultManager;
import com.skyblock.core.vault.VaultManager.VaultTier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StorageManagerTest {

    private static StorageManager isolated() {
        return new StorageManager(
                com.skyblock.core.storage.StorageManager.getInstance(),
                BackpackManager.getInstance(),
                VaultManager.getInstance());
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(StorageManager.getInstance(), StorageManager.getInstance());
    }

    @Test
    void newPlayer_StartsWithOnePage() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        assertEquals(1, mgr.getUnlockedPages(id));
    }

    @Test
    void unlockPage_AddsAPage() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.unlockPage(id));
        assertEquals(2, mgr.getUnlockedPages(id));
    }

    @Test
    void backpackTier_DefaultsToSmall() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        assertEquals(BackpackTier.SMALL, mgr.getBackpackTier(id));
        mgr.backpacks().setTier(id, BackpackTier.LARGE);
        assertEquals(BackpackTier.LARGE, mgr.getBackpackTier(id));
    }

    @Test
    void summary_ReportsAllThreeDomains() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        mgr.backpacks().setTier(id, BackpackTier.MEDIUM);
        mgr.vault().setTier(id, VaultTier.BASIC);
        mgr.vault().deposit(id, 1234L);

        String summary = mgr.getSummary(id);
        assertTrue(summary.contains("Pages: 1"), summary);
        assertTrue(summary.contains("MEDIUM"), summary);
        assertTrue(summary.contains("1234"), summary);
    }

    @Test
    void nullArguments_Rejected() {
        StorageManager mgr = isolated();
        assertThrows(NullPointerException.class, () -> mgr.getUnlockedPages(null));
        assertThrows(NullPointerException.class, () -> mgr.unlockPage(null));
        assertThrows(NullPointerException.class, () -> mgr.getBackpackTier(null));
        assertThrows(NullPointerException.class, () -> mgr.loadAll(null));
        assertThrows(NullPointerException.class, () -> mgr.saveAll(null));
    }
}
