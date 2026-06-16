package com.skyblock.core.manager;

/**
 * @deprecated Use {@link com.skyblock.core.skills.manager.SkillManager} instead.
 */
@Deprecated
public final class SkillManager {
    private SkillManager() {}

    public static com.skyblock.core.skills.manager.SkillManager getInstance() {
        return com.skyblock.core.skills.manager.SkillManager.getInstance();
    }
}
