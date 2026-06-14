package com.skyblock.plugin.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TradingManager {

    private static final TradingManager INSTANCE = new TradingManager();

    private final Map<UUID, UUID> pendingTradeRequests = new HashMap<>();

    private TradingManager() {}

    public static TradingManager getInstance() {
        return INSTANCE;
    }

    public void sendTradeRequest(UUID sender, UUID receiver) {
        pendingTradeRequests.put(sender, receiver);
    }

    public UUID getPendingRequest(UUID sender) {
        return pendingTradeRequests.get(sender);
    }

    public boolean hasPendingRequest(UUID sender) {
        return pendingTradeRequests.containsKey(sender);
    }

    public boolean cancelTradeRequest(UUID sender) {
        return pendingTradeRequests.remove(sender) != null;
    }

    public void acceptTradeRequest(UUID receiver) {
        pendingTradeRequests.values().remove(receiver);
    }
}
