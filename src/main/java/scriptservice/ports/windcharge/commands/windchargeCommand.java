package scriptservice.ports.windcharge.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import scriptservice.ports.Main;

public class windchargeCommand implements CommandExecutor {
    private final Main main;
    public windchargeCommand(Main main) {this.main = main;}

    private void _cmdlist(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.isOp()) {
                player.sendMessage(new String[]{
                        main.windchargeUtils.infoPrefix+"Sous-Commandes "+ChatColor.DARK_GRAY+"(/"+ChatColor.GRAY+"windcharge ..."+ChatColor.DARK_GRAY+")",
                        main.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.GOLD, "give"),
                        main.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.GOLD, "settings"),
                });
            } else {
                player.sendMessage(new String[]{
                        main.windchargeUtils.infoPrefix+"Sous-Commandes "+ChatColor.DARK_GRAY+"(/"+ChatColor.GRAY+"windcharge ..."+ChatColor.DARK_GRAY+")",
                        main.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.RED, "Aucune commande disponible."),
                });
            }



        } else {
            System.out.println("[windcharge] '_cmdlist(sender)' sender has unhandled class: " + sender.getClass());
        }
    }

    private void _give(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player commandSender = (Player) sender;

            if (args.length == 1) {
                if (commandSender.isOp()) {
                    main.windchargeUtils.giveItem(commandSender, 16);
                } else {
                    commandSender.sendMessage(main.windchargeUtils.errorPrefix+"Vous n'avez pas les permissions pour utiliser cette commande.");
                }

            } else {
                if (args.length != 2) {
                    commandSender.sendMessage(main.windchargeUtils.errorPrefix+"Usage: /windcharge give <pseudo>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    commandSender.sendMessage(main.windchargeUtils.errorPrefix+"Player "+ChatColor.AQUA+args[1]+ChatColor.RED+ " doesnt exist or is not connected.");
                    return;
                }

                if (commandSender.isOp()) {
                    main.windchargeUtils.giveItem(target, 16);
                } else {
                    commandSender.sendMessage(main.windchargeUtils.errorPrefix+"Vous n'avez pas les permissions pour utiliser cette commande.");
                }
            }

        } else {
            System.out.println("[windcharge] '_give(sender)' sender has unhandled class: " + sender.getClass());
        }
    }

    private void _settings(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.isOp()) {
                if (args.length != 3) {return;} // oui bon le message d'erreur il est dans ton Q :)

                String setting = args[1];
                String value = args[2];
                boolean cancel = true;
                Object valueChanged = null;

                switch (setting) {
                    // int
                    case "activateFallDamageAfterSeconds":
                        try {
                            main.windchargeUtils.activateFallDamageAfterSeconds = Integer.parseInt(value);
                            valueChanged = main.windchargeUtils.activateFallDamageAfterSeconds;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+ (ChatColor.AQUA + value + ChatColor.RED) + " n'est pas valide pour activateFallDamageAfterSeconds.");
                        }
                        break;
                    case "windchargeCooldown":
                        try {
                            main.windchargeUtils.windchargeCooldown = Integer.parseInt(value);
                            valueChanged = main.windchargeUtils.windchargeCooldown;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour activateFallDamageAfterSeconds.");
                        }
                        break;

                    // doubles
                    case "radius":
                        if (String.valueOf(value.charAt(0)).equals("0")) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"le radius ne peut pas etre egale a 0.");
                            return;
                        }

                        try {
                            main.windchargeUtils.radius = Double.parseDouble(value);
                            valueChanged = main.windchargeUtils.radius;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour radius.");
                        }
                        break;
                    case "power":
                        try {
                            main.windchargeUtils.power = Double.parseDouble(value);
                            valueChanged = main.windchargeUtils.power;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour power.");
                        }
                        break;
                    case "reduceX":
                        try {
                            main.windchargeUtils.reduceX = Double.parseDouble(value);
                            valueChanged = main.windchargeUtils.reduceX;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour reduceX.");
                        }
                        break;
                    case "reduceZ":
                        try {
                            main.windchargeUtils.reduceZ = Double.parseDouble(value);
                            valueChanged = main.windchargeUtils.reduceZ;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour reduceZ.");
                        }
                        break;

                    // booleans
                    case "playSound":
                        try {
                            main.windchargeUtils.playSound = Boolean.parseBoolean(value);
                            valueChanged = main.windchargeUtils.playSound;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour playSound.");
                        }
                        break;
                    case "playParticle":
                        try {
                            main.windchargeUtils.playParticle = Boolean.parseBoolean(value);
                            valueChanged = main.windchargeUtils.playParticle;
                            cancel = false;
                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour playParticle.");
                        }
                        break;
                    case "opDebugMessage":
                        try {
                            main.windchargeUtils.opDebugMessage = Boolean.parseBoolean(value);
                            valueChanged = main.windchargeUtils.opDebugMessage;
                            cancel = false;

                        } catch(Exception exception) {
                            player.sendMessage(main.windchargeUtils.errorPrefix+"'" + value + "' n'est pas valide pour opDebugMessage.");
                        }
                        break;
                    default:
                        player.sendMessage(main.windchargeUtils.errorPrefix+"'" + setting + "' n'est pas un parametre valide.");
                        break;
                }


                if (main.windchargeUtils.opDebugMessage && (!cancel && valueChanged != null)) {
                    player.sendMessage(main.windchargeUtils.validPrefix + ChatColor.AQUA + setting + ChatColor.WHITE +" a pour nouvelle valeur " + ChatColor.AQUA + valueChanged + ChatColor.WHITE + ".");
                }
            } else {
                player.sendMessage(main.windchargeUtils.errorPrefix+"Vous n'avez pas les permissions pour utiliser cette commande.");
            }

        } else {
            System.out.println("[windcharge] '_settings(sender)' sender has unhandled class: " + sender.getClass());
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            _cmdlist(commandSender, args);
        } else {
            String sousCommande = args[0];

            if (sousCommande.equalsIgnoreCase("give")) {
                _give(commandSender, args);
            } else if (sousCommande.equalsIgnoreCase("settings")) {
                _settings(commandSender, args);
            }
        }

        return true;
    }
}
