package com.skyblock.core.forge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ItemForgeManager {

    public static final int MAX_SLOTS = 5;

    public enum ForgeRecipe {
        REFINED_MITHRIL("Refined Mithril",               3600),
        REFINED_TITANIUM("Refined Titanium",             7200),
        MITHRIL_PLATE("Mithril Plate",                   5400),
        TITANIUM_TALISMAN("Titanium Talisman",          10800),
        DIVAN_FRAGMENT("Divan Fragment",                 14400),
        GEMSTONE_GAUNTLET("Gemstone Gauntlet",          21600),
        HOT_STUFF("Hot Stuff",                           28800),
        GOLDEN_PLATE("Golden Plate",                      1800),
        TOPAZ_CRYSTAL("Topaz Crystal",                   7200),
        RUBY_CRYSTAL("Ruby Crystal",                     7200),
        SAPPHIRE_CRYSTAL("Sapphire Crystal",             7200),
        AMBER_CRYSTAL("Amber Crystal",                   7200),
        AMETHYST_CRYSTAL("Amethyst Crystal",             7200),
        JADE_CRYSTAL("Jade Crystal",                     7200),
        MITHRIL_DRILL_1("Mithril-Infused Fuel Cell",     5400),
        MITHRIL_DRILL_2("Mithril-Infused Mining Tool",  10800),
        GEMSTONE_DRILL_1("Gemstone Drill 1",            14400),
        GEMSTONE_DRILL_2("Gemstone Drill 2",            28800),
        TITANIUM_DRILL_1("Titanium Drill 1",            21600),
        TITANIUM_DRILL_2("Titanium Drill 2",            43200),
        PICKONIMBUS_2000("Pickonimbus 2000",            86400),
        FUEL_TANK("Fuel Tank",                           3600),
        GOBLIN_OMELETTE("Goblin Omelette",               1800),
        SORROW_HELMET("Sorrow Helmet",                  21600),
        SORROW_CHESTPLATE("Sorrow Chestplate",          28800),
        SORROW_LEGGINGS("Sorrow Leggings",              25200),
        SORROW_BOOTS("Sorrow Boots",                    18000);

        private final String displayName;
        /** Duration in seconds. */
        private final int durationSeconds;

        ForgeRecipe(String displayName, int durationSeconds) {
            this.displayName = displayName;
            this.durationSeconds = durationSeconds;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDurationSeconds() {
            return durationSeconds;
        }
    }

    public static final class ForgeSlot {
        public final ForgeRecipe recipe;
        public final long startTimeMillis;

        public ForgeSlot(ForgeRecipe recipe, long startTimeMillis) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
            this.startTimeMillis = startTimeMillis;
        }

        public boolean isComplete(long nowMillis) {
            return nowMillis - startTimeMillis >= (long) recipe.getDurationSeconds() * 1000L;
        }

        public long remainingSeconds(long nowMillis) {
            long elapsed = (nowMillis - startTimeMillis) / 1000L;
            return Math.max(0L, recipe.getDurationSeconds() - elapsed);
        }
    }

    private static final ItemForgeManager INSTANCE = new ItemForgeManager();

    private final Map<UUID, Map<Integer, ForgeSlot>> slots = new HashMap<>();

    private ItemForgeManager() {}

    public static ItemForgeManager getInstance() {
        return INSTANCE;
    }

    public ForgeSlot startForge(UUID playerId, int slot, ForgeRecipe recipe, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipe, "recipe");
        if (slot < 0 || slot >= MAX_SLOTS) {
            throw new IllegalArgumentException("slot must be 0–" + (MAX_SLOTS - 1) + ", got " + slot);
        }
        Map<Integer, ForgeSlot> playerSlots = slots.computeIfAbsent(playerId, id -> new HashMap<>());
        if (playerSlots.containsKey(slot)) {
            throw new IllegalStateException("Slot " + slot + " is already occupied");
        }
        ForgeSlot forgeSlot = new ForgeSlot(recipe, nowMillis);
        playerSlots.put(slot, forgeSlot);
        return forgeSlot;
    }

    public ForgeSlot getSlot(UUID playerId, int slot) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, ForgeSlot> playerSlots = slots.get(playerId);
        return playerSlots == null ? null : playerSlots.get(slot);
    }

    public Map<Integer, ForgeSlot> getSlots(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, ForgeSlot> playerSlots = slots.get(playerId);
        return playerSlots == null ? Collections.emptyMap() : Collections.unmodifiableMap(playerSlots);
    }

    public ForgeSlot claimSlot(UUID playerId, int slot, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, ForgeSlot> playerSlots = slots.get(playerId);
        if (playerSlots == null) return null;
        ForgeSlot forgeSlot = playerSlots.get(slot);
        if (forgeSlot == null || !forgeSlot.isComplete(nowMillis)) return null;
        playerSlots.remove(slot);
        return forgeSlot;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return slots.remove(playerId) != null;
    }
}
