package com.skyblock.plugin.profile;

import java.io.Serializable;
import java.util.HashMap;
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
    private long purse = 0L;
    private long bank = 0L;

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
