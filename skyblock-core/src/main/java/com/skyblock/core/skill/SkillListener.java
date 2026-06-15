package com.skyblock.core.skill;

import com.skyblock.core.skills.SkillManager;

/**
 * @deprecated Use {@link com.skyblock.core.skills.listener.SkillListener} instead.
 */
@Deprecated
public final class SkillListener extends com.skyblock.core.skills.listener.SkillListener {
    public SkillListener(SkillManager skillManager) {
        super(skillManager);
    }
}
