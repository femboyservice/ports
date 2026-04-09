package scriptservice.ports.utils;

import org.bukkit.ChatColor;

import scriptservice.ports.Main;

public class stringUtils extends utilManager {
    public stringUtils(Main plugin) {
        super(plugin);
    }

    // overrides
    @Override
    public void init() {}

    // per-class
    public final String formatGreaterArrow(ChatColor color, ChatColor textColor, String text) {
        return ((color+"> ") + (textColor+text) + ChatColor.RESET);
    }

    public final String formatBracket(ChatColor bracketColor, ChatColor textColor, String text) {
        return ((bracketColor+"{") + (textColor+text) + (bracketColor+"}") + ChatColor.RESET);
    }
}
