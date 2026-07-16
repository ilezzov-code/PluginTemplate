package ru.ilezzov.pluginTemplate;

import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginTemplate.logger.ConsoleMessage;
import ru.ilezzov.pluginTemplate.logger.PluginLogger;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class Main extends JavaPlugin {
    @Getter
    private PluginLogger pluginLogger;

    @Getter
    private ConsoleMessage consoleMessage;

    @Override
    public void onEnable() {
        this.pluginLogger = new PluginLogger(this);
        this.consoleMessage = new ConsoleMessage("messages");
    }

    @Override
    public void onDisable() {

    }
}
