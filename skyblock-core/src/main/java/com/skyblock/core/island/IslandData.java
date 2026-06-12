package com.skyblock.core.island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Holds the persistent state for a single SkyBlock island.
 */
public final class IslandData {

    private final UUID owner;
    private final List<UUID> members = new ArrayList<>();
    private int level;

    public IslandData(UUID owner) {
        this.owner = Objects.requireNonNull(owner, "owner");
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void addMember(UUID player) {
        Objects.requireNonNull(player, "player");
        if (!members.contains(player)) {
            members.add(player);
        }
    }

    public boolean removeMember(UUID player) {
        Objects.requireNonNull(player, "player");
        return members.remove(player);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
