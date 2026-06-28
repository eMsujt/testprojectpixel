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

    /** Pet list sort orders, cycled by the Sort button (1:1 with Hypixel's Pets menu). */
    private enum SortMode {
        LEVEL("Level"), RARITY("Rarity"), ALPHABETICAL("Alphabetical"), SKILL("Skill");
        final String label;
        SortMode(String label) { this.label = label; }
        SortMode next() { return values()[(ordinal() + 1) % values().length]; }
    }

    /** Inset 7-wide pet grid (cols 2-8, rows 2-5), matching the wiki Pets layout. */
    private static final int[] PET_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final int page;
    private final SortMode sort;
    /** When on, clicking a pet converts it back to an item instead of (un)equipping. */
    private final boolean convertToItem;

    public PetMenu(Player player) {
        this(player, 0, SortMode.LEVEL, false);
    }

    private PetMenu(Player player, int page, SortMode sort, boolean convertToItem) {
        super(player, title(player, page), 6);
        this.page = page;
        this.sort = sort;
        this.convertToItem = convertToItem;
    }

    /** Hypixel's Pets window title carries the page indicator, e.g. {@code (1/2) Pets}. */
    private static String title(Player player, int page) {
        int count = PetManager.getInstance().getPets(player.getUniqueId()).size();
        int capacity = 28; // (6 rows - 2) * 7
        int total = Math.max(1, (count + capacity - 1) / capacity);
        int p = Math.max(0, Math.min(page, total - 1));
        return "(" + (p + 1) + "/" + total + ") Pets";
    }

    @Override
    protected void populate() {
        // No side border: the wiki's Pets grid is left-aligned (the base fills empty slots).
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

        // Summary info head (verbatim wiki lore).
        String activeName = "§cNone";
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
                        "§7View and manage all of your Pets.",
                        "§7Level up your pets faster by gaining",
                        "§7XP in their favorite skill!",
                        "",
                        "§7Selected pet: " + activeName)
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
            if (convertToItem) {
                lore.add("§eClick to convert to an item!");
            } else {
                lore.add(isActive ? "§aCurrently active — §eclick to unequip" : "§eClick to equip!");
            }

            ItemBuilder icon = petIcon
                    .displayName("§7[Lvl " + level + "] " + color + pet.type.getDisplayName())
                    .lore(lore);
            if (isActive) {
                icon.glow();
            }
            setItem(PET_SLOTS[i - start], icon.build(),
                    e -> {
                        e.setCancelled(true);
                        if (convertToItem) {
                            // Hypixel turns the pet back into an item here; our pets aren't
                            // item-backed yet, so keep the pet and tell the player.
                            player.sendMessage("§cConverting pets back to items isn't available yet.");
                            return;
                        }
                        if (isActive) {
                            manager.unequipPet(playerId);
                        } else {
                            manager.equipPet(playerId, pet.id);
                        }
                        new PetMenu(player, pageClamped, sort, convertToItem).open(player);
                    });
        }

        if (pets.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Pets")
                    .lore("§7You have no pets yet.")
                    .build(), e -> e.setCancelled(true));
        }

        // --- Bottom control bar, 1:1 with Hypixel's Pets menu ---

        // Convert to Item toggle (slot 47) — a Diamond; glows when enabled.
        ItemBuilder convert = new ItemBuilder(Material.DIAMOND)
                .displayName("§aConvert to Item")
                .lore("§7Turn this feature on to convert",
                      "§7your pets back into items by",
                      "§7clicking on them!",
                      "",
                      "§7Status: " + (convertToItem ? "§aEnabled" : "§cDisabled"),
                      "",
                      "§eClick to toggle!");
        if (convertToItem) {
            convert.glow();
        }
        setItem(47, convert.build(),
                e -> { e.setCancelled(true); new PetMenu(player, pageClamped, sort, !convertToItem).open(player); });

        // Sort (slot 51) — a Hopper listing every sort mode, current one highlighted.
        List<String> sortLore = new ArrayList<>();
        sortLore.add("");
        for (SortMode mode : SortMode.values()) {
            sortLore.add((mode == sort ? "§e> " : "§7> ") + mode.label);
        }
        sortLore.add("");
        sortLore.add("§eClick to change!");
        setItem(51, new ItemBuilder(Material.HOPPER)
                .displayName("§aSort")
                .lore(sortLore)
                .build(),
                e -> { e.setCancelled(true); new PetMenu(player, 0, sort.next(), convertToItem).open(player); });

        // Go Back (slot 48) + Close (slot 49).
        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
        setItem(49, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> { e.setCancelled(true); player.closeInventory(); });

        // Page arrows: previous at slot 45, next at slot 53.
        if (pageClamped > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + pageClamped + "§7/§e" + totalPages)
                    .build(),
                    e -> { e.setCancelled(true); new PetMenu(player, pageClamped - 1, sort, convertToItem).open(player); });
        }
        if (pageClamped < totalPages - 1) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (pageClamped + 2) + "§7/§e" + totalPages)
                    .build(),
                    e -> { e.setCancelled(true); new PetMenu(player, pageClamped + 1, sort, convertToItem).open(player); });
        }
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
            case ALPHABETICAL:
                pets.sort((a, b) -> a.type.getDisplayName().compareToIgnoreCase(b.type.getDisplayName()));
                break;
            case SKILL:
                pets.sort((a, b) -> b.type.getCategory().compareTo(a.type.getCategory()));
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
                "§2" + "-".repeat(filled) + "§f" + "-".repeat(20 - filled)
                        + " §e" + String.format("%,d", Math.max(0, into)) + "§6/§e" + String.format("%,d", need));
    }
}
