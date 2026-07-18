package ru.ilezzov.pluginTemplate.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import ru.ilezzov.pluginTemplate.BuildConfig;
import ru.ilezzov.pluginTemplate.Main;
import ru.ilezzov.pluginTemplate.command.executors.MainCommand;
import ru.ilezzov.pluginTemplate.logger.ConsoleMessage;
import ru.ilezzov.pluginTemplate.logger.PluginLogger;

import java.util.Map;

public class CommandManager {
    private final Main plugin;
    private final PluginLogger pluginLogger;
    private final ConsoleMessage consoleMessage;

    public CommandManager(final Main plugin) {
        this.plugin = plugin;
        this.pluginLogger = plugin.getPluginLogger();
        this.consoleMessage = plugin.getConsoleMessage();
    }

    public void loadCommands() {
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.command.all.registration")
        );

        final Map<String, CommandExecutor> commands = getCommands();

        int commandRegistered = 0;
        for (final String commandName : commands.keySet()) {
            final PluginCommand command = this.plugin.getCommand(commandName);

            if (command != null) {
                final CommandExecutor commandExecutor = commands.get(commandName);
                command.setExecutor(commandExecutor);

                if (commandExecutor instanceof TabCompleter completer) {
                    command.setTabCompleter(completer);
                }

                this.pluginLogger.debug(
                        this.consoleMessage.getMessage("plugin.command.registered", commandName)
                );
                commandRegistered++;
            } else {
                this.pluginLogger.debug(
                        this.consoleMessage.getMessage("plugin.command.not_found", commandName)
                );
            }
        }

        this.pluginLogger.debug(
                this.consoleMessage.getMessage("plugin.command.all.registered", commandRegistered)
        );
    }

    private Map<String, CommandExecutor> getCommands() {
        return Map.ofEntries(
                Map.entry(BuildConfig.MAIN_COMMAND, new MainCommand(this.plugin))
        );
    }
}
