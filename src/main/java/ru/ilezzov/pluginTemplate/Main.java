package ru.ilezzov.pluginTemplate;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginTemplate.file.ConfigFile;
import ru.ilezzov.pluginTemplate.logger.ConsoleMessage;
import ru.ilezzov.pluginTemplate.logger.PluginLogger;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class Main extends JavaPlugin {
    @Getter
    private PluginLogger pluginLogger;

    @Getter
    private ConsoleMessage consoleMessage;

    @Getter
    private ConfigFile configFile;

    @Override
    public void onEnable() {
        this.pluginLogger = new PluginLogger(this);
        this.consoleMessage = new ConsoleMessage("messages");

        this.configFile = loadConfig();
        if (configFile.debug) {
            this.pluginLogger.setDebug(true);
            pluginLogger.debug(this.consoleMessage.getMessage("system.debug.enabled"));
        }
    }

    @Override
    public void onDisable() {

    }

    private ConfigFile loadConfig() {
        return (ConfigFile) ConfigManager.create(ConfigFile.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                    opt.bindFile(new File(this.getDataFolder(), "config.yml"));
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }
}
