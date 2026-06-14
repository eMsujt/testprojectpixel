package com.skyblock.plugin.pets;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton registry for each player's pet collection and active pet.
 *
 * <p>Registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}; on
 * {@link PlayerJoinEvent} the joining player's active pet is loaded so the rest
 * of the plugin can query it for an online player.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class PetManager implements Listener {

    public enum PetType {
        BEE, CHICKEN, WOLF, ENDERMAN, SPIDER, RABBIT, HORSE, SHEEP,
        BLAZE, SKELETON, ZOMBIE, OCELOT, MAGMA_CUBE, FLYING_FISH,
        JERRY, ROCK, WITHER_SKELETON, BLUE_WHALE, TIGER, LION
    }

    public static final class PetEntry {
        private final UUID id;
        private final PetType type;
        private long xp;
        private int level;
        private String rarity;

        public PetEntry(PetType type, long xp, int level, String rarity) {
            this(UUID.randomUUID(), type, xp, level, rarity);
        }

        public PetEntry(UUID id, PetType type, long xp, int level, String rarity) {
            this.id = id;
            this.type = type;
            this.xp = xp;
            this.level = level;
            this.rarity = rarity;
        }

        public UUID getId()       { return id; }
        public PetType getType()  { return type; }
        public long getXp()       { return xp; }
        public int getLevel()     { return level; }
        public String getRarity() { return rarity; }

        public void setXp(long xp)          { this.xp = xp; }
        public void setLevel(int level)     { this.level = level; }
        public void setRarity(String rarity) { this.rarity = rarity; }
    }

    private static final PetManager INSTANCE = new PetManager();

    private final Map<UUID, List<PetEntry>> playerPets = new HashMap<>();
    private final Map<UUID, PetEntry> activePets = new HashMap<>();

    private PetManager() {}

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /** Returns an unmodifiable view of a player's owned pets. */
    public List<PetEntry> getPets(UUID playerId) {
        return Collections.unmodifiableList(playerPets.getOrDefault(playerId, Collections.emptyList()));
    }

    /** Adds a pet to the player's collection. */
    public void addPet(UUID playerId, PetEntry pet) {
        playerPets.computeIfAbsent(playerId, k -> new ArrayList<>()).add(pet);
    }

    /** Removes a pet from the player's collection by its UUID. Returns {@code true} if found and removed. */
    public boolean removePet(UUID playerId, UUID petId) {
        List<PetEntry> pets = playerPets.get(playerId);
        if (pets == null) return false;
        boolean removed = pets.removeIf(e -> e.getId().equals(petId));
        PetEntry active = activePets.get(playerId);
        if (removed && active != null && active.getId().equals(petId)) {
            activePets.remove(playerId);
        }
        return removed;
    }

    /** Returns the player's active pet, or {@code null} if none is summoned. */
    public PetEntry getActivePet(UUID playerId) {
        return activePets.get(playerId);
    }

    /**
     * Loads the given player's active pet, ensuring their pet collection is
     * initialised so it is ready for use.
     *
     * @param playerId unique identifier of the player
     * @return the player's active pet, or {@code null} if none is summoned
     */
    public PetEntry load(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerPets.computeIfAbsent(playerId, k -> new ArrayList<>());
        return activePets.get(playerId);
    }

    /**
     * Loads the joining player's active pet so it is ready for use.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        load(player.getUniqueId());
    }

    /**
     * Sets the player's active pet. The pet must already be in the player's
     * collection. Pass {@code null} to despawn the current pet.
     */
    public void setActivePet(UUID playerId, PetEntry pet) {
        if (pet == null) {
            activePets.remove(playerId);
        } else {
            activePets.put(playerId, pet);
        }
    }
}
