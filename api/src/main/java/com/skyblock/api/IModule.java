package com.skyblock.api;

/**
 * Lifecycle contract for every SkyBlock game module.
 *
 * <p>Implementations are registered with the plugin on startup and are enabled
 * or disabled in dependency order by the module registry.
 */
public interface IModule {

    /** Called once when the plugin is enabling this module. */
    void onEnable();

    /** Called once when the plugin is disabling this module. */
    void onDisable();

    /**
     * Called to reload configuration without a full server restart.
     * Default implementation cycles disable → enable.
     */
    default void onReload() {
        onDisable();
        onEnable();
    }

    /** Stable, human-readable name used in logs and commands (e.g. {@code "Economy"}). */
    String getName();

    /** Returns {@code true} while the module is between {@link #onEnable()} and {@link #onDisable()}. */
    boolean isEnabled();
}
