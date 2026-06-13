package com.skyblock.core.vault;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class VaultManager {

    public enum VaultTier {
        BASIC(50_000L),
        SILVER(500_000L),
        GOLD(5_000_000L),
        DIAMOND(50_000_000L);

        private final long capacity;

        VaultTier(long capacity) {
            this.capacity = capacity;
        }

        public long getCapacity() {
            return capacity;
        }

        public VaultTier next() {
            VaultTier[] values = values();
            int idx = ordinal() + 1;
            return idx < values.length ? values[idx] : null;
        }
    }

    private static final VaultManager INSTANCE = new VaultManager();

    private final Map<UUID, Long> balances = new HashMap<>();
    private final Map<UUID, VaultTier> tiers = new HashMap<>();

    private VaultManager() {}

    public static VaultManager getInstance() {
        return INSTANCE;
    }

    public VaultTier getTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return tiers.getOrDefault(playerId, VaultTier.BASIC);
    }

    public void setTier(UUID playerId, VaultTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        tiers.put(playerId, tier);
    }

    public long getBalance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return balances.getOrDefault(playerId, 0L);
    }

    public long deposit(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        long cap = getTier(playerId).getCapacity();
        long current = getBalance(playerId);
        long newBalance = Math.min(current + amount, cap);
        balances.put(playerId, newBalance);
        return newBalance - current;
    }

    public long withdraw(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        long current = getBalance(playerId);
        long withdrawn = Math.min(current, amount);
        balances.put(playerId, current - withdrawn);
        return withdrawn;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "vault.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        balances.clear();
        tiers.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                balances.put(id, cfg.getLong(key + ".balance", 0L));
                String tierStr = cfg.getString(key + ".tier");
                if (tierStr != null) {
                    try {
                        tiers.put(id, VaultTier.valueOf(tierStr));
                    } catch (IllegalArgumentException ignored) {}
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "vault.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (UUID id : balances.keySet()) {
            cfg.set(id + ".balance", balances.get(id));
        }
        for (UUID id : tiers.keySet()) {
            cfg.set(id + ".tier", tiers.get(id).name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save vault.yml", e);
        }
    }
}
