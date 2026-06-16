package com.skyblock.gui.menu;

import com.skyblock.items.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.menu.SkillsMenu;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SkillDetailMenu extends Menu {

    private final UUID playerId;
    private final String displayName;
    private final String key;
    private final Material icon;

    public SkillDetailMenu(UUID playerId, String displayName, String key, Material icon) {
        super("§a" + displayName, 6);
        this.playerId = playerId;
        this.displayName = displayName;
        this.key = key;
        this.icon = icon;
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(playerId);
        SkillManager skillManager = SkillManager.getInstance();
        double xp = profile.getSkillXp(key);
        int level = skillManager.levelForXp(key, (long) xp);

        setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + displayName)
                .lore(
                        "§7Level: §e" + level,
                        "§7Total XP: §e" + (long) xp)
                .build());

        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§aBack")
                .lore("§7Return to your skills.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SkillsMenu(playerId).open((Player) e.getWhoClicked());
                });
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
