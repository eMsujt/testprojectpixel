package com.skyblock.core.listener;

import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.KuudraManager.KuudraTier;
import com.skyblock.core.manager.KuudraManager.KuudraRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public final class KuudraListener implements Listener {

    private static final String METADATA_KEY = "kuudra_tier";

    private static final KuudraListener INSTANCE = new KuudraListener();

    private final KuudraManager kuudraManager = KuudraManager.getInstance();

    private KuudraListener() {}

    public static KuudraListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        List<MetadataValue> meta = entity.getMetadata(METADATA_KEY);
        if (meta.isEmpty()) return;

        KuudraTier tier;
        try {
            tier = KuudraTier.valueOf(meta.get(0).asString());
        } catch (IllegalArgumentException e) {
            return;
        }

        // Complete the run for every online player whose active run matches this tier.
        for (Player player : Bukkit.getOnlinePlayers()) {
            KuudraRun run = kuudraManager.getActiveRun(player.getUniqueId());
            if (run == null || run.getTier() != tier) continue;

            // Advance through any remaining phases until the run reaches BURN.
            while (!run.isFinalPhase()) {
                kuudraManager.advancePhase(player.getUniqueId());
            }
            kuudraManager.completeRun(player.getUniqueId());
        }
    }
}
