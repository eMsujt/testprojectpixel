package com.skyblock.core.util;

import com.skyblock.core.SkyBlockCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * One-shot in-game sign text input — the professional alternative to chat prompts.
 * Opens the vanilla sign editor for the player (no chat), reads what they type on
 * the top line, and runs a callback. Implemented natively on Paper via a transient
 * sign block (placed at the player's own position and restored immediately) plus
 * {@link SignChangeEvent} — no NMS, no packets, no chat.
 *
 * <p>Register the singleton once: {@code registerEvents(SignInput.getInstance(), plugin)}.</p>
 */
public final class SignInput implements Listener {

    private static final SignInput INSTANCE = new SignInput();

    public static SignInput getInstance() {
        return INSTANCE;
    }

    private SignInput() {
    }

    private record Pending(Location loc, BlockData original, Consumer<String> callback) {}

    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    /**
     * Opens a sign editor for {@code player} with {@code label} shown below the input
     * line, then runs {@code callback} with the trimmed first line (empty string if
     * they typed nothing / cancelled).
     */
    public static void request(Player player, String label, Consumer<String> callback) {
        INSTANCE.open(player, label, callback);
    }

    private void open(Player player, String label, Consumer<String> callback) {
        UUID uid = player.getUniqueId();
        Pending prev = pending.remove(uid);
        if (prev != null) {
            restore(prev);
        }

        Block block = player.getLocation().getBlock();
        BlockData original = block.getBlockData();
        block.setType(Material.OAK_SIGN, false);
        BlockState state = block.getState();
        if (!(state instanceof Sign sign)) {
            block.setBlockData(original, false);
            return;
        }
        sign.setLine(1, "§7^^^^^^^^^^^^^^^");
        sign.setLine(2, label == null ? "" : label);
        sign.setLine(3, "§7Type above");
        sign.update(true, false);

        Pending p = new Pending(block.getLocation(), original, callback);
        pending.put(uid, p);

        // Open one tick later so the client has the block before the editor opens.
        Bukkit.getScheduler().runTask(SkyBlockCore.getInstance(), () -> {
            if (pending.get(uid) != p || !player.isOnline()) {
                return;
            }
            if (block.getState() instanceof Sign live) {
                player.openSign(live, Side.FRONT);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        Pending p = pending.get(uid);
        if (p == null || !event.getBlock().getLocation().equals(p.loc())) {
            return;
        }
        pending.remove(uid);
        String first = event.getLine(0);
        String input = first == null ? "" : first.trim();
        // Restore the block and run the callback next tick (the callback typically
        // opens an inventory, which can't happen inside the sign-change event).
        Bukkit.getScheduler().runTask(SkyBlockCore.getInstance(), () -> {
            restore(p);
            p.callback().accept(input);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Pending p = pending.remove(event.getPlayer().getUniqueId());
        if (p != null) {
            restore(p);
        }
    }

    private void restore(Pending p) {
        Block block = p.loc().getBlock();
        if (block.getType() == Material.OAK_SIGN) {
            block.setBlockData(p.original(), false);
        }
    }
}
