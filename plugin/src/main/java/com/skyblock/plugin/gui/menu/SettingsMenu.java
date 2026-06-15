package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsMenu extends Menu {

    private final Player player;

    public SettingsMenu(Player player) {
        super("§8SkyBlock Settings", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        setItem(20, lever("§aSkill Notifications", profile.isShowSkillNotifications()),
                event -> {
                    profile.setShowSkillNotifications(!profile.isShowSkillNotifications());
                    open((Player) event.getWhoClicked());
                });

        setItem(22, lever("§aPet Notifications", profile.isShowPetNotifications()),
                event -> {
                    profile.setShowPetNotifications(!profile.isShowPetNotifications());
                    open((Player) event.getWhoClicked());
                });

        setItem(24, lever("§aCollection Notifications", profile.isShowCollectionNotifications()),
                event -> {
                    profile.setShowCollectionNotifications(!profile.isShowCollectionNotifications());
                    open((Player) event.getWhoClicked());
                });
    }

    private ItemStack lever(String name, boolean enabled) {
        return new ItemBuilder(Material.LEVER)
                .displayName(name)
                .lore(
                        enabled ? "§7Currently: §aEnabled" : "§7Currently: §cDisabled",
                        "§eClick to toggle!")
                .build();
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
