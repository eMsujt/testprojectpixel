package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * "Your Equipment and Stats", opened from the profile head in the SkyBlock Menu.
 * Laid out 1:1 with Hypixel's Equipment menu (Equipment_Menu/UI): the equipment
 * column (Necklace 10, Cloak 19, Belt 28, Gloves 37) and armor column (Helmet 11,
 * Chestplate 20, Leggings 29, Boots 38, Pet 47) on the left, and the six stat
 * category items on the right (Combat 15, Gathering 16, Wisdom 24, Misc 25,
 * Fishing 33, Active Effects 34), with a Go Back arrow (48) and Achievements (50).
 */
public final class EquipmentMenu extends AbstractSkyBlockMenu {

    public EquipmentMenu(Player player) {
        super(player, "§aYour Equipment and Stats", 6);
    }

    @Override
    protected void populate() {
        ItemStack bg = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, bg);

        UUID id = player.getUniqueId();
        StatsManager.PlayerStats stats = StatsManager.getInstance().getStats(id);

        // Equipment column (no equipment system yet → empty placeholder slots).
        emptySlot(18, "Hand");
        emptySlot(10, "Necklace");
        emptySlot(19, "Cloak");
        emptySlot(28, "Belt");
        emptySlot(37, "Gloves");

        // Armor column — the player's worn pieces.
        armorSlot(11, player.getInventory().getHelmet(), "Helmet");
        armorSlot(20, player.getInventory().getChestplate(), "Chestplate");
        armorSlot(29, player.getInventory().getLeggings(), "Leggings");
        armorSlot(38, player.getInventory().getBoots(), "Boots");

        // Pet slot.
        PetManager pets = PetManager.getInstance();
        UUID activePet = pets.getActivePetId(id);
        if (activePet != null) {
            String petName = "Pet";
            for (PetManager.Pet p : pets.getPets(id)) {
                if (p.id.equals(activePet)) petName = p.type.getDisplayName();
            }
            setItem(47, new ItemBuilder(Material.BONE).displayName("§7Pet: §a" + petName).build());
        } else {
            emptySlot(47, "Pet");
        }

        // Stat category items (right side).
        setItem(15, statCategory(Material.DIAMOND_SWORD, "§cCombat Stats", stats,
                Stat.HEALTH, Stat.DEFENSE, Stat.STRENGTH, Stat.CRIT_CHANCE, Stat.CRIT_DAMAGE,
                Stat.ATTACK_SPEED, Stat.ABILITY_DAMAGE, Stat.TRUE_DEFENSE, Stat.FEROCITY,
                Stat.HEALTH_REGEN, Stat.VITALITY, Stat.SWING_RANGE));
        setItem(16, statCategory(Material.IRON_PICKAXE, "§6Gathering Stats", stats,
                Stat.MINING_SPEED, Stat.MINING_FORTUNE, Stat.FARMING_FORTUNE,
                Stat.FORAGING_FORTUNE, Stat.PRISTINE));
        setItem(24, statCategory(Material.BOOK, "§bWisdom Stats", stats,
                Stat.COMBAT_WISDOM, Stat.MINING_WISDOM, Stat.FARMING_WISDOM));
        setItem(25, statCategory(Material.CLOCK, "§aMisc Stats", stats,
                Stat.SPEED, Stat.MAGIC_FIND, Stat.PET_LUCK, Stat.INTELLIGENCE));
        setItem(33, statCategory(Material.FISHING_ROD, "§9Fishing Stats", stats,
                Stat.SEA_CREATURE_CHANCE, Stat.FISHING_SPEED));

        // Active effects.
        List<String> fx = new ArrayList<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            fx.add("§8• §7" + prettyEffect(effect.getType().getKey().getKey()) + " §a" + (effect.getAmplifier() + 1));
        }
        if (fx.isEmpty()) fx.add("§7No active effects.");
        setItem(34, new ItemBuilder(Material.BREWING_STAND)
                .displayName("§dActive Effects §7(" + player.getActivePotionEffects().size() + ")")
                .lore(fx).build());

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });

        setItem(50, new ItemBuilder(Material.DIAMOND)
                .displayName("§bSkyBlock Achievements")
                .lore("§7View your achievements.")
                .build());
    }

    private void emptySlot(int slot, String name) {
        setItem(slot, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .displayName("§7Empty " + name + " Slot")
                .lore("§8" + name.toUpperCase())
                .build());
    }

    private void armorSlot(int slot, ItemStack worn, String name) {
        if (worn != null && worn.getType() != Material.AIR) {
            setItem(slot, worn.clone());
        } else {
            emptySlot(slot, name);
        }
    }

    private static ItemStack statCategory(Material icon, String name, StatsManager.PlayerStats stats, Stat... statList) {
        List<String> lore = new ArrayList<>();
        for (Stat stat : statList) {
            lore.add(color(stat) + stat.getSymbol() + " " + stat.getDisplayName() + " "
                    + trim(stats.getStat(stat)) + (percent(stat) ? "%" : ""));
        }
        lore.add("");
        lore.add("§7Your combined stats from all sources.");
        return new ItemBuilder(icon).displayName(name).lore(lore).build();
    }

    private static String color(Stat stat) {
        switch (stat) {
            case HEALTH: case HEALTH_REGEN: case VITALITY: case STRENGTH:
            case ABILITY_DAMAGE: case FEROCITY:
                return "§c";
            case DEFENSE: case TRUE_DEFENSE: case SWING_RANGE:
                return "§a";
            case CRIT_CHANCE: case CRIT_DAMAGE:
                return "§9";
            case INTELLIGENCE: case MAGIC_FIND: case PET_LUCK:
            case SEA_CREATURE_CHANCE: case FISHING_SPEED:
                return "§b";
            case ATTACK_SPEED:
                return "§e";
            case SPEED:
                return "§f";
            case COMBAT_WISDOM: case MINING_WISDOM: case FARMING_WISDOM:
                return "§3";
            default:
                return "§6";
        }
    }

    private static boolean percent(Stat stat) {
        switch (stat) {
            case CRIT_CHANCE: case CRIT_DAMAGE: case ATTACK_SPEED: case SEA_CREATURE_CHANCE:
                return true;
            default:
                return false;
        }
    }

    private static String trim(double v) {
        return v == Math.floor(v) ? Long.toString((long) v) : String.format("%.1f", v);
    }

    private static String prettyEffect(String key) {
        String[] parts = key.replace('_', ' ').split(" ");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
