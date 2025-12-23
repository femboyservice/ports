package scriptservice.ports.mace.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import scriptservice.ports.Main;

import java.util.ArrayList;

import java.util.Collection;
import java.util.List;

public class maceTabComplete implements TabCompleter {
    private final Main main;
    public maceTabComplete(Main main) {this.main = main;}

    // the first setting (give/settings)
    private static final ArrayList<String> commandsOne = new ArrayList<>(); {
        commandsOne.add("give");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commandsOne, completions);
        } else if (args.length == 2) {
            String subCommand = args[0];

            if (subCommand.equals("give")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                ArrayList<String> commandsTwo = new ArrayList<>();

                for (Player player : players) {
                    if (player.isOnline() && player.isOp()) {
                        commandsTwo.add(player.getName());
                    }
                }

                StringUtil.copyPartialMatches(args[1], commandsTwo, completions);
            }
        }

        return completions;
    }
}
