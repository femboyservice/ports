package scriptservice.ports.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import scriptservice.ports.Main;

/**
 * command usage: Global
 * description: command manager (super class)
 */
public abstract class commandManager implements CommandExecutor, TabCompleter {
    protected final Main plugin;
    protected final String commandName;

    public commandManager(Main plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
    }

    public abstract void init();
}
