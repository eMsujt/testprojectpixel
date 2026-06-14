package com.skyblock.plugin.managers;

import com.skyblock.trading.TradingManager.TradeRequest;

import java.util.Optional;
import java.util.UUID;

public final class TradingManager {

    private static final TradingManager INSTANCE = new TradingManager();

    private final com.skyblock.trading.TradingManager delegate = new com.skyblock.trading.TradingManager();

    private TradingManager() {}

    public static TradingManager getInstance() {
        return INSTANCE;
    }

    public TradeRequest sendRequest(UUID sender, UUID target) {
        return delegate.sendRequest(sender, target);
    }

    public Optional<TradeRequest> getRequest(UUID sender) {
        return delegate.getRequest(sender);
    }

    public TradeRequest acceptRequest(UUID sender, UUID target) {
        return delegate.acceptRequest(sender, target);
    }

    public boolean declineRequest(UUID sender) {
        return delegate.declineRequest(sender);
    }
}
