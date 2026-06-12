package com.skyblock.core.npc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton managing registered NPCs and the shop items they sell.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class NpcManager {

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

    private void registerDefaults() {
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
