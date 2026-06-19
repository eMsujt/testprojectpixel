package com.skyblock.core.menu;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.FishType;
import com.skyblock.core.util.MenuUtils;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class FishingMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final UUID playerId;

    public FishingMenu(UUID playerId) {
        super("Fishing", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).displayName("§r").build();
        MenuUtils.fillBorder(getRows(), this::setItem, pane);

        FishingManager fm = FishingManager.getInstance();
        int level = fm.getLevel(playerId);
        double xp = fm.getXp(playerId);
        int totalCaught = fm.getTotalFishCaught(playerId);

        setItem(13, new ItemBuilder(Material.FISHING_ROD)
                .displayName("§bFishing Skill")
                .lore(Arrays.asList(
                        "§7Level: §e" + level,
                        "§7XP: §e" + String.format("%.1f", xp),
                        "§7Fish Caught: §e" + totalCaught
                ))
                .build());

        List<FishType> fishTypes = Arrays.asList(FishType.values());
        for (int i = 0; i < INNER_SLOTS.length && i < fishTypes.size(); i++) {
            FishType fishType = fishTypes.get(i);
            Material mat = materialFor(fishType);
            String name = formatName(fishType.name());
            setItem(INNER_SLOTS[i], new ItemBuilder(mat)
                    .displayName("§b" + name)
                    .lore(List.of("§7A catch obtainable through fishing."))
                    .build());
        }
    }

    private static Material materialFor(FishType fishType) {
        switch (fishType) {
            case PUFFERFISH:           return Material.PUFFERFISH;
            case SEA_CREATURE:         return Material.NAUTILUS_SHELL;
            case FISHING_TREASURE:     return Material.MAP;
            case INK_SAC:              return Material.INK_SAC;
            case LILY_PAD:             return Material.LILY_PAD;
            case NAUTILUS_SHELL:       return Material.NAUTILUS_SHELL;
            case PRISMARINE_CRYSTALS:  return Material.PRISMARINE_CRYSTALS;
            case PRISMARINE_SHARD:     return Material.PRISMARINE_SHARD;
            case SPONGE:               return Material.SPONGE;
            case SEA_LANTERN:          return Material.SEA_LANTERN;
            case TREASURE_MAP:         return Material.MAP;
            default:                   return Material.COD;
        }
    }

    private static String formatName(String enumName) {
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
