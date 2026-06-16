package com.skyblock.plugin.minion;

import java.util.UUID;

/** @deprecated Use {@link com.skyblock.plugin.minion.model.Minion} */
@Deprecated
public class Minion extends com.skyblock.plugin.minion.model.Minion {
    public Minion(UUID id, UUID owner,
                  com.skyblock.plugin.minion.model.Minion.MinionType type,
                  com.skyblock.plugin.minion.model.Minion.MinionTier tier) {
        super(id, owner, type, tier);
    }
}
