package scriptservice.ports.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.plugin.PluginManager;
import scriptservice.ports.Main;
import scriptservice.ports.utils.*;

/**
 * event usage: Mace & Windcharge
 * description: if damageCause is fallDamage, remove player if he's in mace or windcharge damagemodifiers lists and change damage done
 */
public class entityDamageEvent extends eventManager {
    public entityDamageEvent(Main plugin) {
        super(plugin);
    }

    // init stuff
    private apolloUtils apolloUtils;
    private maceUtils maceUtils;
    private windchargeUtils windchargeUtils;

    @Override
    public void init(PluginManager pluginManager) {
        this.apolloUtils = plugin.apolloUtils;
        this.maceUtils = plugin.maceUtils;
        this.windchargeUtils = plugin.windchargeUtils;

        pluginManager.registerEvents(this, plugin); // registert
    }

    @EventHandler
    public void onEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {return;}

            // mace
            if (maceUtils.fallDamageReductionList.contains(entity)) {
                // event stuff
                event.setDamage(event.getDamage() * 0.05);
                maceUtils.fallDamageReductionList.remove(entity);

                // lc stuff
                apolloUtils.removeFallDamageReduction(player);
            }

            // windcharge
            if (windchargeUtils.noDamageList.contains(entity)) {
                // event stuff
                event.setCancelled(true);
                windchargeUtils.noDamageList.remove(entity);

                // lc stuff
                apolloUtils.removeNoFallDuration(player);
            }
        }
    }
}
