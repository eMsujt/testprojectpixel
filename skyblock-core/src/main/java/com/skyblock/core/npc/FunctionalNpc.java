package com.skyblock.core.npc;

import com.skyblock.core.menu.AuctionHouseMenu;
import com.skyblock.core.menu.BankMenu;
import com.skyblock.core.menu.BazaarMenu;
import com.skyblock.core.menu.MuseumMenu;
import com.skyblock.core.menu.PetMenu;
import com.skyblock.core.menu.ReforgeMenu;
import com.skyblock.core.menu.SkyBlockMenu;
import com.skyblock.core.menu.WardrobeMenu;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * A placeable SkyBlock NPC whose right-click opens a specific feature menu.
 * Operators place one in-world with {@code /setnpclocation <id>}; the
 * {@link FunctionalNpcManager} spawns its armor stand and persists the location,
 * and {@link FunctionalNpcListener} opens {@link #open(Player)} on interaction.
 */
public enum FunctionalNpc {

    BANKER("banker", "§aBanker", p -> new BankMenu(p).open(p)),
    AUCTION_MASTER("auction_master", "§6Auction Master", p -> new AuctionHouseMenu(p).open(p)),
    BAZAAR("bazaar", "§eBazaar", p -> new BazaarMenu(p).open(p)),
    MUSEUM("museum", "§bMuseum Curator", p -> new MuseumMenu(p.getUniqueId()).open(p)),
    BLACKSMITH("blacksmith", "§cBlacksmith", p -> new ReforgeMenu(p).open(p)),
    PET_SITTER("pet_sitter", "§dPet Sitter", p -> new PetMenu(p).open(p)),
    WARDROBE("wardrobe", "§eWardrobe", p -> new WardrobeMenu(p).open(p)),
    GUIDE("guide", "§aGuide", p -> new SkyBlockMenu(p).open(p));

    /** Command id used by {@code /setnpclocation} and in the YAML store. */
    public final String id;
    /** Name shown above the NPC's armor stand. */
    public final String displayName;
    private final Consumer<Player> opener;

    FunctionalNpc(String id, String displayName, Consumer<Player> opener) {
        this.id = id;
        this.displayName = displayName;
        this.opener = opener;
    }

    /** Opens this NPC's feature menu for the player. */
    public void open(Player player) {
        opener.accept(player);
    }

    /** Looks up a functional NPC by its command id (case-insensitive), or {@code null}. */
    public static FunctionalNpc byId(String id) {
        if (id == null) {
            return null;
        }
        for (FunctionalNpc npc : values()) {
            if (npc.id.equalsIgnoreCase(id)) {
                return npc;
            }
        }
        return null;
    }
}
