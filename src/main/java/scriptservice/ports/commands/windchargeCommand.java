package scriptservice.ports.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import scriptservice.ports.Main;
import scriptservice.ports.utils.stringUtils;
import scriptservice.ports.utils.windchargeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * command usage: Windcharge
 * description: windcharge command manager
 */
public class windchargeCommand extends commandManager {
    public windchargeCommand(Main plugin, String commandName) {
        super(plugin, commandName);
    }

    // init stuff
    private stringUtils stringUtils;
    private windchargeUtils windchargeUtils;

    @Override
    public void init() {
        this.stringUtils = plugin.stringUtils;
        this.windchargeUtils = plugin.windchargeUtils;

        plugin.getCommand(commandName).setExecutor(this);
        plugin.getCommand(commandName).setTabCompleter(this);
    }

    // command executor
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {return true;}
        Player player = (Player) commandSender;

        // non-op
        if (!player.isOp()) {
            player.sendMessage(new String[]{
                    windchargeUtils.getInfoPrefix() + ("Sous-Commandes "+ChatColor.DARK_GRAY+"(/") + (ChatColor.GRAY+"windcharge ...") + (ChatColor.DARK_GRAY+")"),
                    stringUtils.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.RED, "Aucune commande disponible."),
            });
            return true;
        }

        if (strings.length == 0) {
            player.sendMessage(new String[]{
                    windchargeUtils.getInfoPrefix() + ("Sous-Commandes "+ChatColor.DARK_GRAY+"(/") + (ChatColor.GRAY+"windcharge ...") + (ChatColor.DARK_GRAY+")"),
                    stringUtils.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.GOLD, "give"),
            });
        } else if (strings.length == 1) {
            windchargeUtils.giveItem(player, 16);
        } else {
            if (strings.length > 3) {
                commandSender.sendMessage(windchargeUtils.getErrorPrefix() + "Usage: /windcharge give <pseudo> <OPTIONAL number>");
                return true;
            }

            // get target
            Player target = Bukkit.getPlayer(strings[1]);
            if (target == null) {
                commandSender.sendMessage(windchargeUtils.getErrorPrefix() + "Player " + ChatColor.AQUA + strings[1] + ChatColor.RED + " not found.");
                return true;
            }

            // get given ammount
            int givenAmmount = 1;
            if (strings.length == 3) {
                try {
                    givenAmmount = Integer.parseInt(strings[2]);
                    givenAmmount = Math.min(givenAmmount, 16);
                    if (givenAmmount < 0) {
                        givenAmmount = 0;
                    }
                } catch (Exception ignored) {
                    // not integer
                    commandSender.sendMessage(windchargeUtils.getErrorPrefix() + (ChatColor.AQUA + strings[2]) + (ChatColor.RED + " is not a valid number."));
                    return true;
                }
            }

            // give it
            if (givenAmmount != 0) {
                windchargeUtils.giveItem(target, givenAmmount);
                player.sendMessage(windchargeUtils.getInfoPrefix() + ("Given " + windchargeUtils.getItemName()) + (ChatColor.YELLOW + " " + givenAmmount + " ") + (ChatColor.WHITE + "to " + ChatColor.AQUA + player.getDisplayName()) + (ChatColor.WHITE + "."));
            } else {
                commandSender.sendMessage(windchargeUtils.getErrorPrefix() + "Ammount cannot be a " + (ChatColor.AQUA + "negative ") + (ChatColor.RED + "number."));
            }
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

        return null;
    }
}
