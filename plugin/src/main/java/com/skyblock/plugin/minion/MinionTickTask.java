package com.skyblock.plugin.minion;

import com.skyblock.core.minion.manager.MinionManager;

/** @deprecated Use {@link com.skyblock.plugin.minion.task.MinionTickTask} */
@Deprecated
public class MinionTickTask extends com.skyblock.plugin.minion.task.MinionTickTask {
    public MinionTickTask(MinionManager manager) {
        super(manager);
    }
}
