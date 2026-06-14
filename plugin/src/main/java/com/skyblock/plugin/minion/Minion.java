package com.skyblock.plugin.minion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/** Mutable state for a single placed minion owned by a player. */
public final class Minion {

    public enum MinionType {
        COBBLESTONE("Cobblestone Minion"),
        WHEAT("Wheat Minion"),
        COAL("Coal Minion"),
        IRON("Iron Minion"),
        GOLD("Gold Minion"),
        DIAMOND("Diamond Minion"),
        LAPIS("Lapis Minion"),
        REDSTONE("Redstone Minion"),
        EMERALD("Emerald Minion"),
        SNOW("Snow Minion"),
        CLAY("Clay Minion"),
        FISHING("Fishing Minion"),
        LOG("Log Minion"),
        OAK("Oak Minion"),
        CARROT("Carrot Minion"),
        POTATO("Potato Minion"),
        MELON("Melon Minion"),
        PUMPKIN("Pumpkin Minion"),
        SUGAR_CANE("Sugar Cane Minion"),
        MUSHROOM("Mushroom Minion"),
        CACTUS("Cactus Minion"),
        FLOWER("Flower Minion"),
        SAND("Sand Minion"),
        GLOWSTONE("Glowstone Minion"),
        NETHER_WART("Nether Wart Minion"),
        QUARTZ("Quartz Minion"),
        CHICKEN("Chicken Minion"),
        COW("Cow Minion"),
        PIG("Pig Minion"),
        SHEEP("Sheep Minion"),
        RABBIT("Rabbit Minion"),
        ZOMBIE("Zombie Minion"),
        SKELETON("Skeleton Minion"),
        SPIDER("Spider Minion"),
        CREEPER("Creeper Minion"),
        BLAZE("Blaze Minion"),
        MAGMA_CUBE("Magma Cube Minion"),
        ENDERMAN("Enderman Minion"),
        GHAST("Ghast Minion"),
        SLIME("Slime Minion"),
        TARANTULA("Tarantula Minion"),
        ICE("Ice Minion"),
        GRAVEL("Gravel Minion"),
        OBSIDIAN("Obsidian Minion"),
        BIRCH("Birch Minion"),
        SPRUCE("Spruce Minion"),
        DARK_OAK("Dark Oak Minion"),
        JUNGLE("Jungle Minion"),
        ACACIA("Acacia Minion"),
        MITHRIL("Mithril Minion"),
        HARD_STONE("Hard Stone Minion"),
        GEMSTONE("Gemstone Minion");

        private final String displayName;

        MinionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum MinionTier {
        TIER_1, TIER_2, TIER_3, TIER_4, TIER_5,
        TIER_6, TIER_7, TIER_8, TIER_9, TIER_10,
        TIER_11
    }

    public final UUID id;
    public final UUID owner;
    public final MinionType type;
    private MinionTier tier;
    private Location location;
    private final List<ItemStack> storage = new ArrayList<>();

    public Minion(UUID id, UUID owner, MinionType type, MinionTier tier) {
        this.id = Objects.requireNonNull(id, "id");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.type = Objects.requireNonNull(type, "type");
        this.tier = Objects.requireNonNull(tier, "tier");
    }

    public MinionTier getTier() {
        return tier;
    }

    public void setTier(MinionTier tier) {
        this.tier = Objects.requireNonNull(tier, "tier");
    }

    public Location getLocation() {
        return location == null ? null : location.clone();
    }

    public void setLocation(Location location) {
        this.location = location == null ? null : location.clone();
    }

    /** @return an unmodifiable view of the minion's stored items. */
    public List<ItemStack> getStorage() {
        return Collections.unmodifiableList(storage);
    }

    public void addToStorage(ItemStack item) {
        storage.add(Objects.requireNonNull(item, "item"));
    }

    public boolean removeFromStorage(ItemStack item) {
        return storage.remove(item);
    }

    public void clearStorage() {
        storage.clear();
    }
}
