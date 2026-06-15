package com.skyblock.plugin.pets;

import com.skyblock.core.manager.PetManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link PetManager} directly.
 *
 * <p>Retained for backward compatibility only. All methods delegate to
 * {@link PetManager#getInstance()}.</p>
 */
@Deprecated
public final class PetManager implements Listener {

    /** Minimal pet entry used by callers of this deprecated class. */
    public static final class PetEntry {
        private final UUID id;
        private final String type;
        private long xp;
        private int level;
        private String rarity;

        public PetEntry(String type, long xp, int level, String rarity) {
            this(UUID.randomUUID(), type, xp, level, rarity);
        }

        public PetEntry(UUID id, String type, long xp, int level, String rarity) {
            this.id = id;
            this.type = type;
            this.xp = xp;
            this.level = level;
            this.rarity = rarity;
        }

        public UUID getId()       { return id; }
        public String getType()   { return type; }
        public long getXp()       { return xp; }
        public int getLevel()     { return level; }
        public String getRarity() { return rarity; }

        public void setXp(long xp)           { this.xp = xp; }
        public void setLevel(int level)      { this.level = level; }
        public void setRarity(String rarity) { this.rarity = rarity; }
    }

    /** An immutable pet type definition (retained for callers that used this record). */
    public record PetDefinition(String id, String rarity, Map<String, Double> baseStats,
                                List<Integer> xpThresholds) {
        public PetDefinition {
            baseStats = Collections.unmodifiableMap(new LinkedHashMap<>(baseStats));
            xpThresholds = Collections.unmodifiableList(new ArrayList<>(xpThresholds));
        }
    }

    private static final PetManager INSTANCE = new PetManager();
    private final com.skyblock.core.manager.PetManager delegate =
            com.skyblock.core.manager.PetManager.getInstance();

    private PetManager() {
    }

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /** No-op: definitions now live in {@link com.skyblock.core.manager.PetManager#PET_DATA}. */
    public void load(JavaPlugin plugin) {
    }

    public List<PetEntry> getPets(UUID playerId) {
        List<PetEntry> result = new ArrayList<>();
        for (com.skyblock.core.manager.PetManager.Pet pet : delegate.getPets(playerId)) {
            long xp = delegate.getExperience(playerId, pet.type);
            int level = delegate.getLevel(playerId, pet.type);
            result.add(new PetEntry(pet.id, pet.type.name(), xp, level, pet.rarity.name()));
        }
        return Collections.unmodifiableList(result);
    }

    public void addPet(UUID playerId, PetEntry pet) {
        try {
            com.skyblock.core.manager.PetManager.PetType type =
                    com.skyblock.core.manager.PetManager.PetType.valueOf(pet.getType());
            com.skyblock.core.manager.PetManager.PetRarity rarity =
                    com.skyblock.core.manager.PetManager.PetRarity.valueOf(pet.getRarity());
            delegate.addPet(playerId, type, rarity);
            delegate.addExperience(playerId, type, pet.getXp());
        } catch (IllegalArgumentException ignored) {
            // unknown type or rarity — skip silently
        }
    }

    public boolean removePet(UUID playerId, UUID petId) {
        return delegate.removePet(playerId, petId);
    }

    public PetEntry getActivePet(UUID playerId) {
        com.skyblock.core.manager.PetManager.Pet pet = delegate.getActivePet(playerId);
        if (pet == null) {
            return null;
        }
        long xp = delegate.getExperience(playerId, pet.type);
        int level = delegate.getLevel(playerId, pet.type);
        return new PetEntry(pet.id, pet.type.name(), xp, level, pet.rarity.name());
    }

    public PetEntry load(UUID playerId) {
        return getActivePet(playerId);
    }

    public void setActivePet(UUID playerId, PetEntry pet) {
        if (pet == null) {
            delegate.unequipPet(playerId);
        } else {
            delegate.equipPet(playerId, pet.getId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        delegate.getPets(player.getUniqueId()); // ensure collection is initialized
    }
}
