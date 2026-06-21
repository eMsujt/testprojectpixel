package com.skyblock.core.model;

import java.util.Objects;
import java.util.UUID;

/** Lightweight runtime representation of a connected SkyBlock player. */
public final class SkyBlockPlayer {

    private final UUID uuid;
    private double coins;
    private double bankCoins;
    private int fairySouls;

    public SkyBlockPlayer(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
    }

    public UUID getUuid() { return uuid; }

    public double getCoins() { return coins; }

    public void setCoins(double coins) {
        if (coins < 0) throw new IllegalArgumentException("coins must not be negative");
        this.coins = coins;
    }

    public double getBankCoins() { return bankCoins; }

    public void setBankCoins(double bankCoins) {
        if (bankCoins < 0) throw new IllegalArgumentException("bankCoins must not be negative");
        this.bankCoins = bankCoins;
    }

    public int getFairySouls() { return fairySouls; }

    public void setFairySouls(int fairySouls) {
        if (fairySouls < 0) throw new IllegalArgumentException("fairySouls must not be negative");
        this.fairySouls = fairySouls;
    }
}
