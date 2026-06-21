package com.skyblock.core.listener;

import com.skyblock.core.ability.AbilityEffects;
import com.skyblock.core.ability.LoreAbility;
import com.skyblock.core.manager.ManaManager;
import com.skyblock.core.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Fires real item abilities declared in an item's lore (e.g. Aspect of the End's Instant
 * Transmission). Reads the ability from the held item's lore, picks the one matching the
 * player's sneak state, gates it on mana, and runs the effect. Only abilities with an
 * implemented effect are handled, so non-ability items and unimplemented abilities are ignored.
 */
public final class ItemAbilityListener implements Listener {

    private static final ItemAbilityListener INSTANCE = new ItemAbilityListener();

    private ItemAbilityListener() {}

    public static ItemAbilityListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
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
        if (meta == null || meta.getLore() == null) {
            return;
        }

        List<LoreAbility> abilities = LoreAbility.parse(meta.getLore());
        if (abilities.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        LoreAbility.Trigger want = player.isSneaking()
                ? LoreAbility.Trigger.SNEAK_RIGHT_CLICK
                : LoreAbility.Trigger.RIGHT_CLICK;

        LoreAbility chosen = null;
        for (LoreAbility ability : abilities) {
            if (ability.trigger == want && AbilityEffects.isImplemented(ability.name)) {
                chosen = ability;
                break;
            }
        }
        if (chosen == null) {
            return;
        }

        // From here on this is an ability use — stop the click from also placing/using a block.
        event.setCancelled(true);

        ManaManager mana = ManaManager.getInstance();
        if (mana.getCurrentMana(player.getUniqueId()) < chosen.manaCost) {
            ChatUtil.sendError(player, "Not enough mana! (" + chosen.manaCost + " required)");
            return;
        }
        mana.useMana(player.getUniqueId(), chosen.manaCost);
        AbilityEffects.run(chosen, player);
        ChatUtil.send(player, "§bUsed §6" + chosen.name + "§b! (" + chosen.manaCost + " Mana)");
    }
}
