package com.skyblock.core.manager;

import com.skyblock.core.manager.MuseumManager.MuseumCategory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MuseumManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(MuseumManager.getInstance(), MuseumManager.getInstance());
    }

    @Test
    void donate_RecordsItemAndIsIdempotent() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.donate(id, MuseumCategory.WEAPONS, "Aspect of the End"));
        assertFalse(mgr.donate(id, MuseumCategory.WEAPONS, "Aspect of the End"));
        assertTrue(mgr.getDonations(id, MuseumCategory.WEAPONS).contains("Aspect of the End"));
    }

    @Test
    void getTotalDonations_CountsAcrossCategories() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.donate(id, MuseumCategory.WEAPONS, "Hyperion");
        mgr.donate(id, MuseumCategory.ARMOR, "Necron's Chestplate");
        mgr.donate(id, MuseumCategory.ARMOR, "Necron's Leggings");
        assertEquals(3, mgr.getTotalDonations(id));
    }

    @Test
    void getDonations_UnknownPlayerIsEmpty() {
        assertTrue(MuseumManager.getInstance().getDonations(UUID.randomUUID(), MuseumCategory.RARITIES).isEmpty());
    }

    @Test
    void getMuseumValue_SumsRegisteredDonationValues() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.registerItem(MuseumCategory.WEAPONS, "Valued Sword", 1000L);
        mgr.registerItem(MuseumCategory.ARMOR, "Valued Helmet", 250L);
        mgr.donate(id, MuseumCategory.WEAPONS, "Valued Sword");
        mgr.donate(id, MuseumCategory.ARMOR, "Valued Helmet");
        assertEquals(1250L, mgr.getMuseumValue(id));
    }

    @Test
    void getMuseumValue_UnregisteredDonationsContributeNothing() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.donate(id, MuseumCategory.RARITIES, "Unpriced Relic");
        assertEquals(0L, mgr.getMuseumValue(id));
    }

    @Test
    void registerItem_ExpandsCatalogOnceAndDrivesCompletion() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.registerItem(MuseumCategory.FISHING, "Magma Lord Chestplate"));
        assertFalse(mgr.registerItem(MuseumCategory.FISHING, "Magma Lord Chestplate"));
        assertEquals(1, mgr.getCategorySize(MuseumCategory.FISHING));

        assertEquals(0.0, mgr.getCategoryCompletion(id, MuseumCategory.FISHING));
        assertFalse(mgr.isCategoryComplete(id, MuseumCategory.FISHING));

        mgr.donate(id, MuseumCategory.FISHING, "Magma Lord Chestplate");
        assertEquals(1.0, mgr.getCategoryCompletion(id, MuseumCategory.FISHING));
        assertTrue(mgr.isCategoryComplete(id, MuseumCategory.FISHING));
    }

    @Test
    void remove_DiscardsPlayerData() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.donate(id, MuseumCategory.WEAPONS, "Throwaway");
        assertTrue(mgr.remove(id));
        assertFalse(mgr.remove(id));
        assertEquals(0, mgr.getTotalDonations(id));
    }
}
