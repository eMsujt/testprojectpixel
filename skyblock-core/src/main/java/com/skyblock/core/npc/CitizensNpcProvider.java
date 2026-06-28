package com.skyblock.core.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawns the functional NPCs as real <b>Citizens</b> player-model NPCs and opens
 * their menu on right-click. This class is only loaded/used when the Citizens
 * plugin is installed; {@link FunctionalNpcManager} falls back to armor stands
 * otherwise.
 *
 * <p>NPCs are tagged with their {@link FunctionalNpc} id so right-clicks route
 * to the right menu, and {@link #despawnAll()} destroys every tagged NPC (so a
 * fresh set is spawned each load, even if Citizens persisted the old ones).</p>
 */
public final class CitizensNpcProvider implements Listener {

    /** Persistent-data key carrying the FunctionalNpc id on each NPC. */
    private static final String TAG = "sb_functional_npc";

    /** True if the Citizens plugin is installed and enabled. */
    public static boolean isCitizensEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }

    /** Destroys every functional-NPC Citizens NPC (current or persisted from a previous run). */
    public void despawnAll() {
        destroyMatching(npc -> npc.data().has(TAG));
    }

    /** Destroys the Citizens NPC(s) for one functional-NPC type. */
    public void despawn(FunctionalNpc type) {
        destroyMatching(npc -> {
            Object tag = npc.data().get(TAG);
            return tag instanceof String id && id.equals(type.id);
        });
    }

    private void destroyMatching(java.util.function.Predicate<NPC> match) {
        List<NPC> toRemove = new ArrayList<>();
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (match.test(npc)) {
                toRemove.add(npc);
            }
        }
        for (NPC npc : toRemove) {
            npc.destroy();
        }
    }

    /** Creates and spawns a player-model NPC for {@code type} at {@code location}. */
    public void spawn(FunctionalNpc type, Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, type.displayName);
        npc.data().setPersistent(TAG, type.id);
        npc.setProtected(true);

        // Apply the real Hypixel skin (texture + signature), if one is configured.
        NpcSkin skin = NpcSkins.get(type);
        if (skin != null) {
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinPersistent(type.id, skin.signature(), skin.texture());
        }

        // Make the NPC turn to face nearby players, like Hypixel's hub NPCs.
        npc.getOrAddTrait(LookClose.class).lookClose(true);

        npc.spawn(location);
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        Object tag = event.getNPC().data().get(TAG);
        if (!(tag instanceof String id)) {
            return;
        }
        FunctionalNpc type = FunctionalNpc.byId(id);
        if (type != null && event.getClicker() instanceof Player player) {
            type.open(player);
        }
    }
}
