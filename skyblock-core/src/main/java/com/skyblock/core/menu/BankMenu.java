package com.skyblock.core.menu;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Personal Bank Account" menu, opened from the SkyBlock Menu. Laid out and
 * worded 1:1 with Hypixel's Bank GUI (verbatim tooltip lore from the wiki's
 * Bank/UI page). The main view has Deposit Coins (Chest, slot 11), Withdraw
 * Coins (Dropper, 13), Recent transactions (Map, 15), Information (Redstone
 * Torch, 32) and Bank Upgrades (Block of Gold, 35); Deposit/Withdraw open
 * sub-views offering all / half (and a custom-amount placeholder), like Hypixel.
 */
public final class BankMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§6Personal Bank Account";

    /** null = main account view; "deposit" / "withdraw" = the amount sub-views. */
    private final String view;

    public BankMenu(Player player) {
        this(player, null);
    }

    private BankMenu(Player player, String view) {
        super(player, TITLE, 4);
        this.view = view;
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 36; slot++) setItem(slot, pane);

        if ("deposit".equals(view)) {
            buildDeposit();
        } else if ("withdraw".equals(view)) {
            buildWithdraw();
        } else {
            buildMain();
        }
    }

    private void buildMain() {
        UUID uuid = player.getUniqueId();
        BankManager bank = BankManager.getInstance();

        double balance = bank.getBalance(uuid);
        BankTier tier = bank.getTier(uuid);
        String bal = String.format("%,.0f", balance);
        String cap = String.format("%,.0f", tier.getInterestCap());
        String rate = trimRate(tier.getInterestRate());

        setItem(11, new ItemBuilder(Material.CHEST)
                .displayName("§aDeposit Coins")
                .lore(
                        "§7Current balance: §6" + bal,
                        "",
                        "§7Store coins in the bank to keep",
                        "§7them safe while you go on",
                        "§7adventures!",
                        "",
                        "§7You will earn §b" + rate + "% §7interest every",
                        "§7season for your first §610 million",
                        "§7banked coins.",
                        "",
                        "§eClick to make a deposit!")
                .build(),
                e -> { e.setCancelled(true); new BankMenu(player, "deposit").open(player); });

        setItem(13, new ItemBuilder(Material.DROPPER)
                .displayName("§aWithdraw Coins")
                .lore(
                        "§7Current balance: §6" + bal,
                        "",
                        "§7Take your coins out of the bank",
                        "§7in order to spend them.",
                        "",
                        "§eClick to withdraw coins!")
                .build(),
                e -> { e.setCancelled(true); new BankMenu(player, "withdraw").open(player); });

        setItem(15, new ItemBuilder(Material.MAP)
                .displayName("§aRecent transactions")
                .lore("§7There are no recent", "§7transactions!")
                .build(), e -> e.setCancelled(true));

        setItem(30, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });

        setItem(31, closeButton(), e -> { e.setCancelled(true); player.closeInventory(); });

        setItem(32, new ItemBuilder(Material.REDSTONE_TORCH)
                .displayName("§aInformation")
                .lore(
                        "§7Keep your coins safe in the bank!",
                        "§7You lose half the coins in your",
                        "§7purse when dying in combat.",
                        "",
                        "§7Balance limit: §6" + cap,
                        "",
                        "§7The banker rewards you with",
                        "§b" + rate + "% §7interest each season for the",
                        "§7coins in your bank balance.",
                        "",
                        "§7Account: §a" + tier.getDisplayName())
                .build(), e -> e.setCancelled(true));

        setItem(35, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Upgrades")
                .lore(
                        "§7Are you so rich that you can't",
                        "§7even store your coins?",
                        "",
                        "§7Current account: §a" + tier.getDisplayName(),
                        "§7Bank limit: §6" + cap,
                        "",
                        "§eClick to view upgrades!")
                .build(), e -> e.setCancelled(true));
    }

    private void buildDeposit() {
        long purse = EconomyManager.getInstance().getPurse(player.getUniqueId());

        setItem(11, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§aDeposit all coins")
                .lore("§7Purse: §6" + String.format("%,d", purse), "", "§eClick to deposit everything!")
                .build(), e -> { e.setCancelled(true); deposit(purse); });

        setItem(13, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§aDeposit half")
                .lore("§7Deposit: §6" + String.format("%,d", purse / 2), "", "§eClick to deposit half!")
                .build(), e -> { e.setCancelled(true); deposit(purse / 2); });

        setItem(15, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aCustom amount")
                .lore("§7Deposit a specific amount.", "", "§eClick to type an amount!")
                .build(), e -> { e.setCancelled(true); promptCustom(true); });

        backToMainButtons();
    }

    private void buildWithdraw() {
        long bal = (long) BankManager.getInstance().getBalance(player.getUniqueId());

        setItem(11, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§aWithdraw all coins")
                .lore("§7Bank: §6" + String.format("%,d", bal), "", "§eClick to withdraw everything!")
                .build(), e -> { e.setCancelled(true); withdraw(bal); });

        setItem(13, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§aWithdraw half")
                .lore("§7Withdraw: §6" + String.format("%,d", bal / 2), "", "§eClick to withdraw half!")
                .build(), e -> { e.setCancelled(true); withdraw(bal / 2); });

        setItem(15, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aCustom amount")
                .lore("§7Withdraw a specific amount.", "", "§eClick to type an amount!")
                .build(), e -> { e.setCancelled(true); promptCustom(false); });

        backToMainButtons();
    }

    private void deposit(long amount) {
        UUID uuid = player.getUniqueId();
        EconomyManager econ = EconomyManager.getInstance();
        long purse = econ.getPurse(uuid);
        if (amount <= 0 || purse <= 0) {
            player.sendMessage("§cYour purse is empty.");
            return;
        }
        if (amount > purse) {
            player.sendMessage("§cYou don't have that many coins in your purse.");
            return;
        }
        econ.withdraw(uuid, amount);
        BankManager.getInstance().deposit(uuid, amount);
        player.sendMessage("§aDeposited §6" + String.format("%,d", amount) + " §acoins into your bank.");
        new BankMenu(player, null).open(player);
    }

    private void withdraw(long amount) {
        UUID uuid = player.getUniqueId();
        long balance = (long) BankManager.getInstance().getBalance(uuid);
        if (amount <= 0 || balance <= 0) {
            player.sendMessage("§cYour bank is empty.");
            return;
        }
        if (amount > balance) {
            player.sendMessage("§cYou don't have that many coins in your bank.");
            return;
        }
        BankManager.getInstance().withdraw(uuid, amount);
        EconomyManager.getInstance().addPurse(uuid, amount);
        player.sendMessage("§aWithdrew §6" + String.format("%,d", amount) + " §acoins from your bank.");
        new BankMenu(player, null).open(player);
    }

    /** Prompts for a custom amount in chat, then runs the deposit/withdraw action. */
    private void promptCustom(boolean isDeposit) {
        player.closeInventory();
        player.sendMessage("§eType the amount to " + (isDeposit ? "deposit" : "withdraw")
                + " in chat §7(e.g. §f10000§7, §f2.5m§7), or type §ccancel§7.");
        com.skyblock.core.manager.ChatInputManager.getInstance().request(player.getUniqueId(), input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage("§cCancelled.");
                new BankMenu(player, null).open(player);
                return;
            }
            long amount = parseAmount(input);
            if (amount <= 0) {
                player.sendMessage("§c'" + input + "' is not a valid amount.");
                new BankMenu(player, isDeposit ? "deposit" : "withdraw").open(player);
                return;
            }
            if (isDeposit) {
                deposit(amount);
            } else {
                withdraw(amount);
            }
        });
    }

    /** Parses "10000", "1,000", "10k", "2.5m", "1b" into a coin amount; -1 if invalid. */
    private static long parseAmount(String raw) {
        String s = raw.trim().toLowerCase().replace(",", "");
        if (s.isEmpty()) return -1;
        double mult = 1;
        char last = s.charAt(s.length() - 1);
        if (last == 'k') { mult = 1_000D; s = s.substring(0, s.length() - 1); }
        else if (last == 'm') { mult = 1_000_000D; s = s.substring(0, s.length() - 1); }
        else if (last == 'b') { mult = 1_000_000_000D; s = s.substring(0, s.length() - 1); }
        try {
            double value = Double.parseDouble(s) * mult;
            return value <= 0 ? -1 : (long) value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Go Back (to the main account view) + Close, for the deposit / withdraw sub-views. */
    private void backToMainButtons() {
        setItem(30, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To Personal Bank Account")
                .build(),
                e -> { e.setCancelled(true); new BankMenu(player, null).open(player); });
        setItem(31, closeButton(), e -> { e.setCancelled(true); player.closeInventory(); });
    }

    private static ItemStack closeButton() {
        return new ItemBuilder(Material.BARRIER).displayName("§cClose").build();
    }

    private static String trimRate(double v) {
        return v == Math.floor(v) ? Long.toString((long) v) : Double.toString(v);
    }
}
