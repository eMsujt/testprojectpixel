package com.skyblock.plugin.menu;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class SkillsMenu implements InventoryHolder {

    private final Inventory inventory;

    public SkillsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§aYour Skills");
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
        // Five main skills centered in row 3 (slots 20–24)
        inventory.setItem(20, makeSkillItem(player, sm, Material.WHEAT,        "farming",  "§eFarming"));
        inventory.setItem(21, makeSkillItem(player, sm, Material.COBBLESTONE,  "mining",   "§eMining"));
        inventory.setItem(22, makeSkillItem(player, sm, Material.ROTTEN_FLESH, "combat",   "§eCombat"));
        inventory.setItem(23, makeSkillItem(player, sm, Material.OAK_LOG,      "foraging", "§eForaging"));
        inventory.setItem(24, makeSkillItem(player, sm, Material.COD,          "fishing",  "§eFishing"));
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
