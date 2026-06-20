package com.skyblock.core.listener;

import com.skyblock.core.manager.ItemAbilityManager;
import com.skyblock.core.manager.ManaManager;
import com.skyblock.core.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Triggers item abilities on right-click.
 *
 * <p>When a player right-clicks while holding an item, the item's display name
 * is resolved to an {@link ItemAbilityManager.AbilityType} (e.g. a display name
 * of {@code "Wither Shield"} maps to {@link ItemAbilityManager.AbilityType#WITHER_SHIELD}).
 * If the name names a known ability, activation is gated through
 * {@link ItemAbilityManager#activate} on the player's current mana; on success
 * the mana cost is deducted via {@link ManaManager}.</p>
 */
public final class AbilityListener implements Listener {

    private static final AbilityListener INSTANCE = new AbilityListener();

    private AbilityListener() {}

    public static AbilityListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Only the main hand, and only right-clicks, to avoid double-firing.
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        ItemAbilityManager.AbilityType type = resolveAbility(meta.getDisplayName());
        if (type == null) {
            return;
        }

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        ManaManager mana = ManaManager.getInstance();
        ItemAbilityManager abilities = ItemAbilityManager.getInstance();

        switch (abilities.activate(id, type, mana.getCurrentMana(id))) {
            case SUCCESS -> {
                mana.useMana(id, type.manaCost);
                ChatUtil.send(player, "§bUsed " + format(type) + "§b! (" + type.manaCost + " mana)");
            }
            case NOT_UNLOCKED ->
                    ChatUtil.sendError(player, "You have not unlocked " + format(type) + "§c.");
            case ON_COOLDOWN ->
                    ChatUtil.sendError(player, "That ability is on cooldown ("
                            + abilities.getRemainingCooldown(id, type) + "s).");
            case NOT_ENOUGH_MANA ->
                    ChatUtil.sendError(player, "Not enough mana! (" + type.manaCost + " required)");
        }
    }

    /**
     * Resolves an item display name to an ability type, or {@code null} if the
     * name does not match a known ability. Colour codes are stripped and spaces
     * normalised so {@code "§5Wither Shield"} matches {@code WITHER_SHIELD}.
     */
    private ItemAbilityManager.AbilityType resolveAbility(String displayName) {
        String key = ChatColor.stripColor(displayName).trim().toUpperCase().replace(' ', '_');
        try {
            return ItemAbilityManager.AbilityType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String format(ItemAbilityManager.AbilityType type) {
        String name = type.name().replace('_', ' ').toLowerCase();
        StringBuilder sb = new StringBuilder(name.length());
        boolean cap = true;
        for (char c : name.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : c);
            cap = c == ' ';
        }
        return "§6" + sb.toString();
    }
}
