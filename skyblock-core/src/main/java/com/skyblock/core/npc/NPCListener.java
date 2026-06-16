package com.skyblock.core.npc;

import com.skyblock.core.npc.NpcManager.NpcDefinition;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Locale;

/**
 * Bukkit listener that intercepts right-click interactions with NPC
 * {@link ArmorStand} entities spawned by {@link NpcManager} and shows the
 * associated shop items to the interacting player.
 */
public final class NPCListener implements Listener {

    private final NpcManager npcManager;

    /**
     * Creates a listener backed by the given {@link NpcManager}.
     *
     * @param npcManager the NPC manager, must not be null
     */
    public NPCListener(NpcManager npcManager) {
        if (npcManager == null) {
            throw new IllegalArgumentException("npcManager must not be null");
        }
        this.npcManager = npcManager;
    }

    /**
     * Detects a right-click on a tracked ArmorStand NPC and displays its
     * shop inventory to the player.
     *
     * @param event the interaction event fired by the server
     */
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }

        NpcDefinition npc = npcManager.findByEntity(event.getRightClicked().getUniqueId());
        if (npc == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        player.sendMessage("=== " + npc.name() + " ===");
        ShopManager.getInstance().getShop(npc.shopId()).ifPresentOrElse(shop -> {
            if (shop.entries().isEmpty()) {
                player.sendMessage("This NPC has nothing for sale.");
            } else {
                for (ShopEntry entry : shop.entries()) {
                    player.sendMessage("- " + formatName(entry.itemId()) + " — " + entry.buyPrice() + " coins");
                }
                player.sendMessage("Use /npc buy " + npc.id() + " <item> to purchase.");
            }
        }, () -> player.sendMessage("This NPC has nothing for sale."));
    }

    private static String formatName(String itemId) {
        StringBuilder sb = new StringBuilder();
        for (String word : itemId.split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }
}
