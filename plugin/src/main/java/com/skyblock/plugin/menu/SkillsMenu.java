package com.skyblock.plugin.menu;

import com.skyblock.plugin.managers.SkillsManager;
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
        this.inventory = Bukkit.createInventory(this, 54, "§aSkills");
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
        SkillsManager sm = SkillsManager.getInstance();
        inventory.setItem(4, makeItem(Material.BOOK, "§aSkills", Arrays.asList("§7Your skill progress")));
        // Fixed Hypixel skill slots — first row, one slot per skill (slots 10–14)
        inventory.setItem(10, makeSkillItem(player, sm, Material.WHEAT,        "farming",  "§eFarming"));
        inventory.setItem(11, makeSkillItem(player, sm, Material.COBBLESTONE,  "mining",   "§eMining"));
        inventory.setItem(12, makeSkillItem(player, sm, Material.ROTTEN_FLESH, "combat",   "§eCombat"));
        inventory.setItem(13, makeSkillItem(player, sm, Material.OAK_LOG,      "foraging", "§eForaging"));
        inventory.setItem(14, makeSkillItem(player, sm, Material.COD,          "fishing",  "§eFishing"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SkillsMenu)) return;
        event.setCancelled(true);
    }

    private ItemStack makeSkillItem(Player player, SkillsManager sm,
                                    Material material, String skill, String displayName) {
        int level = sm.getSkillLevel(player.getUniqueId(), skill);
        long xp = sm.getSkillXP(player.getUniqueId(), skill);
        List<String> lore = Arrays.asList(
                "§7Level: §e" + level,
                "§7XP: §e" + xp
        );
        return makeItem(material, displayName, lore);
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
