package ru.ilezzov.pluginTemplate.manager;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.ilezzov.pluginTemplate.file.MessageFile;
import ru.ilezzov.pluginTemplate.placeholder.PluginPlaceholder;

@AllArgsConstructor
public class MessageManager {
    private final Plugin plugin;
    private final MessageFile messageFile;

    public void send(final CommandSender commandSender, final String text, final PluginPlaceholder placeholder) {
        commandSender.sendMessage(
                this.parseComponent(text, placeholder)
        );
    }

    private Component parseComponent(final String text, final PluginPlaceholder placeholder) {
        return this.messageFile.colorizer.parse(text, placeholder);
    }
}
