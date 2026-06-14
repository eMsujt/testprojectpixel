package com.skyblock.plugin.menu;

import com.skyblock.plugin.managers.SkillsManager;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class SkillsMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public SkillsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§2Skills");
        build(player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", Arrays.asList());
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(4, makeItem(Material.LIME_DYE, "§9Skills", Arrays.asList("§7Your skill progress")));

        // Row 2, centered (cols 2-5): farming, mining, combat, foraging
        inventory.setItem(11, makeSkillItem(profile, Material.WHEAT,           "farming",      "§eFarming"));
        inventory.setItem(12, makeSkillItem(profile, Material.COBBLESTONE,     "mining",       "§eMining"));
        inventory.setItem(13, makeSkillItem(profile, Material.ROTTEN_FLESH,    "combat",       "§eCombat"));
        inventory.setItem(14, makeSkillItem(profile, Material.OAK_LOG,         "foraging",     "§eForaging"));

        // Row 4, centered (cols 2-5): fishing, enchanting, alchemy, taming
        inventory.setItem(29, makeSkillItem(profile, Material.COD,             "fishing",      "§eFishing"));
        inventory.setItem(30, makeSkillItem(profile, Material.ENCHANTED_BOOK,  "enchanting",   "§eEnchanting"));
        inventory.setItem(31, makeSkillItem(profile, Material.BREWING_STAND,   "alchemy",      "§eAlchemy"));
        inventory.setItem(32, makeSkillItem(profile, Material.LEAD,            "taming",       "§eTaming"));
        inventory.setItem(33, makeSkillItem(profile, Material.CRAFTING_TABLE,  "carpentry",    "§eCarpentry"));
        inventory.setItem(34, makeSkillItem(profile, Material.MAGMA_CREAM,     "runecrafting", "§eRunecrafting"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SkillsMenu)) return;
        event.setCancelled(true);
    }

    private ItemStack makeSkillItem(PlayerProfile profile, Material material,
                                    String skill, String displayName) {
        long xp = profile.getSkillXp(skill);
        int level = computeLevelFromXp(skill, xp);
        List<String> lore = Arrays.asList(
                "§7Level: §e" + level,
                "§7XP: §e" + xp
        );
        return makeItem(material, displayName, lore);
    }

    private static int computeLevelFromXp(String skill, long xp) {
        long[] table = SkillsManager.SKILL_XP_TABLE.get(skill);
        if (table == null) return 0;
        long cumulative = 0;
        int level = 0;
        for (long threshold : table) {
            cumulative += threshold;
            if (xp < cumulative) break;
            level++;
        }
        return level;
    }

    private ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
