package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Garden "Desk" hub, matching the wiki {@code {{UI|Desk}}} layout: Garden
 * Level (slot 4), Configure Plots (20), Garden Upgrades (22), SkyMart (24),
 * Garden Milestones (26), Garden Skins (32), Set Speed per Crop (49), Garden
 * Time (51) and Farming Toolkit (52).
 *
 * <p>Garden Level opens the real {@link GardenLevelsMenu}; live Garden data is
 * surfaced in the button lores. Sub-screens that don't exist yet (SkyMart,
 * Garden Upgrades, plot configuration, etc.) report "coming soon" rather than
 * inventing UI.</p>
 */
public final class GardenMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "Desk";

    public GardenMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        GardenManager manager = GardenManager.getInstance();

        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        int level = manager.getGardenLevel(playerId);
        long copper = manager.getCopper(playerId);

        // 1,5 — Garden Level (Sunflower) -> Garden Levels menu
        setItem(4, new ItemBuilder(Material.SUNFLOWER)
                .displayName("§aGarden Level " + toRoman(level))
                .lore(
                        "§7Garden Level: §e" + level + "§7/§e" + manager.getMaxGardenLevel(),
                        "§7Copper: §c" + String.format("%,d", copper),
                        "",
                        "§eClick to view Garden Levels!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new GardenLevelsMenu(player).open(player);
                });

        // 3,2 — Configure Plots (Grass Block)
        stub(20, Material.GRASS_BLOCK, "§aConfigure Plots", "Plot configuration is coming soon.",
                "§7Unlock and manage your", "§7Garden's crop plots.", "", "§eClick to configure!");

        // 3,4 — Garden Upgrades (Glistering Melon Slice)
        stub(22, Material.GLISTERING_MELON_SLICE, "§aGarden Upgrades", "Garden Upgrades are coming soon.",
                "§7Spend Copper on permanent", "§7Garden upgrades.", "", "§eClick to view!");

        // 3,6 — SkyMart (Emerald)
        stub(24, Material.EMERALD, "§aSkyMart", "SkyMart is coming soon.",
                "§7Spend Copper on tools, seeds", "§7and Garden cosmetics.", "", "§eClick to view!");

        // 3,8 — Garden Milestones (Block of Gold)
        stub(26, Material.GOLD_BLOCK, "§aGarden Milestones", "The milestones view is coming soon.",
                "§7Track your crop-collection", "§7milestones for rewards.",
                "", "§7Visitors Served: §e" + manager.getCompletedOffers(playerId),
                "", "§eClick to view!");

        // 4,5 — Garden Skins (Beacon)
        stub(32, Material.BEACON, "§aGarden Skins", "Garden Skins are coming soon.",
                "§7Customise the look of your", "§7Garden.", "", "§eClick to view!");

        // 6,4 — Set Speed per Crop (Sundial -> Clock)
        stub(49, Material.CLOCK, "§aSet Speed per Crop", "Speed configuration is coming soon.",
                "§7Set the expected farming speed", "§7used to estimate visitor offers.",
                "", "§eClick to configure!");

        // 6,6 — Garden Time (Clock)
        stub(51, Material.CLOCK, "§aGarden Time", "Garden Time is coming soon.",
                "§7The Garden runs on its own", "§7time, separate from SkyBlock.",
                "", "§eClick to view!");

        // 6,7 — Farming Toolkit (Iron Hoe)
        stub(52, Material.IRON_HOE, "§aFarming Toolkit", "The Farming Toolkit is coming soon.",
                "§7Equip farming tools for quick", "§7access while in the Garden.",
                "", "§eClick to view!");
    }

    /** A labelled Desk button that reports {@code comingSoon} on click (sub-screen not built yet). */
    private void stub(int slot, Material material, String name, String comingSoon, String... lore) {
        setItem(slot, new ItemBuilder(material).displayName(name).lore(lore).build(),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§e" + comingSoon);
                });
    }

    private static String toRoman(int n) {
        if (n <= 0) {
            return "I";
        }
        String[] th = {"", "M", "MM", "MMM"};
        String[] hu = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] te = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] on = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return th[(n / 1000) % 4] + hu[(n / 100) % 10] + te[(n / 10) % 10] + on[n % 10];
    }
}
