package com.skyblock.core.item.manager;

import com.skyblock.core.SkyBlockPlugin;
import com.skyblock.core.items.manager.CustomItemManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

/**
 * Singleton that stamps a SkyBlock item id onto an {@link ItemStack} via
 * {@link PersistentDataContainer} and reads it back.
 */
public final class SkyBlockItemManager {

    private static final SkyBlockItemManager INSTANCE = new SkyBlockItemManager();

    private final NamespacedKey idKey;
    private final NamespacedKey rarityKey;
    private final CustomItemManager customItemManager;

    private SkyBlockItemManager() {
        SkyBlockPlugin plugin = SkyBlockPlugin.getInstance();
        this.idKey = new NamespacedKey(plugin, "skyblock_id");
        this.rarityKey = new NamespacedKey(plugin, "skyblock_rarity");
        this.customItemManager = new CustomItemManager();
    }

    /**
     * Returns the single shared {@code SkyBlockItemManager} instance.
     *
     * @return the singleton instance
     */
    public static SkyBlockItemManager getInstance() {
        return INSTANCE;
    }

    /**
     * Writes the SkyBlock item id and rarity into the item's
     * {@link PersistentDataContainer}.
     *
     * @param item the item to tag; must have {@link ItemMeta}
     * @param id   the unique SkyBlock item id, must not be null or blank
     * @throws IllegalArgumentException if {@code id} is null or blank, or if the item has no meta
     */
    public void setItemId(ItemStack item, String id) {
        Objects.requireNonNull(item, "item");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("item has no ItemMeta");
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(idKey, PersistentDataType.STRING, id);
        customItemManager.getById(id).ifPresent(def ->
                pdc.set(rarityKey, PersistentDataType.STRING, def.getRarity().name()));
        item.setItemMeta(meta);
    }

    /**
     * Returns the SkyBlock item id stored in the item's
     * {@link PersistentDataContainer}, if present.
     *
     * @param item the item to read; may be null
     * @return the id, or empty if the item is not a tagged SkyBlock item
     */
    public Optional<String> getItemId(ItemStack item) {
        if (item == null) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING));
    }

    /**
     * Returns the rarity stored in the item's {@link PersistentDataContainer},
     * if present.
     *
     * @param item the item to read; may be null
     * @return the rarity, or empty if not tagged
     */
    public Optional<CustomItemManager.Rarity> getRarity(ItemStack item) {
        if (item == null) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }
        String raw = meta.getPersistentDataContainer().get(rarityKey, PersistentDataType.STRING);
        if (raw == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(CustomItemManager.Rarity.valueOf(raw));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns {@code true} if the item carries a SkyBlock id tag.
     *
     * @param item the item to check; may be null
     * @return {@code true} if the item is a tagged SkyBlock item
     */
    public boolean isSkyBlockItem(ItemStack item) {
        return getItemId(item).isPresent();
    }

    /**
     * Looks up the {@link CustomItemManager.SkyBlockItem} definition for the
     * id stored on the given item, if any.
     *
     * @param item the item to read; may be null
     * @return the matching definition, or empty
     */
    public Optional<CustomItemManager.SkyBlockItem> getDefinition(ItemStack item) {
        return getItemId(item).flatMap(customItemManager::getById);
    }
}
