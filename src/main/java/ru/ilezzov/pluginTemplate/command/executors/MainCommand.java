package ru.ilezzov.pluginTemplate.command.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ilezzov.pluginTemplate.BuildConfig;
import ru.ilezzov.pluginTemplate.Main;
import ru.ilezzov.pluginTemplate.file.MessageFile;
import ru.ilezzov.pluginTemplate.message.MessageManager;
import ru.ilezzov.pluginTemplate.permission.PermissionManager;
import ru.ilezzov.pluginTemplate.permission.Permissions;
import ru.ilezzov.pluginTemplate.placeholder.PluginPlaceholder;
import ru.ilezzov.pluginTemplate.version.VersionControl;
import ru.ilezzov.pluginTemplate.version.VersionData;
import ru.ilezzov.pluginTemplate.version.VersionManager;
import ru.ilezzov.pluginTemplate.version.VersionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements CommandExecutor, TabExecutor {
    private final Main plugin;
    private final VersionManager versionManager;
    private final MessageManager messageManager;

    private static final Map<String, String> ROOT_COMMANDS = Map.of(
            "reload", Permissions.RELOAD,
            "version", Permissions.VERSION_COMMAND
    );

    public MainCommand(final Main plugin) {
        this.plugin = plugin;
        this.versionManager = plugin.getVersionManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );

        placeholder.addPlaceholder("{CONTACT}", BuildConfig.WEBSITE);

        if (args.length == 0) {
            handleHelp(sender, placeholder);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "version" -> handleVersion(sender);
            case "reload" -> handleReload(sender, placeholder);
            default -> handleHelp(sender, placeholder);
        };

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final String input = args[0];
            for (Map.Entry<String, String> entry : ROOT_COMMANDS.entrySet()) {
                final String subCommand = entry.getKey();
                final String permission = entry.getValue();

                if (PermissionManager.hasPermission(sender, permission) && StringUtil.startsWithIgnoreCase(subCommand, input)) {
                    completions.add(subCommand);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private void handleHelp(final CommandSender sender, final PluginPlaceholder placeholder) {
        this.messageManager.send(
                sender, this.message().mainCommand.help, placeholder
        );
    }

    private void handleVersion(@NotNull CommandSender sender) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );

        if (!PermissionManager.hasPermission(sender, Permissions.VERSION_COMMAND)) {
            this.messageManager.send(
                    sender, this.message().plugin.noPerms, placeholder
            );
            return;
        }

        this.messageManager.send(
                sender, this.message().version.loading,placeholder
        );

        CompletableFuture.runAsync(this.versionManager::loadVersionData).thenRun(() -> {
            final VersionData versionData = this.versionManager.getVersionData();

            if (versionData == null) {
                this.messageManager.sendFromThread(
                       sender, this.message().version.error, placeholder
                );
                return;
            }

            placeholder.addPlaceholder("{CURRENT_VERSION}", BuildConfig.PLUGIN_VERSION);
            placeholder.addPlaceholder("{LATEST_VERSION}", versionData.getLatest().getVersion());
            
            if (this.versionManager.getVersionType() == VersionType.LATEST) {
                this.messageManager.sendFromThread(
                        sender, this.message().version.latest, placeholder
                );
                return;
            }

            switch (versionManager.getVersionType()) {
                case SUPPORTED -> this.messageManager.sendFromThread(
                        sender, this.message().version.supported, placeholder
                );
                case BLACKLIST -> this.messageManager.sendFromThread(
                        sender, this.message().version.blacklist, placeholder
                );
                case OUTDATED -> this.messageManager.sendFromThread(
                        sender, this.message().version.outdated, placeholder
                );
            }

            this.messageManager.sendFromThread(
                    sender, this.message().version.download, placeholder
            );
        });
    }

    private void handleReload(final CommandSender sender, final PluginPlaceholder placeholder) {
        if (!PermissionManager.hasPermission(sender, Permissions.RELOAD)) {
            this.messageManager.send(
                    sender, this.message().plugin.noPerms, placeholder
            );
            return;
        }

        final String oldMessageFile = this.plugin.getConfigFile().language;

        this.plugin.getConfigFile().load();

        final String messageFile = this.plugin.getConfigFile().language;
        this.plugin.reloadMessageFile(oldMessageFile, messageFile);

        placeholder.addPlaceholder("{P}", this.message().plugin.prefix);
        placeholder.addPlaceholder("{P_E}", this.message().plugin.prefixError);

        this.plugin.getEventManager().reloadEvents();
        
        final VersionControl versionControl = this.plugin.getVersionControl();
        versionControl.stop();
        versionControl.startBackgroundCheckTask();
        versionControl.startCriticalNotifyTask();

        this.messageManager.send(
                sender, this.message().plugin.reload, placeholder
        );
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
