package scriptservice.ports.mace.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import scriptservice.ports.Main;

public class maceCommand implements CommandExecutor {
    private final Main main;
    public maceCommand(Main main) {this.main = main;}

    private void _cmdlist(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.isOp()) {
                player.sendMessage(new String[]{
                        main.maceUtils.infoPrefix+"Sous-Commandes "+ChatColor.DARK_GRAY+"(/"+ChatColor.GRAY+"mace ..."+ChatColor.DARK_GRAY+")",
                        main.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.GOLD, "give")
                });
            } else {
                player.sendMessage(new String[]{
                        main.maceUtils.infoPrefix+"Sous-Commandes "+ChatColor.DARK_GRAY+"(/"+ChatColor.GRAY+"mace ..."+ChatColor.DARK_GRAY+")",
                        main.formatGreaterArrow(ChatColor.DARK_GRAY, ChatColor.RED, "Aucune commande disponible.")
                });
            }



        } else {
            System.out.println("[mace] '_cmdlist(sender)' sender has unhandled class: " + sender.getClass());
        }
    }

    private void _give(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player commandSender = (Player) sender;

            if (!commandSender.isOp()) {
                commandSender.sendMessage(main.maceUtils.errorPrefix+"Vous n'avez pas les permissions pour utiliser cette commande.");
            }

            if (args.length == 1) {
                main.maceUtils.giveItem(commandSender, 1, 0);
            } else {
                if (args.length > 3) {
                    commandSender.sendMessage(main.maceUtils.errorPrefix+"Usage: /mace give <pseudo> <OPTIONAL windburstLevel>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    commandSender.sendMessage(main.maceUtils.errorPrefix+"Player "+ChatColor.AQUA+args[1]+ChatColor.RED+ " doesnt exist or is not connected.");
                    return;
                }

                int windburstLevel = 0;
                if (args.length == 3) {
                    try {
                        windburstLevel = Integer.parseInt(args[2]);
                    } catch (Exception exception) {}
                }

                main.maceUtils.giveItem(target, 1, windburstLevel);
            }

        } else {
            System.out.println("[mace] '_give(sender)' sender has unhandled class: " + sender.getClass());
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
            }
        }

        return true;
    }
}
