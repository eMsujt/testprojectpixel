package com.skyblock.core.menu;

import com.skyblock.core.manager.HarpManager;
import com.skyblock.core.manager.HarpManager.Song;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /harp}. Renders Melody's Harp song list directly
 * from {@link HarpManager}: each song's unlock state and best completion
 * percentage, plus a summary of completed songs and the earned Intelligence
 * bonus.
 */
public final class HarpMenu extends Menu {

    /** Inner content slots for the song tiles (mirrors {@link BestiaryMenu}). */
    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private static final int SUMMARY_SLOT = 4;
    private static final int CLOSE_SLOT   = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public HarpMenu(Player player) {
        this(player.getUniqueId());
    }

    public HarpMenu(UUID playerId) {
        super("§5Melody's Harp", 6);
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

        HarpManager manager = HarpManager.getInstance();

        inventory.setItem(SUMMARY_SLOT, new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName("§dHarp Progress")
                .lore(
                        "§7Songs completed: §e" + manager.getCompletedCount(playerId)
                                + "§7/§e" + Song.values().length,
                        "§7Intelligence bonus: §a+" + manager.getIntelligenceBonus(playerId) + " §b✎")
                .build());

        Song[] songs = Song.values();
        for (int i = 0; i < INNER_SLOTS.length && i < songs.length; i++) {
            Song song = songs[i];
            boolean unlocked = manager.isUnlocked(playerId, song);
            boolean completed = manager.isCompleted(playerId, song);
            int best = manager.getBestCompletion(playerId, song);
            Material icon = !unlocked ? Material.GRAY_DYE
                    : completed ? Material.MUSIC_DISC_CAT : Material.NOTE_BLOCK;
            String status = !unlocked ? "§cLocked"
                    : completed ? "§aCompleted" : "§e" + best + "%";
            inventory.setItem(INNER_SLOTS[i], new ItemBuilder(icon)
                    .displayName((completed ? "§a" : unlocked ? "§f" : "§8") + song.getDisplayName())
                    .lore(
                            "§7Difficulty: §6" + song.getDifficulty(),
                            "§7Best completion: " + (unlocked ? "§e" + best + "%" : "§8—"),
                            "§7Status: " + status,
                            "§7Reward: §b+" + song.getIntelligenceReward() + " Intelligence")
                    .build());
        }

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the harp menu.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
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
