package com.skyblock.core.manager;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Opens and manages the paired 6-row trade GUIs for two players in an active session.
 *
 * <p>Layout visible to each player (relative to the viewer):
 * <pre>
 *   cols  0-3   | col 4 | cols 5-8
 *   YOUR  ITEMS | GLASS | THEIR ITEMS   (rows 0-4, 20 offer slots per side)
 *   [CANCEL]    | GLASS | [CONFIRM]     (row 5)
 * </pre>
 * Initiator items occupy {@link #OFFER_SLOTS_LEFT} in the initiator's GUI and
 * {@link #OFFER_SLOTS_RIGHT} in the partner's GUI; partner items are mirrored
 * on the opposite sides.
 * </p>
 */
public final class TradingManager {

    /** Left-side offer slots (initiator's items from initiator's view). */
    static final int[] OFFER_SLOTS_LEFT = {
             0,  1,  2,  3,
             9, 10, 11, 12,
            18, 19, 20, 21,
            27, 28, 29, 30,
            36, 37, 38, 39
    };

    /** Right-side offer slots (partner's items from initiator's view). */
    static final int[] OFFER_SLOTS_RIGHT = {
             5,  6,  7,  8,
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35,
            41, 42, 43, 44
    };

    private static final int[] SEPARATOR_SLOTS = {4, 13, 22, 31, 40, 49};

    static final int CANCEL_SLOT  = 45;
    static final int CONFIRM_SLOT = 53;

    private static final ItemStack SEPARATOR = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName("§r").build();
    private static final ItemStack CONFIRM_ITEM = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
            .displayName("§aConfirm Trade").build();
    private static final ItemStack CANCEL_ITEM = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
            .displayName("§cCancel Trade").build();
    private static final ItemStack CONFIRMED_ITEM = new ItemBuilder(Material.LIME_WOOL)
            .displayName("§aConfirmed — waiting for partner…").build();

    /** Player UUID → open trade inventory. */
    private final Map<UUID, Inventory> openInventories = new HashMap<>();

    /** Player UUID → partner UUID (both directions stored). */
    private final Map<UUID, UUID> pairings = new HashMap<>();

    private final TradeManager tradeManager;

    public TradingManager(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    // -------------------------------------------------------------------------
    // GUI lifecycle
    // -------------------------------------------------------------------------

    /**
     * Creates and opens a paired GUI for both players.
     * Each player sees their own items on the left and the partner's items on the right.
     */
    public void openGui(Player initiator, Player partner) {
        UUID iId = initiator.getUniqueId();
        UUID pId = partner.getUniqueId();

        Inventory iInv = Bukkit.createInventory(null, 54, "§6Trade §7with §f" + partner.getName());
        Inventory pInv = Bukkit.createInventory(null, 54, "§6Trade §7with §f" + initiator.getName());

        fillFrame(iInv);
        fillFrame(pInv);

        openInventories.put(iId, iInv);
        openInventories.put(pId, pInv);
        pairings.put(iId, pId);
        pairings.put(pId, iId);

        initiator.openInventory(iInv);
        partner.openInventory(pInv);
    }

    /**
     * Closes both GUIs associated with the given player's session and removes all tracking state.
     */
    public void closeGui(UUID playerId) {
        UUID partnerId = pairings.remove(playerId);
        closeAndRemove(playerId);
        if (partnerId != null) {
            pairings.remove(partnerId);
            closeAndRemove(partnerId);
        }
    }

    /** Returns {@code true} if the player currently has a trade GUI open. */
    public boolean hasOpenGui(UUID playerId) {
        return openInventories.containsKey(playerId);
    }

    // -------------------------------------------------------------------------
    // Item / state synchronisation
    // -------------------------------------------------------------------------

    /**
     * Places {@code items} into the left offer slots of the player's own GUI and into
     * the right offer slots of the partner's GUI, keeping both views in sync.
     */
    public void syncOfferedItems(UUID playerId, List<ItemStack> items) {
        Inventory ownInv     = openInventories.get(playerId);
        UUID partnerId       = pairings.get(playerId);
        Inventory partnerInv = partnerId != null ? openInventories.get(partnerId) : null;

        fillSlots(ownInv,     OFFER_SLOTS_LEFT,  items);
        fillSlots(partnerInv, OFFER_SLOTS_RIGHT, items);
    }

    /**
     * Updates the confirm button in both GUIs to reflect whether the player has confirmed.
     */
    public void markConfirmed(UUID playerId, boolean confirmed) {
        ItemStack btn = confirmed ? CONFIRMED_ITEM : CONFIRM_ITEM;

        Inventory ownInv     = openInventories.get(playerId);
        UUID partnerId       = pairings.get(playerId);
        Inventory partnerInv = partnerId != null ? openInventories.get(partnerId) : null;

        if (ownInv != null)     ownInv.setItem(CONFIRM_SLOT, btn);
        if (partnerInv != null) partnerInv.setItem(CONFIRM_SLOT, btn);
    }

    /** Returns the partner UUID for the given player, or {@code null} if no active GUI. */
    public UUID getPartner(UUID playerId) {
        return pairings.get(playerId);
    }

    /**
     * Returns the 0-based index into {@link #OFFER_SLOTS_LEFT} if {@code slot} is one of
     * the left offer slots in the GUI, or {@code -1} if it is not.
     */
    public int offerSlotIndex(int slot) {
        for (int i = 0; i < OFFER_SLOTS_LEFT.length; i++) {
            if (OFFER_SLOTS_LEFT[i] == slot) return i;
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void fillFrame(Inventory inv) {
        for (int sep : SEPARATOR_SLOTS) inv.setItem(sep, SEPARATOR);
        inv.setItem(CANCEL_SLOT,  CANCEL_ITEM);
        inv.setItem(CONFIRM_SLOT, CONFIRM_ITEM);
    }

    private void fillSlots(Inventory inv, int[] slots, List<ItemStack> items) {
        if (inv == null) return;
        for (int i = 0; i < slots.length; i++) {
            inv.setItem(slots[i], i < items.size() ? items.get(i) : null);
        }
    }

    private void closeAndRemove(UUID playerId) {
        Inventory inv = openInventories.remove(playerId);
        if (inv != null) {
            for (HumanEntity viewer : List.copyOf(inv.getViewers())) {
                viewer.closeInventory();
            }
        }
    }
}
