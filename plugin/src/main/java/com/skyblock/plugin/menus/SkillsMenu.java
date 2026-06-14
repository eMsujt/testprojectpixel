package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Your Skills" menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aYour Skills} that shows the viewing
 * player's eight main skills (from {@link SkillManager}), one icon per skill in
 * Hypixel's skill order, laid out across two centred rows and framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Each icon's lore shows the player's
 * current level and total XP in that skill.</p>
 */
public class SkillsMenu extends Menu {

    /** Centred layout slots, one per skill, in {@link SkillType} order. */
    private static final int[] SLOTS = {20, 21, 22, 23, 29, 30, 31, 32};

    /** The display icon for each skill, indexed in {@link SkillType} order. */
    private static final Material[] ICONS = {
            Material.GOLDEN_HOE,        // FARMING
            Material.STONE_PICKAXE,     // MINING
            Material.STONE_SWORD,       // COMBAT
            Material.JUNGLE_SAPLING,    // FORAGING
            Material.FISHING_ROD,       // FISHING
            Material.ENCHANTING_TABLE,  // ENCHANTING
            Material.BREWING_STAND,     // ALCHEMY
            Material.BONE               // TAMING
    };

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§aYour Skills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        SkillManager skills = SkillManager.getInstance();
        SkillType[] types = SkillType.values();
        for (int i = 0; i < types.length; i++) {
            SkillType type = types[i];
            String name = type.name().charAt(0) + type.name().substring(1).toLowerCase();
            setItem(SLOTS[i], new ItemBuilder(ICONS[i])
                    .displayName("§a" + name)
                    .lore(
                            "§7Level: §e" + skills.getLevel(playerId, type),
                            "§7Total XP: §e" + skills.getXP(playerId, type))
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
