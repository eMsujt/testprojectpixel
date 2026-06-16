package com.skyblock.core.crafting.manager;

import com.skyblock.core.crafting.manager.CraftingManager.ShapedRecipe;
import com.skyblock.core.crafting.manager.CraftingManager.ShapelessRecipe;
import com.skyblock.core.crafting.manager.CraftingManager.SkyBlockRecipe;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CraftingManagerTest {

    private final CraftingManager crafting = CraftingManager.getInstance();

    // -------------------------------------------------------------------------
    // Recipe lookup
    // -------------------------------------------------------------------------

    @Test
    void getRecipe_KnownDefault_Present() {
        Optional<SkyBlockRecipe> recipe = crafting.getRecipe("enchanted_iron_sword");
        assertTrue(recipe.isPresent());
        assertEquals(Material.IRON_SWORD, recipe.get().result());
    }

    @Test
    void getRecipe_Unknown_Empty() {
        assertTrue(crafting.getRecipe("does_not_exist").isEmpty());
    }

    // -------------------------------------------------------------------------
    // Shaped matching
    // -------------------------------------------------------------------------

    @Test
    void matchesShaped_ExactGrid_Matches() {
        // enchanted_iron_sword: {"I","I","S"}
        Material[][] grid = {
                {Material.IRON_INGOT},
                {Material.IRON_INGOT},
                {Material.STICK},
        };
        Optional<SkyBlockRecipe> match = crafting.findMatchingRecipe(grid);
        assertTrue(match.isPresent());
        assertEquals(Material.IRON_SWORD, match.get().result());
    }

    @Test
    void matchesShaped_IsShiftInvariant() {
        SkyBlockRecipe recipe = crafting.getRecipe("enchanted_iron_sword").orElseThrow();
        // Same shape shifted into the right column of a 3x3 grid.
        Material[][] shifted = {
                {null, null, Material.IRON_INGOT},
                {null, null, Material.IRON_INGOT},
                {null, null, Material.STICK},
        };
        assertTrue(crafting.matches(recipe, shifted));
    }

    @Test
    void matchesShaped_WrongIngredient_DoesNotMatch() {
        SkyBlockRecipe recipe = crafting.getRecipe("enchanted_iron_sword").orElseThrow();
        Material[][] grid = {
                {Material.GOLD_INGOT},
                {Material.GOLD_INGOT},
                {Material.STICK},
        };
        assertFalse(crafting.matches(recipe, grid));
    }

    @Test
    void matchesShaped_EmptyGrid_DoesNotMatch() {
        SkyBlockRecipe recipe = crafting.getRecipe("enchanted_iron_sword").orElseThrow();
        Material[][] empty = {{null, null, null}, {null, null, null}, {null, null, null}};
        assertFalse(crafting.matches(recipe, empty));
    }

    // -------------------------------------------------------------------------
    // Shapeless matching
    // -------------------------------------------------------------------------

    @Test
    void matchesShapeless_OrderIndependent_Matches() {
        SkyBlockRecipe torch = crafting.getRecipe("torch_x4").orElseThrow();
        Material[][] grid = {{Material.STICK, Material.COAL}};
        assertTrue(crafting.matches(torch, grid));
    }

    @Test
    void matchesShapeless_ExtraIngredient_DoesNotMatch() {
        SkyBlockRecipe torch = crafting.getRecipe("torch_x4").orElseThrow();
        Material[][] grid = {{Material.COAL, Material.STICK, Material.DIAMOND}};
        assertFalse(crafting.matches(torch, grid));
    }

    @Test
    void matchesShapeless_MissingIngredient_DoesNotMatch() {
        SkyBlockRecipe torch = crafting.getRecipe("torch_x4").orElseThrow();
        Material[][] grid = {{Material.COAL}};
        assertFalse(crafting.matches(torch, grid));
    }

    // -------------------------------------------------------------------------
    // Recipe construction validation
    // -------------------------------------------------------------------------

    @Test
    void shapedRecipe_BlankId_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapedRecipe(
                "  ", Material.STONE, 1, new String[]{"S"}, Map.of('S', Material.STONE)));
    }

    @Test
    void shapedRecipe_NonPositiveAmount_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapedRecipe(
                "id", Material.STONE, 0, new String[]{"S"}, Map.of('S', Material.STONE)));
    }

    @Test
    void shapedRecipe_TooManyRows_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapedRecipe(
                "id", Material.STONE, 1, new String[]{"S", "S", "S", "S"}, Map.of('S', Material.STONE)));
    }

    @Test
    void shapelessRecipe_EmptyIngredients_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapelessRecipe(
                "id", Material.STONE, 1, List.of()));
    }

    // -------------------------------------------------------------------------
    // Registration + craft history
    // -------------------------------------------------------------------------

    @Test
    void register_DuplicateId_Throws() {
        String id = "test_dup_" + UUID.randomUUID();
        crafting.registerShapeless(id, Material.STONE, 1, List.of(Material.COBBLESTONE));
        assertThrows(IllegalStateException.class,
                () -> crafting.registerShapeless(id, Material.STONE, 1, List.of(Material.COBBLESTONE)));
        crafting.removeRecipe(id);
    }

    @Test
    void recordCraft_UnknownRecipe_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> crafting.recordCraft(UUID.randomUUID(), "no_such_recipe"));
    }

    @Test
    void recordCraft_KnownRecipe_IncrementsCount() {
        UUID player = UUID.randomUUID();
        assertEquals(0, crafting.getCraftCount(player, "enchanted_iron_sword"));
        crafting.recordCraft(player, "enchanted_iron_sword");
        crafting.recordCraft(player, "enchanted_iron_sword");
        assertEquals(2, crafting.getCraftCount(player, "enchanted_iron_sword"));
    }
}
