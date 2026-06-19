package com.skyblock.core.npc;

import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.ShopEntry;
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

/**
 * Singleton managing registered NPCs and the shop items they sell.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class NpcManager {

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
     * A registered NPC whose shop items live in {@link ShopManager} under
     * {@link #shopId()}.
     *
     * @param id     unique identifier used in commands (e.g. "blacksmith")
     * @param name   display name shown in messages (e.g. "Blacksmith Bob")
     * @param shopId the {@link ShopManager} shop id that holds this NPC's wares
     */
    public record NpcDefinition(String id, String name, String shopId) {
        public NpcDefinition {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(shopId, "shopId");
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
        ShopManager sm = ShopManager.getInstance();

        sm.registerShop("sword_smith", "Sword Smith", List.of(
                new ShopEntry("IRON_SWORD",    200,  0),
                new ShopEntry("GOLD_SWORD",    400,  0),
                new ShopEntry("DIAMOND_SWORD", 1500, 0),
                new ShopEntry("BOW",           300,  0)));
        register(new NpcDefinition("sword_smith", "Sword Smith", "sword_smith"));

        sm.registerShop("potion_brewer", "Potion Brewer", List.of(
                new ShopEntry("SPEED_POTION",        150,  0),
                new ShopEntry("STRENGTH_POTION",     200,  0),
                new ShopEntry("REGENERATION_POTION", 250,  0),
                new ShopEntry("JUMP_BOOST_POTION",   100,  0)));
        register(new NpcDefinition("potion_brewer", "Potion Brewer", "potion_brewer"));

        sm.registerShop("resource_merchant", "Resource Merchant", List.of(
                new ShopEntry("COAL",       5,   0),
                new ShopEntry("IRON_INGOT", 20,  0),
                new ShopEntry("GOLD_INGOT", 40,  0),
                new ShopEntry("DIAMOND",    250, 0)));
        register(new NpcDefinition("resource_merchant", "Resource Merchant", "resource_merchant"));

        sm.registerShop("dungeon_vendor", "Dungeon Vendor", List.of(
                new ShopEntry("DUNGEON_KEY",     500,  0),
                new ShopEntry("DUNGEON_COMPASS", 1000, 0),
                new ShopEntry("REVIVE_STONE",    2000, 0),
                new ShopEntry("DUNGEON_ORB",     750,  0)));
        register(new NpcDefinition("dungeon_vendor", "Dungeon Vendor", "dungeon_vendor"));

        sm.registerShop("blacksmith", "Blacksmith Bob", List.of(
                new ShopEntry("IRON_SWORD",      200, 0),
                new ShopEntry("IRON_PICKAXE",    200, 0),
                new ShopEntry("IRON_AXE",        200, 0),
                new ShopEntry("IRON_CHESTPLATE", 300, 0)));
        register(new NpcDefinition("blacksmith", "Blacksmith Bob", "blacksmith"));

        sm.registerShop("farmer", "Farmer Joe", List.of(
                new ShopEntry("WHEAT_SEEDS",  1,  0),
                new ShopEntry("CARROT",       2,  0),
                new ShopEntry("POTATO",       2,  0),
                new ShopEntry("MELON_SEEDS",  3,  0),
                new ShopEntry("PUMPKIN_SEEDS", 3, 0),
                new ShopEntry("NETHER_WART",  10, 0)));
        register(new NpcDefinition("farmer", "Farmer Joe", "farmer"));

        sm.registerShop("fisherman", "Fisherman Pete", List.of(
                new ShopEntry("FISHING_ROD", 100, 0),
                new ShopEntry("COD",         3,   0),
                new ShopEntry("SALMON",      5,   0)));
        register(new NpcDefinition("fisherman", "Fisherman Pete", "fisherman"));

        sm.registerShop("materials_merchant", "Materials Merchant", List.of(
                new ShopEntry("OAK_LOG",      8,  4),
                new ShopEntry("COBBLESTONE",  2,  1),
                new ShopEntry("SAND",         3,  1),
                new ShopEntry("GRAVEL",       3,  1),
                new ShopEntry("CLAY_BALL",    5,  2),
                new ShopEntry("FLINT",        4,  2),
                new ShopEntry("STRING",       6,  3),
                new ShopEntry("LEATHER",     10,  5)));
        register(new NpcDefinition("materials_merchant", "Materials Merchant", "materials_merchant"));

        sm.registerShop("farmer_merchant", "Farmer Merchant", List.of(
                new ShopEntry("WHEAT",         3,  1),
                new ShopEntry("CARROT",        2,  1),
                new ShopEntry("POTATO",        2,  1),
                new ShopEntry("BEETROOT",      4,  2),
                new ShopEntry("PUMPKIN",      10,  5),
                new ShopEntry("MELON_SLICE",   2,  1),
                new ShopEntry("SUGAR_CANE",    2,  1),
                new ShopEntry("COCOA_BEANS",   5,  2)));
        register(new NpcDefinition("farmer_merchant", "Farmer Merchant", "farmer_merchant"));
    }
}
