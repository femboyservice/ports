package scriptservice.ports;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import scriptservice.ports.mace.commands.maceCommand;
import scriptservice.ports.mace.commands.maceTabComplete;
import scriptservice.ports.mace.events.maceEvent;
import scriptservice.ports.mace.maceUtils;
import scriptservice.ports.windcharge.commands.windchargeCommand;
import scriptservice.ports.windcharge.commands.windchargeTabComplete;
import scriptservice.ports.windcharge.events.windchargeEvent;

import scriptservice.ports.utils.apolloUtils;
import scriptservice.ports.windcharge.windchargeUtils;

public final class Main extends JavaPlugin {
    // fonctions
    public String formatGreaterArrow(ChatColor color, ChatColor textColor, String text) {
        return (color+"> "+textColor+text+ChatColor.RESET);
    }

    // utils
    public apolloUtils apolloUtils;
    public windchargeUtils windchargeUtils;
    public maceUtils maceUtils;

    @Override
    public void onEnable() {
        // Plugin startup logic
        apolloUtils = new apolloUtils(this);
        windchargeUtils = new windchargeUtils();
        maceUtils = new maceUtils();

        getCommand("windcharge").setExecutor(new windchargeCommand(this));
        getCommand("windcharge").setTabCompleter(new windchargeTabComplete(this));
        getServer().getPluginManager().registerEvents(new windchargeEvent(this), this);

        getCommand("mace").setExecutor(new maceCommand(this));
        getCommand("mace").setTabCompleter(new maceTabComplete(this));
        getServer().getPluginManager().registerEvents(new maceEvent(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
