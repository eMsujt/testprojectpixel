package com.skyblock.plugin.profile;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Data holder for a single player's SkyBlock profile.
 *
 * <p>Tracks the player's identity and accumulated experience keyed by skill
 * name, along with collection progress keyed by collection name. Instances are
 * not thread-safe; access them from the server main thread or guard them
 * externally.</p>
 */
public final class PlayerProfile {

    private final UUID uuid;
    private final Map<String, Long> skillXp = new HashMap<>();
    private final Map<String, Long> collectionXp = new HashMap<>();
    private long purse = 0L;
    private long bank = 0L;
    private ItemStack[] enderChestContents;
    private ItemStack[] potionBagContents;
    private ItemStack[] quiverContents;
    private ItemStack[] fishingBagContents;
    private ItemStack[] islandStorageContents;

    /**
     * Creates a new profile with no accumulated skill experience.
     *
     * @param uuid unique identifier of the player
     */
    public PlayerProfile(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
    }

    /**
     * Returns the unique identifier of the player this profile belongs to.
     *
     * @return the player's UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns an immutable snapshot of the player's skill experience keyed by
     * skill name.
     *
     * @return the skill experience totals
     */
    public Map<String, Long> getSkillXp() {
        return Map.copyOf(skillXp);
    }

    /**
     * Returns the player's accumulated experience in the given skill.
     *
     * @param skill the skill name
     * @return the experience, or 0 if the skill has never been trained
     */
    public long getSkillXp(String skill) {
        Objects.requireNonNull(skill, "skill");
        return skillXp.getOrDefault(skill, 0L);
    }

    /**
     * Adds experience to the given skill.
     *
     * @param skill the skill name
     * @param amount the experience to add, must not be negative
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void addSkillXp(String skill, long amount) {
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        skillXp.merge(skill, amount, Long::sum);
    }

    /**
     * Sets the player's accumulated experience in the given skill.
     *
     * @param skill the skill name
     * @param amount the new experience total, must not be negative
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void setSkillXp(String skill, long amount) {
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        skillXp.put(skill, amount);
    }

    /**
     * Returns an immutable snapshot of the player's collection progress keyed by
     * collection name.
     *
     * @return the collection totals
     */
    public Map<String, Long> getCollectionXp() {
        return Map.copyOf(collectionXp);
    }

    /**
     * Returns the player's accumulated progress in the given collection.
     *
     * @param collection the collection name
     * @return the amount, or 0 if the collection has never been progressed
     */
    public long getCollectionXp(String collection) {
        Objects.requireNonNull(collection, "collection");
        return collectionXp.getOrDefault(collection, 0L);
    }

    /**
     * Adds progress to the given collection.
     *
     * @param collection the collection name
     * @param amount the amount to add, must not be negative
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void addCollectionXp(String collection, long amount) {
        Objects.requireNonNull(collection, "collection");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        collectionXp.merge(collection, amount, Long::sum);
    }

    /**
     * Sets the player's accumulated progress in the given collection.
     *
     * @param collection the collection name
     * @param amount the new total, must not be negative
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void setCollectionXp(String collection, long amount) {
        Objects.requireNonNull(collection, "collection");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        collectionXp.put(collection, amount);
    }

    public long getPurse() { return purse; }

    public void setPurse(long purse) {
        if (purse < 0) throw new IllegalArgumentException("purse must not be negative");
        this.purse = purse;
    }

    public long getBank() { return bank; }

    public void setBank(long bank) {
        if (bank < 0) throw new IllegalArgumentException("bank must not be negative");
        this.bank = bank;
    }

    /**
     * Returns the player's persisted Ender Chest contents as a per-slot array,
     * or {@code null} if the Ender Chest has never been opened. Individual slots
     * may be {@code null} (empty). The returned array is a defensive copy.
     *
     * @return the Ender Chest contents, or {@code null}
     */
    public ItemStack[] getEnderChestContents() {
        return enderChestContents == null ? null : enderChestContents.clone();
    }

    /**
     * Sets the player's persisted Ender Chest contents from a per-slot array.
     * The array is copied, so later mutations of the live inventory do not leak
     * into the stored snapshot.
     *
     * @param enderChestContents the Ender Chest contents, may be {@code null}
     */
    public void setEnderChestContents(ItemStack[] enderChestContents) {
        this.enderChestContents = enderChestContents == null ? null : enderChestContents.clone();
    }

    /**
     * Returns the player's persisted Potion Bag contents as a per-slot array,
     * or {@code null} if the Potion Bag has never been opened. Individual slots
     * may be {@code null} (empty). The returned array is a defensive copy.
     *
     * @return the Potion Bag contents, or {@code null}
     */
    public ItemStack[] getPotionBagContents() {
        return potionBagContents == null ? null : potionBagContents.clone();
    }

    /**
     * Sets the player's persisted Potion Bag contents from a per-slot array.
     * The array is copied, so later mutations of the live inventory do not leak
     * into the stored snapshot.
     *
     * @param potionBagContents the Potion Bag contents, may be {@code null}
     */
    public void setPotionBagContents(ItemStack[] potionBagContents) {
        this.potionBagContents = potionBagContents == null ? null : potionBagContents.clone();
    }

    /**
     * Returns the player's persisted Quiver contents as a per-slot array, or
     * {@code null} if the Quiver has never been opened. Individual slots may be
     * {@code null} (empty). The returned array is a defensive copy.
     *
     * @return the Quiver contents, or {@code null}
     */
    public ItemStack[] getQuiverContents() {
        return quiverContents == null ? null : quiverContents.clone();
    }

    /**
     * Sets the player's persisted Quiver contents from a per-slot array. The
     * array is copied, so later mutations of the live inventory do not leak into
     * the stored snapshot.
     *
     * @param quiverContents the Quiver contents, may be {@code null}
     */
    public void setQuiverContents(ItemStack[] quiverContents) {
        this.quiverContents = quiverContents == null ? null : quiverContents.clone();
    }

    /**
     * Returns the player's persisted Fishing Bag contents as a per-slot array, or
     * {@code null} if the Fishing Bag has never been opened. Individual slots may
     * be {@code null} (empty). The returned array is a defensive copy.
     *
     * @return the Fishing Bag contents, or {@code null}
     */
    public ItemStack[] getFishingBagContents() {
        return fishingBagContents == null ? null : fishingBagContents.clone();
    }

    /**
     * Sets the player's persisted Fishing Bag contents from a per-slot array. The
     * array is copied, so later mutations of the live inventory do not leak into
     * the stored snapshot.
     *
     * @param fishingBagContents the Fishing Bag contents, may be {@code null}
     */
    public void setFishingBagContents(ItemStack[] fishingBagContents) {
        this.fishingBagContents = fishingBagContents == null ? null : fishingBagContents.clone();
    }

    /**
     * Returns the player's persisted Island Storage contents as a per-slot array,
     * or {@code null} if the storage has never been opened. Individual slots may
     * be {@code null} (empty). The returned array is a defensive copy.
     *
     * @return the Island Storage contents, or {@code null}
     */
    public ItemStack[] getIslandStorageContents() {
        return islandStorageContents == null ? null : islandStorageContents.clone();
    }

    /**
     * Sets the player's persisted Island Storage contents from a per-slot array.
     * The array is copied, so later mutations of the live inventory do not leak
     * into the stored snapshot.
     *
     * @param islandStorageContents the Island Storage contents, may be {@code null}
     */
    public void setIslandStorageContents(ItemStack[] islandStorageContents) {
        this.islandStorageContents = islandStorageContents == null ? null : islandStorageContents.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerProfile other && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerProfile{uuid=" + uuid + ", skills=" + skillXp.size() + '}';
    }
}
