package com.skyblock.core.menu;

import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.SlayerBoss;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /slayer}. Renders all six slayer bosses
 * (Revenant Horror, Tarantula Broodfather, Sven Packmaster, Voidgloom Seraph,
 * Inferno Demonlord and Riftstalker Bloodfiend) directly from
 * {@link SlayerManager}, showing the player's level, XP and total kills for
 * each underlying {@link SlayerType}.
 */
public final class SlayerMenu extends Menu {

    private static final int SUMMARY_SLOT = 4;
    private static final int CLOSE_SLOT    = 49;
    /** First slot of the boss row; bosses occupy {@code BOSS_START_SLOT .. +5}. */
    private static final int BOSS_START_SLOT = 19;

    private static final Map<SlayerType, Material> ICONS = new EnumMap<>(SlayerType.class);

    static {
        ICONS.put(SlayerType.ZOMBIE,   Material.ROTTEN_FLESH);
        ICONS.put(SlayerType.SPIDER,   Material.SPIDER_EYE);
        ICONS.put(SlayerType.WOLF,     Material.BONE);
        ICONS.put(SlayerType.ENDERMAN, Material.ENDER_PEARL);
        ICONS.put(SlayerType.BLAZE,    Material.BLAZE_POWDER);
        ICONS.put(SlayerType.VAMPIRE,  Material.REDSTONE);
    }

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public SlayerMenu(Player player) {
        this(player.getUniqueId());
    }

    public SlayerMenu(UUID playerId) {
        super("§cSlayers", 6);
        this.playerId = playerId;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();
        inventory = Bukkit.createInventory(this, 54, getTitle());

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) inventory.setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) inventory.setItem(slot, pane);

        SlayerManager manager = SlayerManager.getInstance();

        inventory.setItem(SUMMARY_SLOT, new ItemBuilder(Material.DIAMOND_SWORD)
                .displayName("§cSlayer Bosses")
                .lore(
                        "§7Defeat slayer bosses to earn",
                        "§7slayer XP and rare drops.",
                        "",
                        "§7Click a boss to view your progress.")
                .build());

        SlayerBoss[] bosses = SlayerBoss.values();
        for (int i = 0; i < bosses.length; i++) {
            SlayerBoss boss = bosses[i];
            SlayerType type = boss.type;
            int level = manager.getLevel(playerId, type);
            long xp = manager.getExperience(playerId, type);
            int kills = manager.getKillCount(playerId, type);
            int maxLevel = maxLevel(type);

            int slot = BOSS_START_SLOT + i;
            inventory.setItem(slot, new ItemBuilder(ICONS.getOrDefault(type, Material.SKELETON_SKULL))
                    .displayName("§c" + boss.getDisplayName())
                    .lore(
                            "§7Type: §e" + type.getDisplayName(),
                            "§7Level: §e" + level + "§7/§e" + maxLevel,
                            "§7XP: §e" + xp,
                            "§7Bosses slain: §e" + kills)
                    .build());
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the slayer menu.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    /** Returns the maximum slayer level for the given type from {@link SlayerManager#SLAYER_BOSS_DATA}. */
    private static int maxLevel(SlayerType type) {
        int[] data = SlayerManager.SLAYER_BOSS_DATA.get(type.name());
        return data != null ? data[0] : SlayerManager.MAX_LEVEL;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
