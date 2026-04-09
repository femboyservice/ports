package scriptservice.ports.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import scriptservice.ports.Main;

public class playerUtils extends utilManager {
    public playerUtils(Main plugin) {
        super(plugin);
    }

    // overrides
    @Override
    public void init() {}

    // per-class
    public final void playSound(Player player, Sound soundPlayed, float volume, float pitch) {
        Bukkit.getScheduler().runTask(plugin, () -> player.playSound(player.getLocation(), soundPlayed, volume, pitch));
    }

    public final void playSound(Player player, String soundPlayed, float volume, float pitch) {
        Bukkit.getScheduler().runTask(plugin, () -> player.playSound(player.getLocation(), soundPlayed, volume, pitch));
    }
}
