package scriptservice.ports.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import scriptservice.ports.Main;
import scriptservice.ports.utils.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * event usage: Windcharge
 * description: if projectile is a snowball and the shooter has the windcharge item in hand, do the launch logic
 */
public class projectileLaunchEvent extends eventManager {
    public projectileLaunchEvent(Main plugin) {
        super(plugin);
    }

    // init stuff
    private apolloUtils apolloUtils;
    private windchargeUtils windchargeUtils;

    private int windchargeCooldown;
    private int windchargeActivateFallDamageAfterSeconds;
    private boolean debugNonLCmessage;

    @Override
    public void init(PluginManager pluginManager) {
        this.apolloUtils = plugin.apolloUtils;
        this.windchargeUtils = plugin.windchargeUtils;

        windchargeCooldown = (int) plugin.windchargeConfig.get("windchargeCooldown");
        windchargeActivateFallDamageAfterSeconds = (int) plugin.windchargeConfig.get("activateFallDamageAfterSeconds");
        debugNonLCmessage = (boolean) plugin.debugConfig.get("nonLCmessage");

        pluginManager.registerEvents(this, plugin); // register
    }

    @EventHandler
    public void onEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball ball = (Snowball) event.getEntity();

            if (ball.getShooter() instanceof Player) {
                Player player = (Player) ball.getShooter();

                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getType() == Material.AIR) {return;}

                ItemMeta itemMeta = itemInHand.getItemMeta();
                if (!itemMeta.hasDisplayName()) {return;}
                if (!itemMeta.getDisplayName().equals(windchargeUtils.getItemName())) {return;}

                if (windchargeUtils.cooldownList.contains(player)) {
                    // si le joueur est sur lunar, il a le cooldown (normallement)
                    if (!(apolloUtils.isUsingLC(player))) {
                        player.sendMessage(windchargeUtils.getErrorPrefix() + "L'item est sous cooldown.");
                    }

                    event.setCancelled(true);

                    // car askip, event.setCancelled ne cancel PAS le -1 du snowball (l'arnaque..) (de plus, faut pas le give pour les joueurs en crea)
                    if (!(player.getGameMode() == GameMode.CREATIVE)) {
                        windchargeUtils.giveItem(player, 1);
                    }
                    return;
                } else {
                    windchargeUtils.cooldownList.add(player);
                    apolloUtils.showWindChargeCooldown(player);
                    apolloUtils.showNoFallDuration(player);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (windchargeUtils.cooldownList.contains(player)) {
                                windchargeUtils.cooldownList.remove(player);

                                if (!apolloUtils.isUsingLC(player) && debugNonLCmessage) {
                                    player.sendMessage(windchargeUtils.getInfoPrefix()+"Vous pouvez de nouveau utiliser une windcharge.");
                                }
                            }

                        }
                    },(windchargeCooldown * 1000L));
                }

                ball.setCustomName(windchargeUtils.getItemName());
                ball.setCustomNameVisible(false);

                if (!windchargeUtils.noDamageList.contains(player)) {
                    windchargeUtils.noDamageList.add(player); // les autres vont se faire foutre <3

                    // cancel l'ancinne task
                    if (windchargeUtils.timerTasks.containsKey(player)) {
                        TimerTask oldTask = windchargeUtils.timerTasks.get(player);
                        windchargeUtils.timerTasks.remove(player);
                        oldTask.cancel();
                    }
                    apolloUtils.removeNoFallDuration(player);

                    // creation d'une newTask, et on l'utilise
                    TimerTask newTask = windchargeUtils.createFallDamageTask(player);
                    windchargeUtils.timerTasks.put(player, newTask);
                    new Timer().schedule(newTask, (windchargeActivateFallDamageAfterSeconds * 1000L));

                    // lunar client visuals
                    apolloUtils.showNoFallDuration(player);
                }
            }
        }

    }
}
