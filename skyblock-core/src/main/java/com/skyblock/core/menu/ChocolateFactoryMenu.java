package com.skyblock.core.menu;

import com.skyblock.core.manager.ChocolateFactoryManager;
import com.skyblock.core.manager.ChocolateFactoryManager.Employee;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Chocolate Factory" menu. Laid out like Hypixel: a summary at slot 4, the
 * central click-for-chocolate Dropper at slot 13, the seven named rabbit
 * employees on row 3 (slots 19-25, click to upgrade), the upgrade nodes on row 4
 * (28-31), and the bottom-row nav (Shop, Hoppity's Collection, Milestones,
 * Ranking, Rabbit Barn) plus Close.
 */
public final class ChocolateFactoryMenu extends Menu {

    private static final int[] EMPLOYEE_SLOTS = {19, 20, 21, 22, 23, 24, 25};

    private final Player player;

    public ChocolateFactoryMenu(Player player) {
        super("§6Chocolate Factory", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, pane);

        UUID id = player.getUniqueId();
        ChocolateFactoryManager factory = ChocolateFactoryManager.getInstance();

        setItem(4, new ItemBuilder(Material.COOKIE)
                .displayName("§6Chocolate Factory")
                .lore(
                        "§7Chocolate: §e" + String.format("%,d", factory.getChocolate(id)),
                        "§7Production: §e" + factory.getProductionRate(id) + " §7/s")
                .build(), e -> e.setCancelled(true));

        long clickValue = Math.max(1, factory.getProductionRate(id));
        setItem(13, new ItemBuilder(Material.DROPPER)
                .displayName("§6Click me!")
                .lore("§7Click for §6+" + clickValue + " §7Chocolate!", "", "§eClick!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    factory.addChocolate(id, Math.max(1, factory.getProductionRate(id)));
                    open(player);
                });

        Employee[] employees = Employee.values();
        for (int i = 0; i < employees.length && i < EMPLOYEE_SLOTS.length; i++) {
            Employee emp = employees[i];
            int level = factory.getEmployeeLevel(id, emp);
            long cost = factory.getUpgradeCost(id, emp);
            setItem(EMPLOYEE_SLOTS[i], new ItemBuilder(Material.RABBIT_FOOT)
                    .displayName("§6" + emp.getDisplayName())
                    .lore(
                            "§7Level: §e" + level,
                            "§7Produces: §6" + factory.getEmployeeProduction(id, emp) + " §7/s",
                            "",
                            "§7Upgrade cost: §6" + String.format("%,d", cost) + " Chocolate",
                            "§eClick to upgrade!")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (factory.upgradeEmployee(id, emp)) {
                            player.sendMessage("§aUpgraded §6" + emp.getDisplayName()
                                    + " §ato level §e" + factory.getEmployeeLevel(id, emp) + "§a!");
                        } else {
                            player.sendMessage("§cNot enough Chocolate to upgrade " + emp.getDisplayName() + ".");
                        }
                        open(player);
                    });
        }

        setItem(28, display(Material.COOKIE, "§6Hand-Baked Chocolate", "§7Increases chocolate per click."));
        setItem(29, display(Material.CLOCK, "§6Time Tower", "§7Multiplies production for a time."));
        setItem(30, display(Material.RABBIT_FOOT, "§6Rabbit Shrine", "§7Boosts rabbit production."));
        setItem(31, display(Material.LEAD, "§6Coach Jackrabbit", "§7+1% production per employee level."));

        setItem(45, display(Material.EMERALD, "§aChocolate Shop", "§7Spend your chocolate."));
        setItem(46, display(Material.PLAYER_HEAD, "§aHoppity's Collection", "§7Track the rabbits you've found."));
        setItem(48, display(Material.LADDER, "§aFactory Milestones", "§7View your milestones."));
        setItem(50, display(Material.MILK_BUCKET, "§aFactory Ranking", "§7See the leaderboard."));
        setItem(52, display(Material.OAK_FENCE, "§aRabbit Barn", "§7House more rabbits."));

        setItem(49, new ItemBuilder(Material.BARRIER).displayName("§cClose").build(),
                e -> { e.setCancelled(true); player.closeInventory(); });
    }

    private static ItemStack display(Material mat, String name, String... lore) {
        return new ItemBuilder(mat).displayName(name).lore(lore).build();
    }
}
