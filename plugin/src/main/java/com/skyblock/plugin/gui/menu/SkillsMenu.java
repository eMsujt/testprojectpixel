package com.skyblock.plugin.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.manager.SkillManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SkillsMenu extends Menu {

    private enum Skill {
        FARMING      ("Farming",      "farming",      Material.WHEAT,            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyMzI0NDAxNDZhNTVhYWE4NGIzOWEyMzc3NjJkN2EzMGIzY2JhN2Y0Y2U1YjYxZTZlYWJlMDRjZDI0MzgifX19"),
        MINING       ("Mining",       "mining",       Material.IRON_ORE,         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2YzNWYzOGM4OWZlZWI3Y2JiZDM2NWNiMzIxNzEyYTZmMzllMmJhNmZhMzA2NmZhYTFjYjgxZWM4MDZmYjgifX19"),
        COMBAT       ("Combat",       "combat",       Material.DIAMOND_SWORD,    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U1YzJiMDI4NGE5YzJiYzU5NTNjMzljNGZlYzY5MDllODg4MmMwMzNmNzU0MGI4OWIwNzE2ZThhMTlkMTYifX19"),
        FORAGING     ("Foraging",     "foraging",     Material.OAK_LOG,          "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzY3M2UyYWE5NTU1NjkwMzBiZGYxOGIxMGMzOWE3YWI4YjViYmUxNTljNmFhMzM2ZjM0YWI4NTQ0ZDgwZjYifX19"),
        FISHING      ("Fishing",      "fishing",      Material.COD,              "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q3YzZhYTZiNTMwZDc4OTgzN2QxNmYyMjViZjlhYjFmMjVkMmUwOTg5YjljMjFiMjk0ZTM2MDNiNTM2ZmUifX19"),
        ENCHANTING   ("Enchanting",   "enchanting",   Material.ENCHANTING_TABLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwYjMxYWU4ZjQ0NjBjMmY1OGI0NmU5ZjE2NWFkNGU1ODc4MzhlZGY0ZWRmNGE4MTZhYTc5YjMwZDk3ZTcifX19"),
        ALCHEMY      ("Alchemy",      "alchemy",      Material.BREWING_STAND,    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0YzRlZDYzMTU0YzVkNTc2N2Q0OGZkMTVhZmI4YTg0YTYwYTZjNWYwMTY2Y2ZhNzZlMzQ5OTA2ZTJiMmIifX19"),
        TAMING       ("Taming",       "taming",       Material.BONE,             "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1OWFjMTZiYjdjOWU5NGY3N2M0ZDdlNzZmOWYxMDJhODBmNTBhNjlhNGRkNGM5M2ZjMzIzMjc4ZmE3ZWFkNyJ9fX0="),
        CARPENTRY    ("Carpentry",    "carpentry",    Material.CRAFTING_TABLE,   "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkMmMxYjJlNGI4NmQ1M2M0ZGZjMWVhNTI3Zjk3NDRhYjRmYjhhYTM4YTFmMGU1N2ZkZTQyMzJiNmZiYzEifX19"),
        RUNECRAFTING ("Runecrafting", "runecrafting", Material.MAGMA_CREAM,      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q1NzExMzQxZTZiYWE0ZDllZTU0NTI5ZWUyN2Q4MzFiMzc4NTI1ZDgxZTE2MGRlY2Q0NTZlNzFhZjgxN2UyMTkifX19"),
        SOCIAL       ("Social",       "social",       Material.EMERALD,          "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzBhMmNjMjU4ZmZlMDc1Nzk5YzllMzI1MDBiNzRiZGNhZGYzOGM1MzRlMzdlMzUzZGE3MjE5ZTFhOWFhYTEifX19");

        private final String displayName;
        private final String key;
        private final Material icon;
        private final String texture;

        Skill(String displayName, String key, Material icon, String texture) {
            this.displayName = displayName;
            this.key = key;
            this.icon = icon;
            this.texture = texture;
        }
    }

    private static final int[] SLOTS = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§aSkills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(playerId);
        SkillManager skillManager = SkillManager.getInstance();
        Skill[] values = Skill.values();
        for (int i = 0; i < values.length; i++) {
            Skill skill = values[i];
            double xp = profile.getSkillXp(skill.key);
            int level = skillManager.levelForXp(skill.key, (long) xp);
            ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                    .skullTexture(skill.texture)
                    .displayName("§a" + skill.displayName)
                    .lore("§7Level: §e" + level, "§7Total XP: §e" + (long) xp)
                    .build();
            final Skill s = skill;
            setItem(SLOTS[i], skull, e -> {
                e.setCancelled(true);
                new SkillDetailMenu(playerId, s.displayName, s.key, s.icon)
                        .open((Player) e.getWhoClicked());
            });
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
