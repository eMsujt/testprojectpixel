package com.skyblock.dungeons;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.DungeonManager} instead.
 */
@Deprecated
public final class DungeonManager {

    private static final com.skyblock.core.manager.DungeonManager DELEGATE =
            com.skyblock.core.manager.DungeonManager.getInstance();

    private static final DungeonManager INSTANCE = new DungeonManager();

    private DungeonManager() {}

    /** @deprecated Use {@link com.skyblock.core.manager.DungeonManager#getInstance()}. */
    @Deprecated
    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    public void recordDungeonEvent(UUID playerUuid, String summary) {
        DELEGATE.recordDungeonEvent(playerUuid, summary);
    }

    public List<String> getDungeonHistory(UUID playerUuid) {
        return DELEGATE.getDungeonHistory(playerUuid);
    }

    public Map<UUID, List<String>> getAllDungeonHistory() {
        return DELEGATE.getAllDungeonHistory();
    }

    public String getDungeonStats(UUID playerId) {
        return DELEGATE.getDungeonStats(playerId);
    }

    public void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }
}
