package com.skyblock.core.mailbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player mailbox item deliveries.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MailboxManager {

    private static final MailboxManager INSTANCE = new MailboxManager();

    /** Pending deliveries: recipient UUID → list of item descriptions. */
    private final Map<UUID, List<String>> deliveries = new HashMap<>();

    private MailboxManager() {}

    public static MailboxManager getInstance() {
        return INSTANCE;
    }

    public void addDelivery(UUID recipient, String itemDescription) {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(itemDescription, "itemDescription");
        deliveries.computeIfAbsent(recipient, k -> new ArrayList<>()).add(itemDescription);
    }

    public List<String> getDeliveries(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(deliveries.getOrDefault(player, Collections.emptyList()));
    }

    public boolean claimAll(UUID player) {
        Objects.requireNonNull(player, "player");
        List<String> items = deliveries.remove(player);
        return items != null && !items.isEmpty();
    }

    public boolean hasDeliveries(UUID player) {
        Objects.requireNonNull(player, "player");
        List<String> items = deliveries.get(player);
        return items != null && !items.isEmpty();
    }
}
