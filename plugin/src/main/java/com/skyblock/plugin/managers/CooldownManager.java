package com.skyblock.plugin.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {

    private static final CooldownManager INSTANCE = new CooldownManager();

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public static CooldownManager getInstance() {
        return INSTANCE;
    }

    public void setCooldown(UUID uuid, String action, long expireTime) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(action, expireTime);
    }

    public boolean isOnCooldown(UUID uuid, String action) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) {
            return false;
        }
        Long expireTime = playerCooldowns.get(action);
        return expireTime != null && System.currentTimeMillis() < expireTime;
    }

    public long getRemainingMillis(UUID uuid, String action) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) {
            return 0L;
        }
        Long expireTime = playerCooldowns.get(action);
        if (expireTime == null) {
            return 0L;
        }
        long remaining = expireTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0L;
    }

    public void clearCooldown(UUID uuid, String action) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns != null) {
            playerCooldowns.remove(action);
        }
    }

    public void removePlayer(UUID uuid) {
        cooldowns.remove(uuid);
    }
}
