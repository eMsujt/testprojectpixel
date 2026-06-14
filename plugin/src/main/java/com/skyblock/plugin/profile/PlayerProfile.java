package com.skyblock.plugin.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Data holder for a single player's SkyBlock profile.
 *
 * <p>Tracks the player's identity and accumulated experience keyed by skill
 * name. Instances are not thread-safe; access them from the server main thread
 * or guard them externally.</p>
 */
public final class PlayerProfile {

    private final UUID uuid;
    private final Map<String, Long> skillXp = new HashMap<>();

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
