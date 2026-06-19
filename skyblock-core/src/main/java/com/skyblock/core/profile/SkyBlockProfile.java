package com.skyblock.core.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Per-player SkyBlock profile state: skills, collections, economy, and progression. */
public final class SkyBlockProfile {

    private final UUID uuid;
    private final Map<String, Long> skillXP = new HashMap<>();
    private final Map<String, Long> collections = new HashMap<>();
    private long bankBalance = 0L;
    private long purse = 0L;
    private int fairySouls = 0;
    private String activePet = null;

    public SkyBlockProfile(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
    }

    public UUID getUuid() { return uuid; }

    // -------------------------------------------------------------------------
    // Skill XP
    // -------------------------------------------------------------------------

    public Map<String, Long> getSkillXP() {
        return Collections.unmodifiableMap(skillXP);
    }

    public long getSkillXP(String skill) {
        return skillXP.getOrDefault(Objects.requireNonNull(skill, "skill"), 0L);
    }

    public void setSkillXP(String skill, long xp) {
        Objects.requireNonNull(skill, "skill");
        if (xp < 0) throw new IllegalArgumentException("xp must not be negative");
        skillXP.put(skill, xp);
    }

    public void addSkillXP(String skill, long amount) {
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        skillXP.merge(skill, amount, Long::sum);
    }

    // -------------------------------------------------------------------------
    // Collections
    // -------------------------------------------------------------------------

    public Map<String, Long> getCollections() {
        return Collections.unmodifiableMap(collections);
    }

    public long getCollection(String key) {
        return collections.getOrDefault(Objects.requireNonNull(key, "key"), 0L);
    }

    public void setCollection(String key, long amount) {
        Objects.requireNonNull(key, "key");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        collections.put(key, amount);
    }

    public void addCollection(String key, long amount) {
        Objects.requireNonNull(key, "key");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        collections.merge(key, amount, Long::sum);
    }

    // -------------------------------------------------------------------------
    // Economy
    // -------------------------------------------------------------------------

    public long getBankBalance() { return bankBalance; }

    public void setBankBalance(long bankBalance) {
        if (bankBalance < 0) throw new IllegalArgumentException("bankBalance must not be negative");
        this.bankBalance = bankBalance;
    }

    public void addBankBalance(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        this.bankBalance += amount;
    }

    public long getPurse() { return purse; }

    public void setPurse(long purse) {
        if (purse < 0) throw new IllegalArgumentException("purse must not be negative");
        this.purse = purse;
    }

    public void addPurse(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        this.purse += amount;
    }

    // -------------------------------------------------------------------------
    // Misc progression
    // -------------------------------------------------------------------------

    public int getFairySouls() { return fairySouls; }

    public void setFairySouls(int fairySouls) {
        if (fairySouls < 0) throw new IllegalArgumentException("fairySouls must not be negative");
        this.fairySouls = fairySouls;
    }

    public void addFairySouls(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        this.fairySouls += amount;
    }

    public String getActivePet() { return activePet; }

    public void setActivePet(String activePet) { this.activePet = activePet; }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkyBlockProfile other && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() { return uuid.hashCode(); }

    @Override
    public String toString() {
        return "SkyBlockProfile{uuid=" + uuid + ", skills=" + skillXP.size()
                + ", collections=" + collections.size() + ", bank=" + bankBalance + '}';
    }
}
