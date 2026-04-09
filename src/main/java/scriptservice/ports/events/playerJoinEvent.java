package scriptservice.ports.events;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import scriptservice.ports.Main;
import scriptservice.ports.utils.*;

/**
 * event usage: Global
 * description: if player is not using lunar client, send message to warn about lunar client's cosmetic ability to show cooldowns (and plays a little sound)
 */
public class playerJoinEvent extends eventManager {
    public playerJoinEvent(Main plugin) {
        super(plugin);
    }

    // init stuff
    private apolloUtils apolloUtils;
    private stringUtils stringUtils;
    private playerUtils playerUtils;

    private boolean joinLCwarning;

    @Override
    public void init(PluginManager pluginManager) {
        this.apolloUtils = plugin.apolloUtils;
        this.stringUtils = plugin.stringUtils;
        this.playerUtils = plugin.playerUtils;

        joinLCwarning = (boolean) plugin.debugConfig.get("joinLCwarning");

        pluginManager.registerEvents(this, plugin); // register
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        if (!joinLCwarning) {return;}

        new BukkitRunnable() {
            @Override
            public void run() {
                final Player player = event.getPlayer();
                final boolean isUsingLC = apolloUtils.isUsingLC(player);

                if (!isUsingLC) {
                    player.sendMessage(
                            (stringUtils.formatBracket(ChatColor.DARK_GRAY, ChatColor.YELLOW, "ⓘ")) +
                               (ChatColor.WHITE + " This plugin uses ") +
                               (ChatColor.AQUA + "Lunar Client") +
                               (ChatColor.WHITE + " to show cooldowns.")
                    );

                    playerUtils.playSound(player, Sound.ITEM_BREAK, 1f, 1f);
                }
            }
        }.runTaskLater(plugin, 20);
    }
}
