package com.skyblock.scoreboard;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages each player's sidebar-scoreboard state: which
 * {@link ScoreboardSection sections} are visible and the custom text line
 * shown for each section.
 *
 * <p>Players start with no sections visible and no custom text set. Not
 * thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ScoreboardManager {

    private final Map<UUID, Set<ScoreboardSection>> visibleSections = new HashMap<>();
    private final Map<UUID, Map<ScoreboardSection, String>> customLines = new HashMap<>();

    /**
     * Shows the given section on the player's scoreboard.
     *
     * @param playerId the player's UUID
     * @param section  the section to show
     */
    public void showSection(UUID playerId, ScoreboardSection section) {
        visibleSections.computeIfAbsent(playerId, k -> EnumSet.noneOf(ScoreboardSection.class))
                .add(section);
    }

    /**
     * Hides the given section from the player's scoreboard.
     *
     * @param playerId the player's UUID
     * @param section  the section to hide
     */
    public void hideSection(UUID playerId, ScoreboardSection section) {
        Set<ScoreboardSection> sections = visibleSections.get(playerId);
        if (sections != null) {
            sections.remove(section);
        }
    }

    /**
     * Returns whether the section is currently visible on the player's
     * scoreboard.
     *
     * @param playerId the player's UUID
     * @param section  the section to query
     * @return {@code true} if the section is visible
     */
    public boolean isSectionVisible(UUID playerId, ScoreboardSection section) {
        Set<ScoreboardSection> sections = visibleSections.get(playerId);
        return sections != null && sections.contains(section);
    }

    /**
     * Returns an unmodifiable view of all sections currently visible on the
     * player's scoreboard, ordered by {@link ScoreboardSection#getDisplayOrder()}.
     *
     * @param playerId the player's UUID
     * @return the visible sections, never {@code null}
     */
    public Set<ScoreboardSection> getVisibleSections(UUID playerId) {
        Set<ScoreboardSection> sections = visibleSections.get(playerId);
        if (sections == null || sections.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(sections);
    }

    /**
     * Sets the custom text line displayed for the given section on the
     * player's scoreboard.
     *
     * @param playerId the player's UUID
     * @param section  the section whose line to set
     * @param line     the text to display, must not be {@code null}
     * @throws NullPointerException if {@code line} is {@code null}
     */
    public void setLine(UUID playerId, ScoreboardSection section, String line) {
        if (line == null) {
            throw new NullPointerException("line must not be null");
        }
        customLines.computeIfAbsent(playerId, k -> new EnumMap<>(ScoreboardSection.class))
                .put(section, line);
    }

    /**
     * Returns the custom text line for the given section on the player's
     * scoreboard.
     *
     * @param playerId the player's UUID
     * @param section  the section to query
     * @return the custom line, or {@code null} if none has been set
     */
    public String getLine(UUID playerId, ScoreboardSection section) {
        Map<ScoreboardSection, String> lines = customLines.get(playerId);
        return lines == null ? null : lines.get(section);
    }

    /**
     * Clears all scoreboard state for the player, hiding all sections and
     * removing any custom line text.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        visibleSections.remove(playerId);
        customLines.remove(playerId);
    }
}
