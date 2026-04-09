package scriptservice.ports.events;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import scriptservice.ports.Main;
import scriptservice.ports.utils.*;

/**
 * event usage: Windcharge
 * description: if projectile is a windcharge, do the hit logic (=velocity stuff)
 */
public class projectileHitEvent extends eventManager {
    public projectileHitEvent(Main plugin) {
        super(plugin);
    }

    // init stuff
    private windchargeUtils windchargeUtils;
    private playerUtils playerUtils;

    private double windchargeRadius;
    private double windchargePower;
    private double windchargeReduceX;
    private double windchargeReduceZ;

    @Override
    public void init(PluginManager pluginManager) {
        this.windchargeUtils = plugin.windchargeUtils;
        this.playerUtils = plugin.playerUtils;

        windchargeRadius = (double) plugin.windchargeConfig.get("radius");
        windchargePower = (double) plugin.windchargeConfig.get("power");
        windchargeReduceX = (double) plugin.windchargeConfig.get("reduceX");
        windchargeReduceZ = (double) plugin.windchargeConfig.get("reduceZ");

        pluginManager.registerEvents(this, plugin); // register
    }

    @EventHandler
    public void onEvent(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball ball = (Snowball) event.getEntity();

            if (ball.getShooter() instanceof Player) {
                if (ball.getCustomName() == null) {return;}
                if (!ball.getCustomName().equals(windchargeUtils.getItemName())) {return;}

                // event consts
                Location actualLocation = ball.getLocation();
                Location appliedLocation = new Location(ball.getWorld(), actualLocation.getX(), (actualLocation.getY() - 1), actualLocation.getZ());
                World world = ball.getWorld();

                // les particules
                world.playEffect(actualLocation, Effect.EXPLOSION_LARGE, 0);

                // le kb + le son
                for (Entity entity : world.getNearbyEntities(appliedLocation, windchargeRadius, windchargeRadius, windchargeRadius)) { // c'est nul hein? ^^
                    if (!(entity instanceof Player)) {continue;}

                    Player player = (Player) entity;
                    Vector knockback = player.getLocation().toVector().subtract(appliedLocation.toVector());

                    if (knockback.length() == 0) {continue;}

                    double distance = knockback.length();
                    knockback.normalize();

                    double strength = windchargePower * (1.0 - (distance / windchargeRadius));
                    knockback.multiply(strength);

                    // c'est du vertical mvmnt, pas du horizontal
                    knockback.setX(knockback.getX() * windchargeReduceX);
                    knockback.setZ(knockback.getZ() * windchargeReduceZ);

                    player.setVelocity(knockback);

                    // le son
                    if (ball.getShooter() instanceof Player) {
                        playerUtils.playSound(player, "minecraft:ports.windcharge.burst1", 1f, 1.2f);
                    }
                }
            }
        }

    }
}
