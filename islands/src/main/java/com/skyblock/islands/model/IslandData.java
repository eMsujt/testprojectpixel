package com.skyblock.islands.model;

import java.util.List;
import java.util.UUID;

/**
 * Holds the persistent state for a single SkyBlock island.
 */
public record IslandData(UUID owner, List<UUID> members) {}
