package scriptservice.ports.events;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import scriptservice.ports.Main;

/**
 * event usage: Global
 * description: event manager (super class)
 */
public abstract class eventManager implements Listener {
    protected final Main plugin;

    public eventManager(Main plugin) {
        this.plugin = plugin;
    }

    public abstract void init(PluginManager pluginManager);
}
