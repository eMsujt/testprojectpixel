package com.skyblock.core.manager;

import com.skyblock.core.manager.HarpManager.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HarpManagerTest {

    private HarpManager mgr;
    private UUID player;

    /** The manager is a singleton; reset the test player's progress each run. */
    @BeforeEach
    void setUp() {
        mgr = HarpManager.getInstance();
        player = UUID.randomUUID();
        mgr.reset(player);
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(HarpManager.getInstance(), HarpManager.getInstance());
    }

    @Test
    void firstSong_AlwaysUnlocked_LaterSongsLocked() {
        assertTrue(mgr.isUnlocked(player, Song.FRERE_JACQUES));
        assertFalse(mgr.isUnlocked(player, Song.HYMN_OF_JOY));
    }

    @Test
    void completingSong_UnlocksNext() {
        assertTrue(mgr.recordCompletion(player, Song.FRERE_JACQUES, 100));
        assertTrue(mgr.isUnlocked(player, Song.HYMN_OF_JOY));
    }

    @Test
    void recordCompletion_KeepsBestOnly() {
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 80);
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 40);
        assertEquals(80, mgr.getBestCompletion(player, Song.FRERE_JACQUES));
    }

    @Test
    void recordCompletion_ClampsPercent() {
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 250);
        assertEquals(100, mgr.getBestCompletion(player, Song.FRERE_JACQUES));
        mgr.reset(player);
        mgr.recordCompletion(player, Song.FRERE_JACQUES, -10);
        assertEquals(0, mgr.getBestCompletion(player, Song.FRERE_JACQUES));
    }

    @Test
    void partialPlay_DoesNotComplete() {
        assertFalse(mgr.recordCompletion(player, Song.FRERE_JACQUES, 99));
        assertFalse(mgr.isCompleted(player, Song.FRERE_JACQUES));
    }

    @Test
    void completion_IsFirstTimeOnly() {
        assertTrue(mgr.recordCompletion(player, Song.FRERE_JACQUES, 100));
        assertFalse(mgr.recordCompletion(player, Song.FRERE_JACQUES, 100));
        assertEquals(1, mgr.getCompletedCount(player));
    }

    @Test
    void intelligenceBonus_SumsCompletedRewards() {
        assertEquals(0, mgr.getIntelligenceBonus(player));
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 100);
        mgr.recordCompletion(player, Song.PURE_IMAGINATION, 100);
        assertEquals(Song.FRERE_JACQUES.getIntelligenceReward()
                + Song.PURE_IMAGINATION.getIntelligenceReward(), mgr.getIntelligenceBonus(player));
    }

    @Test
    void getCompletedSongs_ReturnsUnmodifiable() {
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 100);
        assertEquals(1, mgr.getCompletedSongs(player).size());
        assertThrows(UnsupportedOperationException.class,
                () -> mgr.getCompletedSongs(player).add(Song.BRAHMS));
    }

    @Test
    void songProgression_NextChainsToNull() {
        Song last = Song.values()[Song.values().length - 1];
        assertNull(last.next());
        assertEquals(Song.HYMN_OF_JOY, Song.FRERE_JACQUES.next());
    }
}
