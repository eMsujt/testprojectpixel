package com.skyblock.core.collections.gui;

import com.skyblock.core.model.CollectionCategory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionCategoryMenuTest {

    private static final UUID PLAYER = UUID.randomUUID();

    @Test
    void title_containsCategoryDisplayName_farming() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FARMING);
        assertTrue(menu.getTitle().contains("Farming"), "title must include 'Farming'");
    }

    @Test
    void title_containsCategoryDisplayName_mining() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.MINING);
        assertTrue(menu.getTitle().contains("Mining"), "title must include 'Mining'");
    }

    @Test
    void title_containsCategoryDisplayName_combat() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.COMBAT);
        assertTrue(menu.getTitle().contains("Combat"), "title must include 'Combat'");
    }

    @Test
    void title_containsCategoryDisplayName_foraging() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FORAGING);
        assertTrue(menu.getTitle().contains("Foraging"), "title must include 'Foraging'");
    }

    @Test
    void title_containsCategoryDisplayName_fishing() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FISHING);
        assertTrue(menu.getTitle().contains("Fishing"), "title must include 'Fishing'");
    }

    @Test
    void rows_isSix() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FARMING);
        assertEquals(6, menu.getRows());
    }

    @Test
    void title_includesCollectionsBreadcrumb() {
        CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FARMING);
        assertTrue(menu.getTitle().contains("Collections"), "title must include 'Collections'");
    }

    @Test
    void allCategories_constructWithoutException() {
        for (CollectionCategory cat : CollectionCategory.values()) {
            assertDoesNotThrow(() -> new CollectionCategoryMenu(PLAYER, cat),
                    "constructor must not throw for category " + cat.getDisplayName());
        }
    }
}
