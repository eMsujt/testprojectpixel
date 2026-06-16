package com.skyblock.core.menu;

import com.skyblock.items.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

/**
 * The canonical main SkyBlock hub menu. A 54-slot (6-row) chest GUI with
 * player profile at slot 4 and shortcuts across rows 1–3.
 */
public final class SkyBlockMainMenu extends Menu {

    private final Player player;

    public SkyBlockMainMenu(Player player) {
        super("§aSkyBlock Menu", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        // Slot 4 — Profile (top-row centre, exact Hypixel position)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName("§a" + player.getName() + "'s Profile");
            meta.setLore(Arrays.asList(
                    "§7Purse: §6" + String.format("%,.0f", (double) 0) + " Coins",
                    "§7Bank: §6" + String.format("%,.0f", (double) 0) + " Coins",
                    "",
                    "§eClick to manage profiles!"
            ));
            skull.setItemMeta(meta);
        }
        setItem(4, skull, e -> e.setCancelled(true));

        // Row 1 (slots 10-16) — skills & gear
        setItem(10, new ItemBuilder(Material.IRON_SWORD)
                .displayName("§aSkills")
                .lore("§7View your skill levels and XP.")
                .build(),
                e -> e.setCancelled(true));

        setItem(11, new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName("§aCollections")
                .lore("§7Track your collection progress.")
                .build(),
                e -> e.setCancelled(true));

        setItem(12, new ItemBuilder(Material.CRAFTING_TABLE)
                .displayName("§aCrafting")
                .lore("§7Browse SkyBlock recipes.")
                .build(),
                e -> e.setCancelled(true));

        setItem(13, new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .displayName("§aWardrobe")
                .lore("§7Manage your armor sets.")
                .build(),
                e -> e.setCancelled(true));

        setItem(14, new ItemBuilder(Material.CAT_SPAWN_EGG)
                .displayName("§aPets")
                .lore("§7Manage your pets.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new PetMenu(e.getWhoClicked().getUniqueId()).open((Player) e.getWhoClicked());
                });

        setItem(15, new ItemBuilder(Material.FISHING_ROD)
                .displayName("§aFishing Bag")
                .lore("§7Store your fishing gear.")
                .build(),
                e -> e.setCancelled(true));

        setItem(16, new ItemBuilder(Material.ARROW)
                .displayName("§aQuiver")
                .lore("§7Manage your arrows.")
                .build(),
                e -> e.setCancelled(true));

        // Row 2 (slots 19-25) — economy & travel
        setItem(19, new ItemBuilder(Material.PAPER)
                .displayName("§aObjectives")
                .lore("§7View your active objectives.")
                .build(),
                e -> e.setCancelled(true));

        setItem(20, new ItemBuilder(Material.PISTON)
                .displayName("§aMinions")
                .lore("§7Manage your minions.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new MinionsMenu(player).open(player);
                });

        setItem(21, new ItemBuilder(Material.COMPASS)
                .displayName("§aFast Travel")
                .lore("§7Teleport to SkyBlock locations.")
                .build(),
                e -> e.setCancelled(true));

        setItem(22, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Auction House")
                .lore("§7Buy and sell items.")
                .build(),
                e -> e.setCancelled(true));

        setItem(23, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Bazaar")
                .lore("§7Trade commodities instantly.")
                .build(),
                e -> e.setCancelled(true));

        setItem(24, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank")
                .lore("§7Deposit and withdraw coins.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new BankMenu((Player) e.getWhoClicked()).open((Player) e.getWhoClicked());
                });

        setItem(25, new ItemBuilder(Material.CHEST)
                .displayName("§aStorage")
                .lore("§7Access your personal storage.")
                .build(),
                e -> e.setCancelled(true));

        // Row 3 (slots 28-34) — extras
        setItem(28, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§5Accessory Bag")
                .lore("§7Manage your accessories.")
                .build(),
                e -> e.setCancelled(true));

        setItem(29, new ItemBuilder(Material.GLASS_BOTTLE)
                .displayName("§aPotion Bag")
                .lore("§7Store your potions.")
                .build(),
                e -> e.setCancelled(true));

        setItem(31, new ItemBuilder(Material.BOOK)
                .displayName("§aQuests")
                .lore("§7View your active quests.")
                .build(),
                e -> new QuestsMenu(player).open(player));

        setItem(33, new ItemBuilder(Material.COMPARATOR)
                .displayName("§aSettings")
                .lore("§7Configure your SkyBlock settings.")
                .build(),
                e -> e.setCancelled(true));
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
