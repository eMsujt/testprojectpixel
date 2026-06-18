package com.skyblock.core.menu;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BestiaryMenuTest {

    private static final UUID PLAYER = UUID.randomUUID();

    @BeforeEach
    void reset() {
        BestiaryManager.getInstance().resetKills(PLAYER);
    }

    @Test
    void title_overviewIsBestiary() {
        BestiaryMenu menu = new BestiaryMenu(PLAYER);
        assertEquals("§2Bestiary", menu.getTitle());
    }

    @Test
    void rows_isSix() {
        BestiaryMenu menu = new BestiaryMenu(PLAYER);
        assertEquals(6, menu.getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new BestiaryMenu(PLAYER));
    }

    @Test
    void categoryIcons_combatIsSword() {
        assertEquals(Material.IRON_SWORD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.COMBAT));
    }

    @Test
    void categoryIcons_slayerIsDiamondSword() {
        assertEquals(Material.DIAMOND_SWORD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.SLAYER));
    }

    @Test
    void categoryIcons_bossIsNetherStar() {
        assertEquals(Material.NETHER_STAR, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.BOSS));
    }

    @Test
    void categoryIcons_netherIsNetherrack() {
        assertEquals(Material.NETHERRACK, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.NETHER));
    }

    @Test
    void categoryIcons_oceanIsPrismarineShard() {
        assertEquals(Material.PRISMARINE_SHARD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.OCEAN));
    }

    @Test
    void categoryIcons_miningIsPickaxe() {
        assertEquals(Material.IRON_PICKAXE, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.MINING));
    }

    @Test
    void categoryIcons_allCategoriesMapped() {
        for (BestiaryCategory cat : BestiaryCategory.values()) {
            assertNotNull(BestiaryMenu.CATEGORY_ICONS.get(cat),
                    "CATEGORY_ICONS must contain an entry for " + cat);
        }
    }

    @Test
    void manager_milestoneLevel_zeroOnNoKills() {
        assertEquals(0, BestiaryManager.getInstance().getMilestoneLevel(PLAYER));
    }

    @Test
    void manager_completedFamilyCount_zeroOnNoKills() {
        assertEquals(0, BestiaryManager.getInstance().getCompletedFamilyCount(PLAYER));
    }

    @Test
    void manager_killsForCategory_zeroOnNoKills() {
        assertEquals(0, BestiaryManager.getInstance().getKillsForCategory(PLAYER, BestiaryCategory.COMBAT));
    }

    @Test
    void manager_killsForFamily_zeroOnNoKills() {
        assertEquals(0, BestiaryManager.getInstance().getKillsForFamily(PLAYER, BestiaryFamily.ZOMBIE));
    }

    @Test
    void manager_recordAndGetKills_roundTrips() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        mgr.recordKill(PLAYER, "zombie");
        mgr.recordKill(PLAYER, "zombie");
        assertEquals(2, mgr.getKills(PLAYER, "zombie"));
    }

    @Test
    void manager_killsForFamily_sumsMobKeys() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        mgr.recordKill(PLAYER, "zombie");
        mgr.recordKill(PLAYER, "drowned");
        assertEquals(2, mgr.getKillsForFamily(PLAYER, BestiaryFamily.ZOMBIE));
    }
}
