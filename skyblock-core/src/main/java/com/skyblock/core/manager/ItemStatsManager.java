package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class ItemStatsManager implements Listener {

    private static final ItemStatsManager INSTANCE = new ItemStatsManager();

    private ItemStatsManager() {}

    public static ItemStatsManager getInstance() {
        return INSTANCE;
    }

    public void start(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        rescanArmor(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        // schedule 1 tick so the inventory state reflects the completed click
        player.getServer().getScheduler().runTaskLater(
                JavaPlugin.getProvidingPlugin(ItemStatsManager.class),
                () -> rescanArmor(player),
                1L);
    }

    private void rescanArmor(Player player) {
        UUID id = player.getUniqueId();
        StatManager sm = StatManager.getInstance();
        sm.clearBonuses(id);
        ItemStatManager ism = ItemStatManager.getInstance();
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (piece == null) continue;
            Map<Stat, Integer> stats = ism.getStats(piece);
            for (Map.Entry<Stat, Integer> entry : stats.entrySet()) {
                sm.addBonus(id, entry.getKey(), entry.getValue());
            }
        }
    }
}
