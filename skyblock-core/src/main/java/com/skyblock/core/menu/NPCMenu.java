package com.skyblock.core.menu;

import com.skyblock.core.npc.NpcManager;
import com.skyblock.core.npc.NpcManager.NpcDefinition;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * NPC directory hub. A 54-slot (6-row) chest titled {@code §eNPCs} showing one
 * player-head icon per registered {@link NpcDefinition}, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Clicking an NPC opens that NPC's
 * shop via {@link NPCShopMenu}.
 */
public final class NPCMenu extends AbstractSkyBlockMenu {

    private static final int[] NPC_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    public NPCMenu(Player player) {
        super(player, "§eNPCs", 6);
    }

    @Override
    protected void populate() {
        fillBorder();

        List<NpcDefinition> npcs = NpcManager.getInstance().getAllNpcs();
        for (int i = 0; i < npcs.size() && i < NPC_SLOTS.length; i++) {
            final NpcDefinition npc = npcs.get(i);
            setItem(NPC_SLOTS[i], SkyblockUtils.buildItem(Material.PLAYER_HEAD,
                    "§e" + npc.name(),
                    "§7Click to browse the shop!"),
                    event -> {
                        event.setCancelled(true);
                        event.getWhoClicked().closeInventory();
                        new NPCShopMenu(npc.shopId()).open((Player) event.getWhoClicked());
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = SkyblockUtils.buildItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
