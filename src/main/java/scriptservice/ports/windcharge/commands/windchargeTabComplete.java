package scriptservice.ports.windcharge.commands;

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

public class windchargeTabComplete implements TabCompleter {
    private final Main main;
    public windchargeTabComplete(Main main) {this.main = main;}

    // the first setting (give/settings)
    private static final ArrayList<String> commandsOne = new ArrayList<>(); {
        commandsOne.add("give");
        commandsOne.add("settings");
    }

    // the second setting (give :: null? // settings :: all available setting)
    private static final ArrayList<String> commandsTwo = new ArrayList<>(); {
        // ints
        commandsTwo.add("activateFallDamageAfterSeconds");
        commandsTwo.add("windchargeCooldown");

        // doubles
        commandsTwo.add("radius");
        commandsTwo.add("power");
        commandsTwo.add("reduceX");
        commandsTwo.add("reduceZ");

        // booleans
        commandsTwo.add("playSound");
        commandsTwo.add("playParticle");
        commandsTwo.add("opDebugMessage");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commandsOne, completions);
        } else if (args.length == 2) {
            String subCommand = args[0];
            if (subCommand.equals("settings")) {
                StringUtil.copyPartialMatches(args[1], commandsTwo, completions);
            } else if (subCommand.equals("give")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                ArrayList<String> commandsThree = new ArrayList<>();

                for (Player player : players) {
                    if (player.isOnline() && player.isOp()) {
                        commandsThree.add(player.getName());
                    }
                }

                StringUtil.copyPartialMatches(args[1], commandsThree, completions);
            }
        } else if (args.length == 3) {
            String subCommand = args[0];

            if (subCommand.equals("settings")) {
                String setting = args[1];

                switch (setting) {
                    // ints
                    case "activateFallDamageAfterSeconds":
                        completions.add(Integer.toString((main.windchargeUtils.defaultActivateFallDamageAfterSeconds)));
                        break;
                    case "windchargeCooldown":
                        completions.add(Integer.toString((main.windchargeUtils.defaultWindchargeCooldown)));
                        break;

                    // doubles
                    case "radius":
                        completions.add(Double.toString((main.windchargeUtils.defaultRadius)));
                        break;
                    case "power":
                        completions.add(Double.toString((main.windchargeUtils.defaultPower)));
                        break;
                    case "reduceX":
                        completions.add(Double.toString((main.windchargeUtils.defaultReduceX)));
                        break;
                    case "reduceZ":
                        completions.add(Double.toString((main.windchargeUtils.defaultReduceZ)));
                        break;

                    // booleans
                    case "playSound":
                        completions.add(Boolean.toString((main.windchargeUtils.defaultPlaySound)));
                        break;
                    case "playParticle":
                        completions.add(Boolean.toString((main.windchargeUtils.defaultPlayParticle)));
                        break;
                    case "opDebugMessage":
                        completions.add(Boolean.toString((main.windchargeUtils.defaultOpDebugMessage)));
                        break;
                }
            }
        }

        return completions;
    }
}
