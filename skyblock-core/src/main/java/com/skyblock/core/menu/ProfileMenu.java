package com.skyblock.core.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.stats.StatsManager.PlayerStats;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public final class ProfileMenu extends AbstractMenu {

    private static final int HEAD_SLOT    = 13;
    private static final int COMBAT_SLOT  = 20;
    private static final int SKILLS_SLOT  = 22;
    private static final int OTHER_SLOT   = 24;

    public ProfileMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§b§l" + player.getName() + "'s SkyBlock Profile", 54);
    }

    @Override
    protected void populate() {
        fillBorder();

        UUID id = player.getUniqueId();
        EconomyManager eco = EconomyManager.getInstance();
        PlayerStats stats = StatsManager.getInstance().getStats(id);
        SkillManager skills = SkillManager.getInstance();

        // Player head — economy overview
        ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + player.getName())
                .lore(
                        "§7Purse: §6" + String.format("%,.0f", (double) eco.getPurse(id)) + " Coins",
                        "§7Bank: §6" + String.format("%,.0f", (double) eco.getBank(id)) + " Coins")
                .build();
        if (skull.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwningPlayer(player);
            skull.setItemMeta(meta);
        }
        setItem(HEAD_SLOT, skull, e -> e.setCancelled(true));

        // Combat stats panel
        setItem(COMBAT_SLOT, new ItemBuilder(Material.DIAMOND_SWORD)
                .displayName("§cCombat Stats")
                .lore(
                        "§7❤ Health: §c" + fmt(stats.getStat(Stat.HEALTH)),
                        "§7❈ Defense: §a" + fmt(stats.getStat(Stat.DEFENSE)),
                        "§7❁ Strength: §c" + fmt(stats.getStat(Stat.STRENGTH)),
                        "§7☣ Crit Chance: §6" + fmt(stats.getStat(Stat.CRIT_CHANCE)) + "%",
                        "§7☠ Crit Damage: §6" + fmt(stats.getStat(Stat.CRIT_DAMAGE)) + "%")
                .build(), e -> e.setCancelled(true));

        // Skills summary panel
        setItem(SKILLS_SLOT, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .displayName("§aSkill Levels")
                .lore(
                        "§7Farming: §e" + skills.getSkillLevel(id, "farming"),
                        "§7Mining: §e"   + skills.getSkillLevel(id, "mining"),
                        "§7Combat: §e"   + skills.getSkillLevel(id, "combat"),
                        "§7Foraging: §e" + skills.getSkillLevel(id, "foraging"),
                        "§7Fishing: §e"  + skills.getSkillLevel(id, "fishing"))
                .build(), e -> e.setCancelled(true));

        // Utility/gathering stats panel
        setItem(OTHER_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Other Stats")
                .lore(
                        "§7✦ Speed: §f"           + fmt(stats.getStat(Stat.SPEED)),
                        "§7✎ Intelligence: §b"    + fmt(stats.getStat(Stat.INTELLIGENCE)),
                        "§7✯ Magic Find: §b"      + fmt(stats.getStat(Stat.MAGIC_FIND)),
                        "§7⸕ Mining Speed: §b"    + fmt(stats.getStat(Stat.MINING_SPEED)),
                        "§7☘ Mining Fortune: §6"  + fmt(stats.getStat(Stat.MINING_FORTUNE)))
                .build(), e -> e.setCancelled(true));
    }

    private static String fmt(double value) {
        return value == Math.floor(value) ? Long.toString((long) value) : Double.toString(value);
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
