package com.skyblock.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Lightweight data holder for a single player's SkyBlock profile state. */
public final class PlayerProfile {

    private final UUID uuid;
    private double coins;
    private final Map<String, Integer> skillXP = new HashMap<>();
    private int fairySouls;

    public PlayerProfile(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
    }

    public UUID getUuid() { return uuid; }

    public double getCoins() { return coins; }

    public void setCoins(double coins) {
        if (coins < 0) throw new IllegalArgumentException("coins must not be negative");
        this.coins = coins;
    }

    public void addCoins(double amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        this.coins += amount;
    }

    public Map<String, Integer> getSkillXP() {
        return Collections.unmodifiableMap(skillXP);
    }

    public int getSkillXP(String skill) {
        return skillXP.getOrDefault(Objects.requireNonNull(skill, "skill"), 0);
    }

    public void setSkillXP(String skill, int xp) {
        Objects.requireNonNull(skill, "skill");
        if (xp < 0) throw new IllegalArgumentException("xp must not be negative");
        skillXP.put(skill, xp);
    }

    public void addSkillXP(String skill, int amount) {
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        skillXP.merge(skill, amount, Integer::sum);
    }

    public int getFairySouls() { return fairySouls; }

    public void setFairySouls(int fairySouls) {
        if (fairySouls < 0) throw new IllegalArgumentException("fairySouls must not be negative");
        this.fairySouls = fairySouls;
    }

    public void addFairySouls(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        this.fairySouls += amount;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerProfile other && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() { return uuid.hashCode(); }

    @Override
    public String toString() {
        return "PlayerProfile{uuid=" + uuid + ", coins=" + coins + ", fairySouls=" + fairySouls + '}';
    }
}
