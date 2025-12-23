package scriptservice.ports.mace.events;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import scriptservice.ports.Main;
import scriptservice.ports.utils.NBTEditor;

import java.util.Timer;
import java.util.TimerTask;

public class maceEvent implements Listener {
    private final Main main;
    public maceEvent(Main main) {this.main = main;}

    private TimerTask createFallDamageTask(Player player) {
        return new TimerTask() {
            @Override
            public void run() {
                if (main.maceUtils.fallDamageReductionList.contains(player)) {
                    main.maceUtils.fallDamageReductionList.remove(player);
                    main.apolloUtils.removeNoFallDuration(player);

                    if (main.maceUtils.infoMessage) {
                        player.sendMessage(main.maceUtils.infoPrefix + "Vous redevenez sensible au dégats de chute.");
                    }
                }
            }
        };
    }

    private void spawnParticleCircle(Location location, double radius, EnumParticle particle) {
        World world = location.getWorld();

        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            // ptn vous savez que le cercle trigo c'est utile pour faire des cercles ?
            float x = (float) (location.getX() + radius * Math.cos(angle));
            float y = (float) location.getY();
            float z = (float) (location.getZ() + radius * Math.sin(angle));

            // totale decouverte, askip c'est mieux :)
            PacketPlayOutWorldParticles packet =
                    new PacketPlayOutWorldParticles(
                            particle,
                            false, // true -> visible jusqua 256blocks // false -> visible jusqua 32blocks
                            x, y, z, // particle location (x,y,z)
                            0f, 0f, 0f, // particle vector (x,y,z)
                            0f, // particle speed ?
                            1 // amount
                    );

            for (Player player : world.getPlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    private void spawnParticleCircle(Location location, double radius, EnumParticle particle, double xParticleVector, double yParticleVector, double zParticleVector, double particleSpeed, int particleAmount) {
        World world = location.getWorld();

        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            // ptn vous savez que le cercle trigo c'est utile pour faire des cercles ?
            float x = (float) (location.getX() + radius * Math.cos(angle));
            float y = (float) location.getY();
            float z = (float) (location.getZ() + radius * Math.sin(angle));

            float x2 = (float) xParticleVector;
            float y2 = (float) yParticleVector;
            float z2 = (float) zParticleVector;

            float speed = (float) particleSpeed;

            // totale decouverte, askip c'est mieux :)
            PacketPlayOutWorldParticles packet =
                    new PacketPlayOutWorldParticles(
                            particle,
                            false, // true -> visible jusqua 256blocks // false -> visible jusqua 32blocks
                            x, y, z, // particle location (x,y,z)
                            x2, y2, z2, // particle vector (x,y,z)
                            speed, // particle speed ?
                            particleAmount // amount
                    );

            for (Player player : world.getPlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Player) {
            Player player = (Player) damager;

            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand.getType() == Material.AIR) {return;}

            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (!itemMeta.hasDisplayName()) {return;}
            if (!itemMeta.getDisplayName().equals(main.maceUtils.itemName)) {return;}

            double yVelocity = player.getVelocity().getY();
            boolean isMaceHit = ((Math.abs(yVelocity) >= main.maceUtils.minY) && (yVelocity < 0));

            if (isMaceHit) {
                double drawbackYVelocity = 0.2;

                // set the damage
                double damage = event.getDamage();
                double maceDamage = (damage*(Math.abs(yVelocity) + main.maceUtils.minDamage));
                event.setDamage(maceDamage);

                // spawn particle
                Entity target = event.getEntity();
                if (target != null) {
                    EnumParticle particle = EnumParticle.CRIT;

                    // normal particles
                    spawnParticleCircle(target.getLocation(), 1.5, particle);
                    spawnParticleCircle(target.getLocation(), 3, particle);

                    // upward particle
                    spawnParticleCircle(target.getLocation(), 3, particle, 0, 1, 0, 0.5, 10);
                }




                // send infoMessage n°1
                if (main.maceUtils.infoMessage) {
                    player.sendMessage(main.maceUtils.validPrefix+"Degats de la mace: " + ChatColor.AQUA + Double.toString(maceDamage).substring(0, 4) + ChatColor.WHITE + ".");
                }

                // windburst effect
                if (NBTEditor.contains(itemInHand, NBTEditor.CUSTOM_DATA, "Windburst")) {
                    int windburstLevel = NBTEditor.getInt(itemInHand, NBTEditor.CUSTOM_DATA, "Windburst");
                    drawbackYVelocity += 1.15 + (0.35 * windburstLevel);

                    // send infoMessage n°2
                    if (main.maceUtils.infoMessage) {
                        player.sendMessage(main.maceUtils.infoPrefix+"windburstLevel: " + windburstLevel);
                    }
                }

                // add player to falldamagereduction
                main.maceUtils.fallDamageReductionList.add(damager);

                // set the new velocity
                player.setVelocity(new Vector(player.getVelocity().getX(), drawbackYVelocity, player.getVelocity().getZ()));

                // cancel old task
                if (main.maceUtils.timerTasks.containsKey(player)) {
                    TimerTask oldTask = main.maceUtils.timerTasks.get(player);
                    main.maceUtils.timerTasks.remove(player);
                    oldTask.cancel();
                }

                // remove lc cooldown
                main.apolloUtils.removeFallDamageReduction(player);

                // create newTask, make it the newest task & schedule it
                TimerTask fallDamageProtectionTask = createFallDamageTask(player);
                main.maceUtils.timerTasks.put(player, fallDamageProtectionTask);
                new Timer().schedule(fallDamageProtectionTask, (main.maceUtils.fallDamageProtection * 1000L));

                // show lc cooldown
                main.apolloUtils.showFallDamageReduction(player, main.maceUtils.fallDamageProtection, "ports:windburst.png");

                // SAFETY :: re-add player to falldmgreduction
                // -> bon, faut voire mais ya des cas ou on prend les degats de chute PUIS on est envoyé en l'air, et on a pas la reduction, donc bon :^)
                if (!main.maceUtils.fallDamageReductionList.contains(damager)) {
                    main.maceUtils.fallDamageReductionList.add(damager);
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
            if (!main.maceUtils.fallDamageReductionList.contains(entity)) {return;}

            event.setDamage(event.getDamage() * 0.05); // 95% damage reduction, feels aight na ?
            main.maceUtils.fallDamageReductionList.remove(entity);

            // remove lc cooldown
            main.apolloUtils.removeFallDamageReduction(player);
        }
    }
}
