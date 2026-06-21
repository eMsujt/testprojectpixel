package com.skyblock.core.model;

import java.util.Objects;
import java.util.UUID;

/** Data class wrapping a player UUID with core economy and progression fields. */
public final class SkyBlockPlayer {

    private final UUID uuid;
    private double coins;
    private double bankBalance;
    private int fairySouls;
    private int level;

    public SkyBlockPlayer(UUID uuid) {
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

    public double getBankBalance() { return bankBalance; }

    public void setBankBalance(double bankBalance) {
        if (bankBalance < 0) throw new IllegalArgumentException("bankBalance must not be negative");
        this.bankBalance = bankBalance;
    }

    public void addBankBalance(double amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        this.bankBalance += amount;
    }

    public int getFairySouls() { return fairySouls; }

    public void setFairySouls(int fairySouls) {
        if (fairySouls < 0) throw new IllegalArgumentException("fairySouls must not be negative");
        this.fairySouls = fairySouls;
    }

    public int getLevel() { return level; }

    public void setLevel(int level) {
        if (level < 0) throw new IllegalArgumentException("level must not be negative");
        this.level = level;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkyBlockPlayer other && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() { return uuid.hashCode(); }

    @Override
    public String toString() {
        return "SkyBlockPlayer{uuid=" + uuid + ", coins=" + coins + ", bankBalance=" + bankBalance
                + ", fairySouls=" + fairySouls + ", level=" + level + '}';
    }
}
