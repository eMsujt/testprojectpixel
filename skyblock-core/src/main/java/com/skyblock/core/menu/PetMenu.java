package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PetMenu extends Menu {

    /** Wool material used to represent each rarity tier. */
    static final Map<Rarity, Material> RARITY_WOOL = new EnumMap<>(Rarity.class);

    static {
        RARITY_WOOL.put(Rarity.COMMON,      Material.WHITE_WOOL);
        RARITY_WOOL.put(Rarity.UNCOMMON,    Material.LIME_WOOL);
        RARITY_WOOL.put(Rarity.RARE,        Material.BLUE_WOOL);
        RARITY_WOOL.put(Rarity.EPIC,        Material.PURPLE_WOOL);
        RARITY_WOOL.put(Rarity.LEGENDARY,   Material.ORANGE_WOOL);
        RARITY_WOOL.put(Rarity.MYTHIC,      Material.PINK_WOOL);
        RARITY_WOOL.put(Rarity.DIVINE,      Material.CYAN_WOOL);
        RARITY_WOOL.put(Rarity.SPECIAL,     Material.RED_WOOL);
    }

    /** Width of the textual XP progress bar rendered in each pet's lore. */
    private static final int XP_BAR_SEGMENTS = 20;

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final UUID playerId;
    private final int page;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public PetMenu(Player player) {
        this(player.getUniqueId(), 0);
    }

    public PetMenu(UUID playerId) {
        this(playerId, 0);
    }

    private PetMenu(UUID playerId, int page) {
        super("§dPets", 6);
        this.playerId = playerId;
        this.page = page;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();

        PetManager petManager = PetManager.getInstance();
        List<Pet> owned = new ArrayList<>(petManager.getPets(playerId));
        Pet activePet = petManager.getActivePet(playerId);
        UUID activeId = activePet != null ? activePet.id : null;

        // Group by rarity (highest tier first), then strongest pet within each group.
        owned.sort(Comparator
                .comparingInt((Pet p) -> p.rarity.ordinal()).reversed()
                .thenComparing(Comparator.comparingInt((Pet p) -> petManager.getLevel(playerId, p.type)).reversed())
                .thenComparing(p -> p.type.getDisplayName()));

        int totalPages = Math.max(1, (int) Math.ceil((double) owned.size() / SLOTS_PER_PAGE));
        inventory = Bukkit.createInventory(this, 54, "§dPets");

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) inventory.setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) inventory.setItem(slot, pane);

        int start = page * SLOTS_PER_PAGE;
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int idx = start + i;
            if (idx >= owned.size()) break;
            Pet pet = owned.get(idx);
            boolean equipped = pet.id.equals(activeId);
            int level = petManager.getLevel(playerId, pet.type);
            long xp = petManager.getExperience(playerId, pet.type);
            String rarityColor = ItemBuilder.rarityColor(pet.rarity.name()).toString();
            Material wool = RARITY_WOOL.getOrDefault(pet.rarity, Material.WHITE_WOOL);
            ItemStack item = new ItemBuilder(wool)
                    .displayName(rarityColor + (equipped ? "✦ " : "") + "[Lvl " + level + "] " + pet.type.getDisplayName())
                    .lore(
                            "§7Rarity: " + rarityColor + pet.rarity.getDisplayName(),
                            "§7Level: §a" + level + "§7/§a" + PetManager.MAX_LEVEL,
                            "§7XP: " + xpBar(xp, level, pet.type.defaultRarity),
                            equipped ? "§aCurrently equipped" : "§eClick to equip!")
                    .build();
            int slot = INNER_SLOTS[i];
            inventory.setItem(slot, item);
            UUID petId = pet.id;
            handlers.put(slot, event -> {
                if (equipped) {
                    petManager.unequipPet(playerId);
                } else {
                    petManager.equipPet(playerId, petId);
                }
                new PetMenu(playerId, page).open((Player) event.getWhoClicked());
            });
        }

        if (owned.isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You don't own any pets yet.")
                    .build());
        }

        inventory.setItem(49, new ItemBuilder(Material.BONE)
                .displayName("§aPets")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            inventory.setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build());
            handlers.put(45, event -> new PetMenu(playerId, prevPage).open((Player) event.getWhoClicked()));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            inventory.setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build());
            handlers.put(53, event -> new PetMenu(playerId, nextPage).open((Player) event.getWhoClicked()));
        }

        player.openInventory(inventory);
    }

    /**
     * Renders a textual XP progress bar toward the next level, e.g.
     * {@code §a██████§7░░░░ §e42%}. A maxed pet shows a full bar and {@code MAX}.
     *
     * @param xp     the pet's total accumulated experience
     * @param level  the pet's current level (1–{@link PetManager#MAX_LEVEL})
     * @param rarity the rarity whose XP table governs progression
     */
    private static String xpBar(long xp, int level, Rarity rarity) {
        long[] table = PetManager.PET_XP_TABLE.get(rarity.name());
        if (level >= PetManager.MAX_LEVEL || table == null) {
            return "§a" + "█".repeat(XP_BAR_SEGMENTS) + " §6MAX";
        }
        long prevThreshold = level >= 2 ? table[level - 2] : 0L;
        long nextThreshold = table[level - 1];
        long into = xp - prevThreshold;
        long need = nextThreshold - prevThreshold;
        double fraction = need <= 0 ? 0.0 : Math.max(0.0, Math.min(1.0, (double) into / need));
        int filled = (int) Math.round(fraction * XP_BAR_SEGMENTS);
        return "§a" + "█".repeat(filled) + "§7" + "░".repeat(XP_BAR_SEGMENTS - filled)
                + " §e" + (int) Math.round(fraction * 100) + "%";
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
