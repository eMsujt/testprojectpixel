package com.skyblock.plugin.minion;

import com.skyblock.core.manager.MinionManager;

/** @deprecated Use {@link com.skyblock.plugin.minion.task.MinionTickScheduler} */
@Deprecated
public class MinionTickScheduler extends com.skyblock.plugin.minion.task.MinionTickScheduler {
    public MinionTickScheduler(MinionManager manager) {
        super(manager);
    }
}
