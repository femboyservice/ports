package scriptservice.ports.windcharge.events;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import scriptservice.ports.Main;

import java.lang.reflect.Parameter;
import java.util.*;

public class windchargeEvent implements Listener {
    private final Main main;
    public windchargeEvent(Main main) {this.main = main;}

    private TimerTask createFallDamageTask(Player player) {
        return new TimerTask() {
            @Override
            public void run() {
                if (main.windchargeUtils.noDamageList.contains(player)) {
                    main.windchargeUtils.noDamageList.remove(player);
                    main.apolloUtils.removeNoFallDuration(player);

                    if (main.windchargeUtils.infoMessage) {
                        player.sendMessage(main.windchargeUtils.infoPrefix + "Vous redevenez sensible au dégats de chute.");
                    }
                }
            }
        };
    }

    @EventHandler
    public void throwEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball ball = (Snowball) event.getEntity();

            if (ball.getShooter() instanceof Player) {
                Player player = (Player) ball.getShooter();

                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getType() == Material.AIR) {return;}

                ItemMeta itemMeta = itemInHand.getItemMeta();
                if (!itemMeta.hasDisplayName()) {return;}
                if (!itemMeta.getDisplayName().equals(main.windchargeUtils.itemName)) {return;}

                if (main.windchargeUtils.cooldownList.contains(player)) {
                    player.sendMessage(main.windchargeUtils.errorPrefix+"L'item est sous cooldown.");
                    event.setCancelled(true);

                    // car askip, event.setCancelled ne cancel PAS le -1 du snowball (l'arnaque..)
                    main.windchargeUtils.giveItem(player, 1);
                    return;
                } else {
                    main.windchargeUtils.cooldownList.add(player);
                    main.apolloUtils.showWindChargeCooldown(player);
                    main.apolloUtils.showNoFallDuration(player);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (main.windchargeUtils.cooldownList.contains(player)) {
                                main.windchargeUtils.cooldownList.remove(player);

                                if (main.windchargeUtils.infoMessage) {
                                    player.sendMessage(main.windchargeUtils.infoPrefix+"Vous pouvez de nouveau utiliser une windcharge.");
                                }
                            }

                        }
                    },(main.windchargeUtils.windchargeCooldown * 1000L));
                }

                ball.setCustomName(main.windchargeUtils.itemName);
                ball.setCustomNameVisible(false);

                if (!main.windchargeUtils.noDamageList.contains(player)) {
                    main.windchargeUtils.noDamageList.add(player); // les autres vont se faire foutre <3

                    if (main.windchargeUtils.timerTasks.containsKey(player)) {
                        // cancel old task
                        TimerTask oldTask = main.windchargeUtils.timerTasks.get(player);
                        main.windchargeUtils.timerTasks.remove(player);
                        oldTask.cancel();
                    }
                    main.apolloUtils.removeNoFallDuration(player);

                    // create newTask, make it the newest task & schedule it
                    TimerTask newTask = createFallDamageTask(player);
                    main.windchargeUtils.timerTasks.put(player, newTask);
                    new Timer().schedule(newTask, (main.windchargeUtils.activateFallDamageAfterSeconds * 1000L));

                    // lunar client visuals
                    main.apolloUtils.showNoFallDuration(player);
                }
            }
        }
    }

    @EventHandler
    public void hitEvent(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball ball = (Snowball) event.getEntity();

            if (ball.getShooter() instanceof Player) {
                if (ball.getCustomName() == null) {return;}
                if (!ball.getCustomName().equals(main.windchargeUtils.itemName)) {return;}

                Location actualLocation = ball.getLocation();
                Location appliedLocation = new Location(ball.getWorld(), actualLocation.getX(), (actualLocation.getY() - 1), actualLocation.getZ());
                World world = ball.getWorld();

                if (main.windchargeUtils.playSound) {
                    world.playSound(actualLocation, Sound.STEP_SNOW, 1.0f, 1.0f);
                }
                if (main.windchargeUtils.playParticle) {
                    world.playEffect(actualLocation, Effect.EXPLOSION_LARGE, 0);
                }

                for (Entity entity : world.getNearbyEntities(appliedLocation, main.windchargeUtils.radius, main.windchargeUtils.radius, main.windchargeUtils.radius)) { // c'est nul hein? ^^
                    if (!(entity instanceof Player)) {continue;}

                    Player player = (Player) entity;
                    Vector knockback = player.getLocation().toVector().subtract(appliedLocation.toVector());

                    if (knockback.length() == 0) {continue;}

                    double distance = knockback.length();
                    knockback.normalize();

                    double strength = main.windchargeUtils.power * (1.0 - (distance / main.windchargeUtils.radius));
                    knockback.multiply(strength);

                    // c'est du vertical mvmnt, pas du horizontal
                    knockback.setX(knockback.getX() * main.windchargeUtils.reduceX);
                    knockback.setZ(knockback.getZ() * main.windchargeUtils.reduceZ);

                    player.setVelocity(knockback);
                }
            }
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {return;}
            if (!main.windchargeUtils.noDamageList.contains(entity)) {return;}

            event.setCancelled(true);
            main.windchargeUtils.noDamageList.remove(entity);
            main.apolloUtils.removeNoFallDuration(player);
        }
    }
}
