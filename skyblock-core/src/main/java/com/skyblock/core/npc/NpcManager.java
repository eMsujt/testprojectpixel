package com.skyblock.core.npc;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Singleton managing registered NPCs and the shop items they sell.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class NpcManager {

    /**
     * Built-in NPC shops, each carrying a fixed price map (item display name → coin cost).
     */
    public enum NpcShop {
        SWORD_SMITH("Sword Smith", Map.of(
                "Iron Sword",    200.0,
                "Gold Sword",    400.0,
                "Diamond Sword", 1500.0,
                "Bow",           300.0
        )),
        POTION_BREWER("Potion Brewer", Map.of(
                "Speed Potion",        150.0,
                "Strength Potion",     200.0,
                "Regeneration Potion", 250.0,
                "Jump Boost Potion",   100.0
        )),
        RESOURCE_MERCHANT("Resource Merchant", Map.of(
                "Coal",       5.0,
                "Iron Ingot", 20.0,
                "Gold Ingot", 40.0,
                "Diamond",    250.0
        )),
        DUNGEON_VENDOR("Dungeon Vendor", Map.of(
                "Dungeon Key",     500.0,
                "Dungeon Compass", 1000.0,
                "Revive Stone",    2000.0,
                "Dungeon Orb",     750.0
        ));

        private final String displayName;
        private final Map<String, Double> prices;

        NpcShop(String displayName, Map<String, Double> prices) {
            this.displayName = displayName;
            this.prices = Map.copyOf(prices);
        }

        public String getDisplayName() { return displayName; }

        /** Returns an unmodifiable map of item display name → coin price. */
        public Map<String, Double> getPrices() { return prices; }
    }

    /** Canonical SkyBlock NPC role types. */
    public enum NpcType {
        BANKER("Banker"),
        AUCTION_MASTER("Auction Master"),
        BAZAAR_AGENT("Bazaar Agent"),
        BUILDER("Builder"),
        BLACKSMITH("Blacksmith"),
        LIBRARIAN("Librarian"),
        MERCHANT("Merchant"),
        GUIDE("Guide"),
        CRAFTSMAN("Craftsman"),
        DUNGEON_GUIDE("Dungeon Guide"),
        ISLAND_MERCHANT("Island Merchant"),
        COMMUNITY_SHOP_MERCHANT("Community Shop Merchant");

        public final String displayName;

        NpcType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    /**
     * An item sold by an NPC.
     *
     * @param name     display name shown to players
     * @param material Bukkit Material key (e.g. "IRON_SWORD")
     * @param price    coin cost; must not be negative
     */
    public record ShopItem(String name, String material, double price) {
        public ShopItem {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(material, "material");
            if (price < 0) {
                throw new IllegalArgumentException("price must not be negative, got " + price);
            }
        }
    }

    /**
     * A registered NPC with an associated list of shop items.
     *
     * @param id    unique identifier used in commands (e.g. "blacksmith")
     * @param name  display name shown in messages (e.g. "Blacksmith Bob")
     * @param items items this NPC sells
     */
    public record NpcDefinition(String id, String name, List<ShopItem> items) {
        public NpcDefinition {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(items, "items");
            items = Collections.unmodifiableList(new ArrayList<>(items));
        }
    }

    private static final NpcManager INSTANCE = new NpcManager();

    /** id (lower-case) → NpcDefinition */
    private final Map<String, NpcDefinition> npcs = new LinkedHashMap<>();

    /** entity UUID → NpcDefinition for spawned ArmorStand NPCs */
    private final Map<UUID, NpcDefinition> spawnedEntities = new HashMap<>();

    private NpcManager() {
        registerDefaults();
    }

    /** Returns the single shared {@code NpcManager} instance. */
    public static NpcManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers an NPC.  An existing entry with the same id is replaced.
     *
     * @param npc the definition to register
     */
    public void register(NpcDefinition npc) {
        Objects.requireNonNull(npc, "npc");
        npcs.put(npc.id().toLowerCase(), npc);
    }

    /**
     * Returns the {@link NpcDefinition} for the given id (case-insensitive),
     * or {@code null} if no such NPC is registered.
     *
     * @param id the NPC id to look up
     * @return the definition, or {@code null}
     */
    public NpcDefinition findById(String id) {
        Objects.requireNonNull(id, "id");
        return npcs.get(id.toLowerCase());
    }

    /**
     * Returns an unmodifiable view of all registered NPCs, in registration order.
     *
     * @return all NPC definitions
     */
    public List<NpcDefinition> getAllNpcs() {
        return Collections.unmodifiableList(new ArrayList<>(npcs.values()));
    }

    /**
     * Returns the first item sold by {@code npcId} whose name matches {@code itemName}
     * (case-insensitive), or {@code null} if not found.
     *
     * @param npcId    the NPC id to search
     * @param itemName the item display name
     * @return the matching item, or {@code null}
     */
    public ShopItem findItem(String npcId, String itemName) {
        NpcDefinition npc = findById(npcId);
        if (npc == null) return null;
        for (ShopItem item : npc.items()) {
            if (item.name().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Spawns an invisible, gravity-less {@link ArmorStand} at {@code location}
     * named after {@code npc} and tracks it so {@link NPCListener} can route
     * player interactions back to the correct {@link NpcDefinition}.
     *
     * @param location where to spawn the ArmorStand; must not be null
     * @param npc      the NPC definition to attach; must not be null
     * @return the spawned {@link ArmorStand}
     */
    public ArmorStand spawnNpc(Location location, NpcDefinition npc) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(npc, "npc");
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setCustomName(npc.name());
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        spawnedEntities.put(stand.getUniqueId(), npc);
        return stand;
    }

    /**
     * Returns the {@link NpcDefinition} associated with a spawned ArmorStand,
     * or {@code null} if the entity is not a registered NPC.
     *
     * @param entityId the {@link UUID} of the entity
     * @return the definition, or {@code null}
     */
    public NpcDefinition findByEntity(UUID entityId) {
        return spawnedEntities.get(entityId);
    }

    /**
     * Removes the tracking entry for a despawned NPC entity.
     *
     * @param entityId the {@link UUID} of the entity to forget
     */
    public void removeEntity(UUID entityId) {
        spawnedEntities.remove(entityId);
    }

    private void registerDefaults() {
        for (NpcShop shop : NpcShop.values()) {
            List<ShopItem> items = shop.getPrices().entrySet().stream()
                    .map(e -> new ShopItem(
                            e.getKey(),
                            e.getKey().toUpperCase().replace(" ", "_"),
                            e.getValue()))
                    .collect(Collectors.toList());
            register(new NpcDefinition(shop.name().toLowerCase(), shop.getDisplayName(), items));
        }

        register(new NpcDefinition("blacksmith", "Blacksmith Bob", List.of(
                new ShopItem("Iron Sword", "IRON_SWORD", 200),
                new ShopItem("Iron Pickaxe", "IRON_PICKAXE", 200),
                new ShopItem("Iron Axe", "IRON_AXE", 200),
                new ShopItem("Iron Chestplate", "IRON_CHESTPLATE", 300)
        )));

        register(new NpcDefinition("farmer", "Farmer Joe", List.of(
                new ShopItem("Wheat Seeds", "WHEAT_SEEDS", 1),
                new ShopItem("Carrot", "CARROT", 2),
                new ShopItem("Potato", "POTATO", 2),
                new ShopItem("Melon Seeds", "MELON_SEEDS", 3),
                new ShopItem("Pumpkin Seeds", "PUMPKIN_SEEDS", 3),
                new ShopItem("Nether Wart", "NETHER_WART", 10)
        )));

        register(new NpcDefinition("fisherman", "Fisherman Pete", List.of(
                new ShopItem("Fishing Rod", "FISHING_ROD", 100),
                new ShopItem("Raw Cod", "COD", 3),
                new ShopItem("Raw Salmon", "SALMON", 5)
        )));
    }
}
