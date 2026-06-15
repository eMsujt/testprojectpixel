package com.skyblock.core.skill;

import com.skyblock.core.skills.SkillManager;

/**
 * @deprecated Use {@link com.skyblock.core.skills.command.SkillCommand} instead.
 */
@Deprecated
public final class SkillCommand extends com.skyblock.core.skills.command.SkillCommand {
    public SkillCommand(SkillManager skillManager) {
        super(skillManager);
    }
}
