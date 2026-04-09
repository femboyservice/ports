package scriptservice.ports;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import scriptservice.ports.commands.*;
import scriptservice.ports.events.*;
import scriptservice.ports.utils.*;

public final class Main extends JavaPlugin {
    //--// definition
    // config stuff
    private final FileConfiguration config = getConfig();
    public final MemorySection debugConfig = (MemorySection) config.get("debug");
    public final MemorySection maceConfig = (MemorySection) config.get("mace");
    public final MemorySection windchargeConfig = (MemorySection) config.get("windcharge");

    // utils
    public stringUtils stringUtils;
    public apolloUtils apolloUtils;
    public windchargeUtils windchargeUtils;
    public maceUtils maceUtils;
    public playerUtils playerUtils;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = getServer().getPluginManager();

        //--// create
        // events
        final playerJoinEvent playerJoinEvent = new playerJoinEvent(this);
        final entityDamageEvent entityDamageEvent = new entityDamageEvent(this);
        final entityDamageByEntityEvent entityDamageByEntityEvent = new entityDamageByEntityEvent(this);
        final projectileHitEvent projectileHitEvent = new projectileHitEvent(this);
        final projectileLaunchEvent projectileLaunchEvent = new projectileLaunchEvent(this);
        // utils
        stringUtils = new stringUtils(this);
        apolloUtils = new apolloUtils(this);
        windchargeUtils = new windchargeUtils(this);
        maceUtils = new maceUtils(this);
        playerUtils = new playerUtils(this);
        // commands
        final maceCommand maceCommand = new maceCommand(this, "mace");
        final windchargeCommand windchargeCommand = new windchargeCommand(this, "windcharge");

        //--// inits
        // events
        final eventManager[] eventManagers = new eventManager[]{playerJoinEvent, entityDamageEvent, entityDamageByEntityEvent, projectileHitEvent, projectileLaunchEvent};
        for (eventManager event : eventManagers) {
            event.init(pluginManager); // init
        }

        // utils
        final utilManager[] utilManagers = new utilManager[]{stringUtils, apolloUtils, windchargeUtils, maceUtils, playerUtils};
        for (utilManager util : utilManagers) {
            util.init(); // init
        }

        // commands
        final commandManager[] commandManagers = new commandManager[]{maceCommand, windchargeCommand};
        for (commandManager command : commandManagers) {
            command.init(); // init
        }

        // yaml config
        config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {}
}
