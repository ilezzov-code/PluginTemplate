package ru.ilezzov.pluginTemplate.logger;

import lombok.Setter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;
import ru.ilezzov.pluginTemplate.color.LegacySerialize;
import ru.ilezzov.pluginTemplate.placeholder.PluginPlaceholder;

public class PluginLogger {
    private final ComponentLogger console;

    private static final String DEBUG_PREFIX = "<dark_gray>[<aqua>DEBUG</aqua>]</dark_gray> ";

    @Setter
    private boolean debug = false;

    public PluginLogger(final Plugin plugin) {
        this.console = plugin.getComponentLogger();
    }

    public void info(final String text) {
        console.info(
                LegacySerialize.serialize(text)
        );
    }

    public void info(final String text, final PluginPlaceholder placeholder) {
        console.info(
                LegacySerialize.serialize(
                        PluginPlaceholder.replacePlaceholder(text, placeholder)
                )
        );
    }

    public void warn(final String text) {
        console.warn(
                LegacySerialize.serialize(text)
        );
    }

    public void warn(final String text, final PluginPlaceholder placeholder) {
        console.warn(
                LegacySerialize.serialize(
                        PluginPlaceholder.replacePlaceholder(text, placeholder)
                )
        );
    }

    public void warn(final String text, final Throwable throwable) {
        console.warn(
                LegacySerialize.serialize(text),
                throwable
        );
    }

    public void warn(final String text, final PluginPlaceholder placeholder, final Throwable throwable) {
        console.warn(
                LegacySerialize.serialize(
                        PluginPlaceholder.replacePlaceholder(text, placeholder)
                ),
                throwable
        );
    }

    public void error(final String text) {
        console.error(
                LegacySerialize.serialize(text)
        );
    }

    public void error(final String text, final PluginPlaceholder placeholder) {
        console.error(
                LegacySerialize.serialize(
                        PluginPlaceholder.replacePlaceholder(text, placeholder)
                )
        );
    }

    public void error(final String text, final Throwable throwable) {
        console.error(
                LegacySerialize.serialize(text),
                throwable
        );
    }

    public void error(final String text, final PluginPlaceholder placeholder, final Throwable throwable) {
        console.error(
                LegacySerialize.serialize(
                        PluginPlaceholder.replacePlaceholder(text, placeholder)
                ),
                throwable
        );
    }

    public void debug(final String text) {
        if (debug) {
            console.info(
                    LegacySerialize.serialize(DEBUG_PREFIX.concat(text))
            );
        }
    }

    public void debug(final String text, final PluginPlaceholder placeholder) {
        if (debug) {
            console.info(
                    LegacySerialize.serialize(
                            PluginPlaceholder.replacePlaceholder(DEBUG_PREFIX.concat(text), placeholder)
                    )
            );
        }
    }


}
