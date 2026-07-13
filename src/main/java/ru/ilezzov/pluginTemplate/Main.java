package ru.ilezzov.pluginTemplate;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Hello!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
