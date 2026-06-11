package com.skyblock.api;

import java.util.Collection;
import java.util.Optional;

/**
 * Top-level service locator for the SkyBlock plugin.
 *
 * <p>The concrete implementation lives in the {@code core} module. Consumers
 * should obtain the instance via {@link #get()} rather than depending on
 * the core module directly.
 *
 * <pre>{@code
 * SkyBlockAPI api = SkyBlockAPI.get();
 * EconomyModule eco = api.getModule(EconomyModule.class);
 * }</pre>
 */
public interface SkyBlockAPI {

    // -------------------------------------------------------------------------
    // Static accessor
    // -------------------------------------------------------------------------

    /**
     * Returns the live {@link SkyBlockAPI} instance.
     *
     * @throws IllegalStateException if the plugin has not finished enabling yet
     *                               or has already been disabled
     */
    static SkyBlockAPI get() {
        return SkyBlockAPIProvider.require();
    }

    // -------------------------------------------------------------------------
    // Module registry
    // -------------------------------------------------------------------------

    /**
     * Returns the module registered for {@code type}, or an empty optional if
     * no such module is registered.
     */
    <T extends IModule> Optional<T> findModule(Class<T> type);

    /**
     * Returns the module registered for {@code type}.
     *
     * @throws IllegalArgumentException if no module is registered for {@code type}
     */
    default <T extends IModule> T getModule(Class<T> type) {
        return findModule(type).orElseThrow(
                () -> new IllegalArgumentException("No module registered for " + type.getSimpleName()));
    }

    /** Returns {@code true} if a module of {@code type} is currently enabled. */
    default boolean isModuleEnabled(Class<? extends IModule> type) {
        return findModule(type).map(IModule::isEnabled).orElse(false);
    }

    /** Returns an unmodifiable snapshot of all registered modules. */
    Collection<IModule> getModules();
}
