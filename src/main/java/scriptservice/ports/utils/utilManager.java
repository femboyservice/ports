package scriptservice.ports.utils;

import scriptservice.ports.Main;

/**
 * event usage: Global
 * description: util manager (super class)
 */
public abstract class utilManager {
    protected final Main plugin;

    public utilManager(Main plugin) {
        this.plugin = plugin;
    }

    public abstract void init();
}
