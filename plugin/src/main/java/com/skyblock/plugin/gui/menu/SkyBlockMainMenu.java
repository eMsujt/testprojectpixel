package com.skyblock.plugin.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

/**
 * @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead.
 */
@Deprecated
public class SkyBlockMainMenu extends Menu {

    private final Player player;

    public SkyBlockMainMenu(Player player) {
        super("§aSkyBlock Menu", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        // Slot 4 — Profile (top-row centre, exact Hypixel position)
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName("§a" + player.getName() + "'s Profile");
            meta.setLore(Arrays.asList(
                    "§7Purse: §6" + String.format("%,.0f", (double) profile.getPurse()) + " Coins",
                    "§7Bank: §6" + String.format("%,.0f", (double) profile.getBank()) + " Coins",
                    "",
                    "§eClick to manage profiles!"
            ));
            skull.setItemMeta(meta);
        }
        setItem(4, skull, e -> {
            e.setCancelled(true);
            new ProfileManagementMenu(player).open(player);
        });

        // Row 1 (slots 10-16) — skills & gear
        setItem(10, new ItemBuilder(Material.IRON_SWORD)
                .displayName("§aSkills")
                .lore("§7View your skill levels and XP.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SkillsMenu(player.getUniqueId()).open(player);
                });

        setItem(11, new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName("§aCollections")
                .lore("§7Track your collection progress.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new CollectionsMenu(player.getUniqueId()).open(player);
                });

        setItem(12, new ItemBuilder(Material.CRAFTING_TABLE)
                .displayName("§aCrafting")
                .lore("§7Browse SkyBlock recipes.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new CraftingTableMenu(player).open(player);
                });

        setItem(13, new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .displayName("§aWardrobe")
                .lore("§7Manage your armor sets.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new WardrobeMenu(player).open(player);
                });

        setItem(14, new ItemBuilder(Material.CAT_SPAWN_EGG)
                .displayName("§aPets")
                .lore("§7Manage your pets.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new PetsMenu(player).open(player);
                });

        setItem(15, new ItemBuilder(Material.FISHING_ROD)
                .displayName("§aFishing Bag")
                .lore("§7Store your fishing gear.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new FishingBagMenu(player).open(player);
                });

        setItem(16, new ItemBuilder(Material.ARROW)
                .displayName("§aQuiver")
                .lore("§7Manage your arrows.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new QuiverMenu(player).open(player);
                });

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
                e -> {
                    e.setCancelled(true);
                    new FastTravelMenu().open(player);
                });

        setItem(22, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Auction House")
                .lore("§7Buy and sell items.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new AuctionHouseMenu(player).open(player);
                });

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
                    new BankMenu(player).open(player);
                });

        setItem(25, new ItemBuilder(Material.CHEST)
                .displayName("§aStorage")
                .lore("§7Access your personal storage.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new StorageMenu().open(player);
                });

        // Row 3 (slots 28-34) — extras
        setItem(28, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§5Accessory Bag")
                .lore("§7Manage your accessories.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new AccessoryBagMenu(player).open(player);
                });

        setItem(29, new ItemBuilder(Material.GLASS_BOTTLE)
                .displayName("§aPotion Bag")
                .lore("§7Store your potions.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new PotionBagMenu(player).open(player);
                });

        setItem(31, new ItemBuilder(Material.BOOK)
                .displayName("§aQuests")
                .lore("§7View your active quests.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new QuestsMenu(player).open(player);
                });

        setItem(33, new ItemBuilder(Material.COMPARATOR)
                .displayName("§aSettings")
                .lore("§7Configure your SkyBlock settings.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SettingsMenu(player).open(player);
                });
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
