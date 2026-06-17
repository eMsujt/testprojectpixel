package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking Winter Island Jerry's Workshop progress: gift creation and
 * opening, and each player's snow-minion area in the workshop.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class JerryWorkshopManager {

    /** Wrapping quality of a gift, determining the richness of its reward. */
    public enum GiftQuality {
        WHITE("White Gift", 1),
        GREEN("Green Gift", 2),
        RED("Red Gift", 3);

        private final String displayName;
        private final int tier;

        GiftQuality(String displayName, int tier) {
            this.displayName = displayName;
            this.tier = tier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getTier() {
            return tier;
        }
    }

    private static final JerryWorkshopManager INSTANCE = new JerryWorkshopManager();

    /** Maximum number of snow minions a player may place in their workshop area. */
    public static final int SNOW_MINION_AREA_CAPACITY = 21;

    /** Snow produced per placed snow minion each tick. */
    public static final long SNOW_PER_MINION_PER_TICK = 2L;

    /** Per-player count of gifts the player has wrapped/created. */
    private final Map<UUID, Integer> giftsCreated = new HashMap<>();

    /** Per-player count of gifts the player has given to others. */
    private final Map<UUID, Integer> giftsGiven = new HashMap<>();

    /** Per-player count of gifts the player has received. */
    private final Map<UUID, Integer> giftsReceived = new HashMap<>();

    /** Per-player count of gifts the player has opened. */
    private final Map<UUID, Integer> giftsOpened = new HashMap<>();

    /** Per-player queue of received-but-unopened gifts (oldest first). */
    private final Map<UUID, List<Gift>> pendingGifts = new HashMap<>();

    /** Per-player number of snow minions placed in the workshop area. */
    private final Map<UUID, Integer> snowMinions = new HashMap<>();

    /** Per-player snow produced by the snow-minion area awaiting collection. */
    private final Map<UUID, Long> snow = new HashMap<>();

    private JerryWorkshopManager() {
    }

    /**
     * An immutable wrapped gift: who wrapped it, the contained item, and the
     * wrapping quality.
     */
    public static final class Gift {
        private final UUID sender;
        private final String itemName;
        private final GiftQuality quality;

        public Gift(UUID sender, String itemName, GiftQuality quality) {
            this.sender = Objects.requireNonNull(sender, "sender");
            this.itemName = Objects.requireNonNull(itemName, "itemName");
            this.quality = Objects.requireNonNull(quality, "quality");
        }

        public UUID getSender() {
            return sender;
        }

        public String getItemName() {
            return itemName;
        }

        public GiftQuality getQuality() {
            return quality;
        }
    }

    /**
     * Returns the single shared {@code JerryWorkshopManager} instance.
     *
     * @return the singleton instance
     */
    public static JerryWorkshopManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Gift creation
    // -------------------------------------------------------------------------

    /**
     * Wraps an item into a gift on behalf of the given player.
     *
     * @param sender   the player creating the gift
     * @param itemName the item being wrapped
     * @param quality  the wrapping quality
     * @return the created {@link Gift}
     */
    public Gift createGift(UUID sender, String itemName, GiftQuality quality) {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(itemName, "itemName");
        Objects.requireNonNull(quality, "quality");
        giftsCreated.merge(sender, 1, Integer::sum);
        return new Gift(sender, itemName, quality);
    }

    /**
     * Returns the number of gifts the player has created.
     *
     * @param playerId the player to look up
     * @return the gifts-created count, {@code 0} if none
     */
    public int getGiftsCreated(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return giftsCreated.getOrDefault(playerId, 0);
    }

    // -------------------------------------------------------------------------
    // Gift giving / opening
    // -------------------------------------------------------------------------

    /**
     * Delivers a gift to a recipient, queuing it for them to open and updating
     * the sender's given and recipient's received tallies.
     *
     * @param gift      the gift to deliver
     * @param recipient the player receiving the gift
     */
    public void giveGift(Gift gift, UUID recipient) {
        Objects.requireNonNull(gift, "gift");
        Objects.requireNonNull(recipient, "recipient");
        pendingGifts.computeIfAbsent(recipient, id -> new ArrayList<>()).add(gift);
        giftsGiven.merge(gift.getSender(), 1, Integer::sum);
        giftsReceived.merge(recipient, 1, Integer::sum);
    }

    /**
     * Returns an immutable view of the gifts queued for the player to open.
     *
     * @param playerId the player to look up
     * @return the pending gifts, oldest first (may be empty, never {@code null})
     */
    public List<Gift> getPendingGifts(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<Gift> gifts = pendingGifts.get(playerId);
        return gifts == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(gifts));
    }

    /**
     * Returns the number of gifts queued for the player to open.
     *
     * @param playerId the player to look up
     * @return the pending-gift count, {@code 0} if none
     */
    public int getPendingGiftCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<Gift> gifts = pendingGifts.get(playerId);
        return gifts == null ? 0 : gifts.size();
    }

    /**
     * Opens the player's oldest pending gift, removing it from the queue and
     * incrementing their opened tally.
     *
     * @param playerId the player opening a gift
     * @return the opened {@link Gift}, or {@code null} if the player has none pending
     */
    public Gift openGift(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<Gift> gifts = pendingGifts.get(playerId);
        if (gifts == null || gifts.isEmpty()) {
            return null;
        }
        Gift gift = gifts.remove(0);
        if (gifts.isEmpty()) {
            pendingGifts.remove(playerId);
        }
        giftsOpened.merge(playerId, 1, Integer::sum);
        return gift;
    }

    /**
     * Returns the number of gifts the player has given to others.
     *
     * @param playerId the player to look up
     * @return the gifts-given count, {@code 0} if none
     */
    public int getGiftsGiven(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return giftsGiven.getOrDefault(playerId, 0);
    }

    /**
     * Returns the number of gifts the player has received.
     *
     * @param playerId the player to look up
     * @return the gifts-received count, {@code 0} if none
     */
    public int getGiftsReceived(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return giftsReceived.getOrDefault(playerId, 0);
    }

    /**
     * Returns the number of gifts the player has opened.
     *
     * @param playerId the player to look up
     * @return the gifts-opened count, {@code 0} if none
     */
    public int getGiftsOpened(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return giftsOpened.getOrDefault(playerId, 0);
    }

    // -------------------------------------------------------------------------
    // Snow-minion area
    // -------------------------------------------------------------------------

    /**
     * Returns the number of snow minions the player has placed in their workshop area.
     *
     * @param playerId the player to look up
     * @return the snow-minion count, {@code 0} if none
     */
    public int getSnowMinionCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return snowMinions.getOrDefault(playerId, 0);
    }

    /**
     * Places a snow minion in the player's workshop area, up to
     * {@link #SNOW_MINION_AREA_CAPACITY}.
     *
     * @param playerId the player placing the minion
     * @return {@code true} if placed, {@code false} if the area is already full
     */
    public boolean placeSnowMinion(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int current = getSnowMinionCount(playerId);
        if (current >= SNOW_MINION_AREA_CAPACITY) {
            return false;
        }
        snowMinions.put(playerId, current + 1);
        return true;
    }

    /**
     * Removes a snow minion from the player's workshop area.
     *
     * @param playerId the player removing the minion
     * @return {@code true} if a minion was removed, {@code false} if the area was empty
     */
    public boolean removeSnowMinion(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int current = getSnowMinionCount(playerId);
        if (current <= 0) {
            return false;
        }
        snowMinions.put(playerId, current - 1);
        return true;
    }

    /**
     * Advances the player's snow-minion area by one tick, producing
     * {@link #SNOW_PER_MINION_PER_TICK} snow per placed minion.
     *
     * @param playerId the player whose area to tick
     * @return the snow produced this tick ({@code 0} if no minions are placed)
     */
    public long tickSnowMinions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        long produced = (long) getSnowMinionCount(playerId) * SNOW_PER_MINION_PER_TICK;
        if (produced <= 0L) {
            return 0L;
        }
        snow.merge(playerId, produced, Long::sum);
        return produced;
    }

    /**
     * Returns the snow the player's minion area has produced but not yet collected.
     *
     * @param playerId the player to look up
     * @return the uncollected snow, {@code 0} if none
     */
    public long getSnow(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return snow.getOrDefault(playerId, 0L);
    }

    /**
     * Collects all snow the player's minion area has produced, clearing the stored amount.
     *
     * @param playerId the player collecting
     * @return the amount of snow collected
     */
    public long collectSnow(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long collected = snow.remove(playerId);
        return collected == null ? 0L : collected;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "jerryworkshop.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        giftsCreated.clear();
        giftsGiven.clear();
        giftsReceived.clear();
        giftsOpened.clear();
        pendingGifts.clear();
        snowMinions.clear();
        snow.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isSet(key + ".giftsCreated")) {
                    giftsCreated.put(uuid, cfg.getInt(key + ".giftsCreated", 0));
                }
                if (cfg.isSet(key + ".giftsGiven")) {
                    giftsGiven.put(uuid, cfg.getInt(key + ".giftsGiven", 0));
                }
                if (cfg.isSet(key + ".giftsReceived")) {
                    giftsReceived.put(uuid, cfg.getInt(key + ".giftsReceived", 0));
                }
                if (cfg.isSet(key + ".giftsOpened")) {
                    giftsOpened.put(uuid, cfg.getInt(key + ".giftsOpened", 0));
                }
                if (cfg.isSet(key + ".snowMinions")) {
                    snowMinions.put(uuid, cfg.getInt(key + ".snowMinions", 0));
                }
                if (cfg.isSet(key + ".snow")) {
                    snow.put(uuid, cfg.getLong(key + ".snow", 0L));
                }
                if (cfg.isList(key + ".pendingGifts")) {
                    List<Gift> gifts = new ArrayList<>();
                    for (Map<?, ?> raw : cfg.getMapList(key + ".pendingGifts")) {
                        try {
                            UUID sender = UUID.fromString(String.valueOf(raw.get("sender")));
                            String itemName = String.valueOf(raw.get("itemName"));
                            GiftQuality quality = GiftQuality.valueOf(String.valueOf(raw.get("quality")));
                            gifts.add(new Gift(sender, itemName, quality));
                        } catch (IllegalArgumentException | NullPointerException ignored) {}
                    }
                    if (!gifts.isEmpty()) {
                        pendingGifts.put(uuid, gifts);
                    }
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "jerryworkshop.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        Set<UUID> allUuids = new HashSet<>();
        allUuids.addAll(giftsCreated.keySet());
        allUuids.addAll(giftsGiven.keySet());
        allUuids.addAll(giftsReceived.keySet());
        allUuids.addAll(giftsOpened.keySet());
        allUuids.addAll(pendingGifts.keySet());
        allUuids.addAll(snowMinions.keySet());
        allUuids.addAll(snow.keySet());
        for (UUID uuid : allUuids) {
            String key = uuid.toString();
            if (giftsCreated.containsKey(uuid)) {
                cfg.set(key + ".giftsCreated", giftsCreated.get(uuid));
            }
            if (giftsGiven.containsKey(uuid)) {
                cfg.set(key + ".giftsGiven", giftsGiven.get(uuid));
            }
            if (giftsReceived.containsKey(uuid)) {
                cfg.set(key + ".giftsReceived", giftsReceived.get(uuid));
            }
            if (giftsOpened.containsKey(uuid)) {
                cfg.set(key + ".giftsOpened", giftsOpened.get(uuid));
            }
            if (snowMinions.containsKey(uuid)) {
                cfg.set(key + ".snowMinions", snowMinions.get(uuid));
            }
            if (snow.containsKey(uuid)) {
                cfg.set(key + ".snow", snow.get(uuid));
            }
            List<Gift> gifts = pendingGifts.get(uuid);
            if (gifts != null && !gifts.isEmpty()) {
                List<Map<String, Object>> serialized = new ArrayList<>();
                for (Gift gift : gifts) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("sender", gift.getSender().toString());
                    entry.put("itemName", gift.getItemName());
                    entry.put("quality", gift.getQuality().name());
                    serialized.add(entry);
                }
                cfg.set(key + ".pendingGifts", serialized);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save jerryworkshop.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all Jerry's Workshop data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        giftsCreated.remove(playerId);
        giftsGiven.remove(playerId);
        giftsReceived.remove(playerId);
        giftsOpened.remove(playerId);
        pendingGifts.remove(playerId);
        snowMinions.remove(playerId);
        snow.remove(playerId);
    }

    /**
     * Removes all Jerry's Workshop data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = giftsCreated.remove(playerId) != null;
        had |= giftsGiven.remove(playerId) != null;
        had |= giftsReceived.remove(playerId) != null;
        had |= giftsOpened.remove(playerId) != null;
        had |= pendingGifts.remove(playerId) != null;
        had |= snowMinions.remove(playerId) != null;
        had |= snow.remove(playerId) != null;
        return had;
    }
}
