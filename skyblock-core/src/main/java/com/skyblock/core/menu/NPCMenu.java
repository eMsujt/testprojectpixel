package com.skyblock.core.menu;

import com.skyblock.core.npc.NpcManager.NpcDefinition;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * 3-row chest GUI for a single NPC. The title is the NPC's name; slot 13
 * shows a player head representing the NPC. Left-clicking the head opens the
 * NPC's shop when one is configured.
 */
public final class NPCMenu extends Menu {

    private static final int HEAD_SLOT = 13;

    private final NpcDefinition npc;

    public NPCMenu(NpcDefinition npc) {
        super(npc.name(), 3);
        this.npc = npc;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 27; slot++) {
            setItem(slot, pane);
        }

        boolean hasShop = npc.shopId() != null && !npc.shopId().isBlank();
        ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§e" + npc.name())
                .lore(hasShop ? "§eClick to browse the shop!" : "§7Hello, traveller!")
                .build();
        setItem(HEAD_SLOT, head, event -> {
            event.setCancelled(true);
            if (hasShop) {
                event.getWhoClicked().closeInventory();
                new NPCShopMenu(npc.shopId()).open((org.bukkit.entity.Player) event.getWhoClicked());
            }
        });
    }
}
