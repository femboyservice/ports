package scriptservice.ports.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.util.StringUtil;
import scriptservice.ports.Main;
import scriptservice.ports.utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * command usage: Mace
 * description: mace command manager
 */
public class maceCommand extends commandManager {
    public maceCommand(Main plugin, String commandName) {
        super(plugin, commandName);
    }

    // init stuff
    private stringUtils stringUtils;
    private maceUtils maceUtils;

    @Override
    public void init() {
        this.maceUtils = plugin.maceUtils;
        this.stringUtils = plugin.stringUtils;

        plugin.getCommand(commandName).setExecutor(this);
        plugin.getCommand(commandName).setTabCompleter(this);
    }

    // command executor
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {return true;}
        Player player = (Player) commandSender;

        if (!player.isOp()) {
            player.sendMessage(new String[]{
                    maceUtils.getInfoPrefix()+"Sous-Commandes "+ChatColor.DARK_GRAY+"(/"+ChatColor.GRAY+"mace ..."+ChatColor.DARK_GRAY+")",
                    stringUtils.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.RED, "Aucune commande disponible.")
            });
            return true;
        }

        if (strings.length == 0) {
            player.sendMessage(new String[]{
                    maceUtils.getInfoPrefix()+"Sous-Commandes "+ChatColor.DARK_GRAY+"(/"+ChatColor.GRAY+"mace ..."+ChatColor.DARK_GRAY+")",
                    stringUtils.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.GOLD, "give")
            });
        } else if (strings.length == 1) {
            maceUtils.giveItem(player, 1, 0);
            player.sendMessage(maceUtils.getInfoPrefix()+("Given " + maceUtils.getItemName()) + (ChatColor.AQUA + " 0 ") + ("to " + ChatColor.AQUA + player.getDisplayName()) + (ChatColor.WHITE + "."));
        } else {
            if (strings.length > 3) {
                player.sendMessage(maceUtils.getErrorPrefix() + "Usage: /mace give <player> <OPTIONAL windburstLevel>");
                return true;
            }

            // get target
            Player target = Bukkit.getPlayer(strings[1]);
            if (target == null) {
                player.sendMessage(maceUtils.getErrorPrefix() + "Player " + ChatColor.AQUA+strings[1]+ChatColor.RED + " not found.");
                return true;
            }

            // get windburst level
            int windburstLevel = 0;
            if (strings.length == 3) {
                try {
                    windburstLevel = Integer.parseInt(strings[2]);
                } catch (Exception ignored) {
                    commandSender.sendMessage(maceUtils.getErrorPrefix() + (ChatColor.AQUA + strings[2]) + (ChatColor.RED + " is not a valid number."));
                    return true;
                }
            }

            // give item
            maceUtils.giveItem(target, 1, windburstLevel);
            player.sendMessage(maceUtils.getInfoPrefix() + ("Given " + maceUtils.getItemName()) + (ChatColor.YELLOW + " " + windburstLevel + " ") + (ChatColor.WHITE + "to " + ChatColor.AQUA + player.getDisplayName()) + (ChatColor.WHITE + "."));
        }

        return true;
    }

    // tab completer
    private static final ArrayList<String> mainCommands = new ArrayList<>(); {
        mainCommands.add("give");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        final List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            StringUtil.copyPartialMatches(strings[0], mainCommands, completions);
        } else if (strings.length == 2) {
            String subCommand = strings[0];

            if (subCommand.equals("give")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                ArrayList<String> playersReturned = new ArrayList<>();

                for (Player player : players) {
                    if (player.isOnline() && player.isOp()) {
                        playersReturned.add(player.getName());
                    }
                }

                StringUtil.copyPartialMatches(strings[1], playersReturned, completions);
            }
        }

        return completions;
    }
}
