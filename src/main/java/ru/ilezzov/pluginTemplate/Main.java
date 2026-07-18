package ru.ilezzov.pluginTemplate;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginTemplate.command.CommandManager;
import ru.ilezzov.pluginTemplate.event.EventManager;
import ru.ilezzov.pluginTemplate.file.ConfigFile;
import ru.ilezzov.pluginTemplate.file.MessageFile;
import ru.ilezzov.pluginTemplate.logger.ConsoleMessage;
import ru.ilezzov.pluginTemplate.logger.PluginLogger;
import ru.ilezzov.pluginTemplate.message.MessageManager;
import ru.ilezzov.pluginTemplate.version.VersionControl;
import ru.ilezzov.pluginTemplate.version.VersionData;
import ru.ilezzov.pluginTemplate.version.VersionManager;
import ru.ilezzov.pluginTemplate.version.VersionType;

import java.io.File;

import static ru.ilezzov.pluginTemplate.BuildConfig.*;

public final class Main extends JavaPlugin {
    @Getter
    private PluginLogger pluginLogger;

    @Getter
    private ConsoleMessage consoleMessage;

    @Getter
    private ConfigFile configFile;
    @Getter
    private MessageFile messageFile;

    @Getter
    private MessageManager messageManager;
    @Getter
    private VersionManager versionManager;
    @Getter
    private VersionControl versionControl;

    @Getter
    private EventManager eventManager;

    @Override
    public void onEnable() {
        this.pluginLogger = new PluginLogger(this);
        this.consoleMessage = new ConsoleMessage("messages");

        this.configFile = loadConfig();
        if (configFile.debug) {
            this.pluginLogger.setDebug(true);
            this.pluginLogger.debug(this.consoleMessage.getMessage("plugin.debug.enabled"));
        }

        final String messageFileName = this.configFile.language.concat(".yml");

        this.messageFile = loadMessageFile(messageFileName);
        this.messageManager = new MessageManager(this);

        this.pluginLogger.debug(this.consoleMessage.getMessage("file.message.loaded", messageFileName));

        this.versionManager = new VersionManager(this.pluginLogger, this.consoleMessage);
        this.versionManager.loadVersionData();

        if (this.configFile.versionControl.checkOnStartup) {
            if (!checkVersion()) {
                this.stop();
                return;
            }
        }

        this.versionControl = new VersionControl(this);
        this.versionControl.startBackgroundCheckTask();
        this.versionControl.startCriticalNotifyTask();

        this.eventManager = new EventManager(this);
        eventManager.registerEvents();

        new CommandManager(this).loadCommands();
    }

    @Override
    public void onDisable() {

    }

    public void stop() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void reloadMessageFile(final String oldFile, final String file) {
        if (oldFile.equalsIgnoreCase(file)) {
            this.messageFile.load();
            return;
        }

        this.messageFile = loadMessageFile(file.concat(".yml"));
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

    private MessageFile loadMessageFile(final String file) {
        final File messageDir = new File(this.getDataFolder(), "messages");
        final File messageFile = new File(messageDir, file);

        if (!messageFile.exists()) {
            messageFile.getParentFile().mkdirs();
            this.saveResource("messages/".concat(file), false);
        }

        return (MessageFile) ConfigManager.create(MessageFile.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer());
                    opt.bindFile(messageFile);
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }

    private boolean checkVersion() {
        final VersionData versionData = this.versionManager.getVersionData();

        if (versionData == null) {
            return true;
        }

        final VersionType versionType = this.versionManager.getVersionType();
        final String latestVersion = versionData.getLatest().getVersion();
        final String latestDownloadLink = versionData.getLatest().getDownloadUrl();

        return switch (versionType) {
            case LATEST -> {
                this.pluginLogger.info(
                        this.consoleMessage.getMessage("version.message.latest", PLUGIN_VERSION)
                );
                yield true;
            }
            case SUPPORTED -> {
                this.pluginLogger.warn(
                        this.consoleMessage.getMessage("version.message.supported", PLUGIN_VERSION, latestVersion)
                );
                this.pluginLogger.warn(
                        this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                );
                yield true;
            }
            case OUTDATED -> {
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.outdated", PLUGIN_VERSION, this.consoleMessage.getMessage("version.message.action.impossible_launch"), latestVersion)
                );
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                );
                yield false;
            }
            case BLACKLIST -> {
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.blacklist", PLUGIN_VERSION, this.consoleMessage.getMessage("version.message.action.impossible_launch"), latestVersion)
                );
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                );
                yield false;
            }
            case UNREACHABLE -> true;
        };
    }
}
