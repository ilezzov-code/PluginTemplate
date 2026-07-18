package ru.ilezzov.pluginTemplate.event;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.ilezzov.pluginTemplate.Main;
import ru.ilezzov.pluginTemplate.event.listeners.PlayerJoinEvent;
import ru.ilezzov.pluginTemplate.file.ConfigFile;
import ru.ilezzov.pluginTemplate.logger.ConsoleMessage;
import ru.ilezzov.pluginTemplate.logger.PluginLogger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager {
    private final Main plugin;
    private final ConfigFile configFile;
    private final PluginLogger pluginLogger;
    private final ConsoleMessage consoleMessage;

    private final Set<Listener> registeredListener = new HashSet<>();

    public EventManager(final Main plugin) {
        this.plugin = plugin;
        this.configFile = this.plugin.getConfigFile();
        this.pluginLogger = plugin.getPluginLogger();
        this.consoleMessage = plugin.getConsoleMessage();
    }

    public void registerEvents() {
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.event.all.registration")
        );

        final Map<Listener, Boolean> listeners = loadListeners();

        for (final Listener listener : listeners.keySet()) {
            if (listeners.get(listener)) {
                Bukkit.getPluginManager().registerEvents(listener, plugin);

                this.registeredListener.add(listener);
                this.pluginLogger.debug(
                        this.consoleMessage.getMessage("plugin.event.registered", listener.getClass().getSimpleName())
                );
            }
        }

        this.pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.event.all.registered", this.registeredListener.size())
        );
    }

    public void unregisterEvents() {
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.event.all.unregistration")
        );

        final int eventsCount = this.registeredListener.size();
        for (final Listener listener : this.registeredListener) {
            HandlerList.unregisterAll(listener);
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("plugin.event.unregistered", listener.getClass().getSimpleName())
            );
        }

        this.registeredListener.clear();

        pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.event.all.unregistered", eventsCount)
        );
    }

    public void reloadEvents() {
        this.unregisterEvents();
        this.registerEvents();
        pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.event.all.reloaded")
        );
    }

    private Map<Listener, Boolean> loadListeners() {
        return Map.ofEntries(
                Map.entry(new PlayerJoinEvent(plugin),
                        this.configFile.versionControl.notifyAdminsOnJoin)
        );
    }
}
