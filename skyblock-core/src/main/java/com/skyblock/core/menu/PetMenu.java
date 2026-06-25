package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The "Pets" menu, opened from the SkyBlock Menu. Laid out 1:1 with Hypixel: a
 * summary info head at slot 4, the player's pets across the inner 7×4 grid
 * (slots 10–43, the documented pet-stack region), and a Go Back arrow at slot 48.
 * Each pet shows {@code [Lvl n] Name}, rarity, category and XP progress; clicking
 * equips or unequips it.
 */
public final class PetMenu extends AbstractSkyBlockMenu {

    public static final Map<Rarity, Material> RARITY_WOOL;

    static {
        Map<Rarity, Material> m = new EnumMap<>(Rarity.class);
        m.put(Rarity.COMMON,    Material.WHITE_WOOL);
        m.put(Rarity.UNCOMMON,  Material.LIME_WOOL);
        m.put(Rarity.RARE,      Material.BLUE_WOOL);
        m.put(Rarity.EPIC,      Material.PURPLE_WOOL);
        m.put(Rarity.LEGENDARY, Material.ORANGE_WOOL);
        m.put(Rarity.MYTHIC,    Material.PINK_WOOL);
        m.put(Rarity.DIVINE,    Material.CYAN_WOOL);
        m.put(Rarity.SPECIAL,   Material.RED_WOOL);
        RARITY_WOOL = Collections.unmodifiableMap(m);
    }

    /** Pet list sort orders, cycled by the Sort button (mirrors Hypixel's Pets menu). */
    private enum SortMode {
        LEVEL("Highest Level"), RARITY("Highest Rarity"), NAME("Name");
        final String label;
        SortMode(String label) { this.label = label; }
        SortMode next() { return values()[(ordinal() + 1) % values().length]; }
    }

    private final int page;
    private final SortMode sort;

    public PetMenu(Player player) {
        this(player, 0, SortMode.LEVEL);
    }

    private PetMenu(Player player, int page, SortMode sort) {
        super(player, "§9Pets", 6);
        this.page = page;
        this.sort = sort;
    }

    @Override
    protected void populate() {
        drawBorder(Material.BLACK_STAINED_GLASS_PANE);

        PetManager manager = PetManager.getInstance();
        UUID playerId = player.getUniqueId();
        UUID activePetId = manager.getActivePetId(playerId);

        // Sort a copy of the pet list by the active sort order.
        List<Pet> pets = new ArrayList<>(manager.getPets(playerId));
        sortPets(pets, manager, playerId);

        int capacity = contentCapacity();
        int totalPages = Math.max(1, (pets.size() + capacity - 1) / capacity);
        int pageClamped = Math.max(0, Math.min(page, totalPages - 1));
        int start = pageClamped * capacity;

        // Summary info head at slot 4.
        String activeName = "§7None";
        if (activePetId != null) {
            for (Pet p : pets) {
                if (p.id.equals(activePetId)) {
                    activeName = SkyblockUtils.rarityColor(p.rarity) + p.type.getDisplayName();
                    break;
                }
            }
        }
        setItem(4, new ItemBuilder(Material.BONE)
                .displayName("§aPets")
                .lore(
                        "§7Selected Pet: " + activeName,
                        "§7Total Pets: §e" + pets.size(),
                        "§7Page: §e" + (pageClamped + 1) + "§7/§e" + totalPages,
                        "",
                        "§7View and manage your pets.")
                .build(), e -> e.setCancelled(true));

        for (int i = start; i < pets.size() && i < start + capacity; i++) {
            Pet pet = pets.get(i);
            boolean isActive = pet.id.equals(activePetId);
            long xp = manager.getExperience(playerId, pet.type);
            int level = manager.getLevel(playerId, pet.type);
            String color = SkyblockUtils.rarityColor(pet.rarity).toString();
            String petTex = com.skyblock.core.util.HeadTextures.pet(pet.type.name());
            ItemBuilder petIcon = petTex != null
                    ? new ItemBuilder(Material.PLAYER_HEAD).skullTexture(petTex)
                    : new ItemBuilder(RARITY_WOOL.getOrDefault(pet.rarity, Material.WHITE_WOOL));

            List<String> lore = new ArrayList<>();
            lore.add("§7Rarity: " + color + pet.rarity.getDisplayName());
            lore.add("§7Category: §e" + pet.type.getCategory().getDisplayName());
            lore.add("");
            lore.addAll(progressLines(xp, level, pet.rarity));
            lore.add("");
            lore.add(isActive ? "§aCurrently active — §eclick to unequip" : "§eClick to equip!");

            setItem(contentSlot(i - start), petIcon
                    .displayName(color + "[Lvl " + level + "] " + pet.type.getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (isActive) {
                            manager.unequipPet(playerId);
                        } else {
                            manager.equipPet(playerId, pet.id);
                        }
                        new PetMenu(player, pageClamped, sort).open(player);
                    });
        }

        if (pets.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You have no pets yet.")
                    .build(), e -> e.setCancelled(true));
        }

        // Sort button (cycles order), like Hypixel's Pets menu.
        setItem(45, new ItemBuilder(Material.HOPPER)
                .displayName("§aSort: §e" + sort.label)
                .lore("§7Click to change the sort order.")
                .build(),
                e -> { e.setCancelled(true); new PetMenu(player, 0, sort.next()).open(player); });

        if (pageClamped > 0) {
            setItem(47, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + pageClamped + "§7/§e" + totalPages)
                    .build(),
                    e -> { e.setCancelled(true); new PetMenu(player, pageClamped - 1, sort).open(player); });
        }
        if (pageClamped < totalPages - 1) {
            setItem(51, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (pageClamped + 2) + "§7/§e" + totalPages)
                    .build(),
                    e -> { e.setCancelled(true); new PetMenu(player, pageClamped + 1, sort).open(player); });
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    /** Sorts the pet list in place by the active {@link SortMode}. */
    private void sortPets(List<Pet> pets, PetManager manager, UUID playerId) {
        switch (sort) {
            case LEVEL:
                pets.sort((a, b) -> Integer.compare(
                        manager.getLevel(playerId, b.type), manager.getLevel(playerId, a.type)));
                break;
            case RARITY:
                pets.sort((a, b) -> b.rarity.compareTo(a.rarity));
                break;
            case NAME:
                pets.sort((a, b) -> a.type.getDisplayName().compareToIgnoreCase(b.type.getDisplayName()));
                break;
        }
    }

    /** A "Progress to Level N" line + XP bar, or a single MAX LEVEL line, matching computeLevel(). */
    private static List<String> progressLines(long xp, int level, Rarity rarity) {
        long[] table = PetManager.PET_XP_TABLE.get(rarity.name());
        if (table == null) {
            table = PetManager.PET_XP_TABLE.get(
                    rarity.compareTo(Rarity.LEGENDARY) >= 0 ? "LEGENDARY" : "COMMON");
        }
        if (table == null || level >= PetManager.MAX_LEVEL || level - 1 >= table.length) {
            return List.of("§6§lMAX LEVEL");
        }
        long start = level > 1 ? table[level - 2] : 0;
        long next = table[level - 1];
        long into = xp - start;
        long need = next - start;
        double pct = need > 0 ? Math.min(100.0, into * 100.0 / need) : 100.0;
        int filled = (int) Math.round(pct / 100.0 * 20);
        return List.of(
                "§7Progress to Level " + (level + 1) + ": §e" + String.format("%.1f", pct) + "%",
                "§a" + "━".repeat(filled) + "§7" + "━".repeat(20 - filled)
                        + " §e" + String.format("%,d", Math.max(0, into)) + "§6/§e" + String.format("%,d", need));
    }
}
