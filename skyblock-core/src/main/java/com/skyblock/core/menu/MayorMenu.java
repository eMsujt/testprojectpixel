package com.skyblock.core.menu;

import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.model.Stat;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MayorMenu extends Menu {

    static final int MAYOR_SLOT = 13;
    static final int FIRST_PERK_SLOT = 19;

    private final UUID playerId;

    public MayorMenu(Player player) {
        this(player.getUniqueId());
    }

    public MayorMenu(UUID playerId) {
        super("§6Mayor", 4);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 27; slot < 36; slot++) setItem(slot, pane);

        MayorCandidate mayor = MayorManager.getInstance().getCurrentMayor();

        if (mayor == null) {
            setItem(MAYOR_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Active Mayor")
                    .lore("§7No mayor has been elected yet.")
                    .build());
            return;
        }

        List<String> lore = new ArrayList<>();
        lore.add("§7Active Mayor");
        lore.add("");
        lore.add("§7Perks:");
        for (String perk : mayor.getPerks()) {
            lore.add("  §e" + perk);
        }
        Map<Stat, Double> bonuses = MayorManager.MAYOR_STAT_PERKS.getOrDefault(mayor, Collections.emptyMap());
        if (!bonuses.isEmpty()) {
            lore.add("");
            lore.add("§7Stat Bonuses:");
            for (Map.Entry<Stat, Double> entry : bonuses.entrySet()) {
                lore.add("  §a+" + entry.getValue().intValue() + " " + entry.getKey().getDisplayName());
            }
        }

        setItem(MAYOR_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§6" + mayor.getDisplayName())
                .lore(lore)
                .build());

        List<String> perks = mayor.getPerks();
        for (int i = 0; i < perks.size() && i < contentCapacity(); i++) {
            setItem(contentSlot(i), new ItemBuilder(Material.PAPER)
                    .displayName("§e" + perks.get(i))
                    .lore("§7Active Perk")
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
