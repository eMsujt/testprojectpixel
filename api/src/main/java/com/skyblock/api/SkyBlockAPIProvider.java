package com.skyblock.api;

/**
 * Internal holder that the {@code core} module writes on plugin enable/disable.
 *
 * <p>Nothing outside of {@code core} should call {@link #set} or {@link #clear}.
 */
public final class SkyBlockAPIProvider {

    private static volatile SkyBlockAPI instance;

    private SkyBlockAPIProvider() {}

    /** Called by {@code SkyBlockPlugin.onEnable()} — do not call elsewhere. */
    public static void set(SkyBlockAPI api) {
        if (api == null) throw new NullPointerException("api");
        instance = api;
    }

    /** Called by {@code SkyBlockPlugin.onDisable()} — do not call elsewhere. */
    public static void clear() {
        instance = null;
    }

    /**
     * Returns the live instance.
     *
     * @throws IllegalStateException if the plugin is not enabled
     */
    public static SkyBlockAPI require() {
        SkyBlockAPI api = instance;
        if (api == null) {
            throw new IllegalStateException("SkyBlockAPI is not available — plugin is not enabled");
        }
        return api;
    }
}
