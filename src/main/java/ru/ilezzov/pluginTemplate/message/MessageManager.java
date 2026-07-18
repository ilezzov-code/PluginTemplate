package ru.ilezzov.pluginTemplate.message;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.ilezzov.pluginTemplate.Main;
import ru.ilezzov.pluginTemplate.file.MessageFile;
import ru.ilezzov.pluginTemplate.placeholder.PluginPlaceholder;

@AllArgsConstructor
public class MessageManager {
    private final Main plugin;

    public void send(final CommandSender commandSender, final String text, final PluginPlaceholder placeholder) {
        commandSender.sendMessage(
                this.parseComponent(text, placeholder)
        );
    }

    public void sendFromThread(final CommandSender sender, final String text ,final PluginPlaceholder placeholder) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            this.send(sender, text, placeholder);
        });
    }

    private Component parseComponent(final String text, final PluginPlaceholder placeholder) {
        return this.message().colorizer.parse(text, placeholder);
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
