package com.skyblock.core;

import com.skyblock.api.IModule;
import com.skyblock.api.SkyBlockAPI;
import com.skyblock.api.SkyBlockAPIProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Main entry point for the SkyBlock core plugin.
 *
 * <p>Holds the singleton plugin instance, implements {@link SkyBlockAPI} as the
 * live module registry, and drives the enable/disable lifecycle.</p>
 */
public final class SkyBlockPlugin extends JavaPlugin implements SkyBlockAPI {

    private static SkyBlockPlugin instance;

    private final Map<Class<? extends IModule>, IModule> modules = new LinkedHashMap<>();

    /**
     * Returns the active plugin instance.
     *
     * @return the singleton {@link SkyBlockPlugin} instance
     * @throws IllegalStateException if the plugin has not been enabled yet
     */
    public static SkyBlockPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SkyBlockPlugin is not enabled");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        SkyBlockAPIProvider.set(this);
        getLogger().info("SkyBlock core enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyBlock core disabled.");
        SkyBlockAPIProvider.clear();
        instance = null;
    }

    /**
     * Registers a module so it can be retrieved via {@link SkyBlockAPI#getModule}.
     * Must be called before the module's {@link IModule#onEnable()} is invoked.
     */
    public <T extends IModule> void registerModule(Class<T> type, T module) {
        modules.put(type, module);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IModule> Optional<T> findModule(Class<T> type) {
        return Optional.ofNullable((T) modules.get(type));
    }

    @Override
    public Collection<IModule> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }
}
