package com.skyblock.plugin.profile;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Serialisable data holder for a single player's SkyBlock profile.
 *
 * <p>Tracks the player's identity, accumulated experience keyed by skill name,
 * collection progress keyed by collection name, and coin balances. Unlike
 * {@link PlayerProfile} this type carries only plain serialisable state (no
 * live Bukkit inventory references), so it can be persisted directly. Instances
 * are not thread-safe; access them from the server main thread or guard them
 * externally.</p>
 */
public final class SkyBlockProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID uuid;
    private final Map<String, Long> skillXp = new HashMap<>();
    private final Map<String, Long> collectionXp = new HashMap<>();
    private final Map<String, Integer> collectionCounts = new HashMap<>();
    private final List<ItemStack> quiverContents = new ArrayList<>();
    private final List<ItemStack> fishingBagContents = new ArrayList<>();
    private final List<ItemStack> potionBagContents = new ArrayList<>();
    private final List<ItemStack> accessoryBagContents = new ArrayList<>();
    private final List<ItemStack> storageContents = new ArrayList<>();
    private final List<ItemStack> sacksContents = new ArrayList<>();
    private long purse = 0L;
    private long bank = 0L;
    private boolean showSkillNotifications = true;
    private boolean showCollectionNotifications = true;
    private boolean showPetNotifications = true;

    /**
     * Creates a new profile with no accumulated experience.
     *
     * @param uuid unique identifier of the player
     */
    public SkyBlockProfile(UUID uuid) {
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

    /**
     * Returns the player's per-item collection count for the given item.
     *
     * @param item the collection item name
     * @return the number collected, or 0 if never collected
     */
    public int getCollectionCount(String item) {
        Objects.requireNonNull(item, "item");
        return collectionCounts.getOrDefault(item, 0);
    }

    /**
     * Increments the player's per-item collection count by one.
     *
     * @param item the collection item name
     */
    public void incrementCollection(String item) {
        Objects.requireNonNull(item, "item");
        collectionCounts.merge(item, 1, Integer::sum);
    }

    /**
     * Increments the player's per-item collection count by the given amount.
     *
     * @param key    the collection item name
     * @param amount the amount to add, must not be negative
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void incrementCollection(String key, int amount) {
        Objects.requireNonNull(key, "key");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        collectionCounts.merge(key, amount, Integer::sum);
    }

    /**
     * Returns the player's quiver contents, one stack per slot.
     *
     * @return the live, mutable list of quiver arrow stacks
     */
    public List<ItemStack> getQuiverContents() {
        return quiverContents;
    }

    /**
     * Returns the player's fishing bag contents, one stack per slot.
     *
     * @return the live, mutable list of fishing bag stacks
     */
    public List<ItemStack> getFishingBagContents() {
        return fishingBagContents;
    }

    /**
     * Returns the player's potion bag contents, one stack per slot.
     *
     * @return the live, mutable list of potion bag stacks
     */
    public List<ItemStack> getPotionBagContents() {
        return potionBagContents;
    }

    /**
     * Returns the player's accessory bag contents, one stack per slot.
     *
     * @return the live, mutable list of accessory bag stacks
     */
    public List<ItemStack> getAccessoryBagContents() {
        return accessoryBagContents;
    }

    /**
     * Returns the player's personal storage contents, one stack per slot (up to 54).
     *
     * @return the live, mutable list of storage stacks
     */
    public List<ItemStack> getStorageContents() {
        return storageContents;
    }

    /**
     * Returns the player's sacks contents, one stack per slot (up to 36).
     *
     * @return the live, mutable list of sacks stacks
     */
    public List<ItemStack> getSacksContents() {
        return sacksContents;
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
     * Returns whether skill-progress notifications are shown to the player.
     *
     * @return {@code true} if skill notifications are enabled
     */
    public boolean isShowSkillNotifications() {
        return showSkillNotifications;
    }

    /**
     * Sets whether skill-progress notifications are shown to the player.
     *
     * @param showSkillNotifications the new setting
     */
    public void setShowSkillNotifications(boolean showSkillNotifications) {
        this.showSkillNotifications = showSkillNotifications;
    }

    /**
     * Returns whether collection-progress notifications are shown to the player.
     *
     * @return {@code true} if collection notifications are enabled
     */
    public boolean isShowCollectionNotifications() {
        return showCollectionNotifications;
    }

    /**
     * Sets whether collection-progress notifications are shown to the player.
     *
     * @param showCollectionNotifications the new setting
     */
    public void setShowCollectionNotifications(boolean showCollectionNotifications) {
        this.showCollectionNotifications = showCollectionNotifications;
    }

    /**
     * Returns whether pet-related notifications are shown to the player.
     *
     * @return {@code true} if pet notifications are enabled
     */
    public boolean isShowPetNotifications() {
        return showPetNotifications;
    }

    /**
     * Sets whether pet-related notifications are shown to the player.
     *
     * @param showPetNotifications the new setting
     */
    public void setShowPetNotifications(boolean showPetNotifications) {
        this.showPetNotifications = showPetNotifications;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkyBlockProfile other && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "SkyBlockProfile{uuid=" + uuid + ", skills=" + skillXp.size() + '}';
    }
}
