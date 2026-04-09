package scriptservice.ports.events;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import scriptservice.ports.Main;
import scriptservice.ports.utils.*;
import scriptservice.ports.utils.web.ActionBarUtil;
import scriptservice.ports.utils.web.NBTEditor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * event usage: Mace
 * description: checks if entity was hit by an item with the mace name, if so, deal mace damage and logic.(
 */
public class entityDamageByEntityEvent extends eventManager {
    public entityDamageByEntityEvent(Main plugin) {
        super(plugin);
    }

    // init stuff
    private apolloUtils apolloUtils;
    private maceUtils maceUtils;
    private playerUtils playerUtils;

    private double maceMinY;
    private double maceMinDamage;
    private int maceMinBigGroundDamage;
    private int maceFallDamageProtection;

    @Override
    public void init(PluginManager pluginManager) {
        this.apolloUtils = plugin.apolloUtils;
        this.maceUtils = plugin.maceUtils;
        this.playerUtils = plugin.playerUtils;

        maceMinY = (double) plugin.maceConfig.get("minY");
        maceMinDamage = (double) plugin.maceConfig.get("minDamage");
        maceMinBigGroundDamage = (int) plugin.maceConfig.get("minBigGroundDamage");
        maceFallDamageProtection = (int) plugin.maceConfig.get("fallDamageProtection");

        pluginManager.registerEvents(this, plugin); // register
    }

    @EventHandler
    public void onEvent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Player) {
            Player player = (Player) damager;

            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand.getType() == Material.AIR) {return;}

            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (!itemMeta.hasDisplayName()) {return;}
            if (!itemMeta.getDisplayName().equals(maceUtils.getItemName())) {return;}

            double yVelocity = player.getVelocity().getY();
            boolean isMaceHit = ((Math.abs(yVelocity) >= maceMinY) && (yVelocity < 0));

            if (isMaceHit) {
                // event related consts
                Entity target = event.getEntity();
                boolean isTargetPlayer = (target instanceof Player);
                double damage = event.getDamage();

                // vars
                int windburstLevel = 0;
                double drawbackYVelocity = 0.15;

                // get vals from windburst enchant
                if (NBTEditor.contains(itemInHand, NBTEditor.CUSTOM_DATA, "Windburst")) {
                    windburstLevel = NBTEditor.getInt(itemInHand, NBTEditor.CUSTOM_DATA, "Windburst");
                    drawbackYVelocity += 1.15 + (0.35 * windburstLevel);
                }

                // set the damage
                double maceDamage = (damage*(Math.abs(yVelocity) + maceMinDamage));
                boolean isBigGroundHit = (maceDamage >= maceMinBigGroundDamage);
                event.setDamage(maceDamage);

                //--// velocity, fall damage and apollo related (actually important)
                // add player to falldamagereduction
                maceUtils.fallDamageReductionList.add(damager);

                // set the new velocity
                player.setVelocity(new Vector(player.getVelocity().getX(), drawbackYVelocity, player.getVelocity().getZ()));

                // cancel old task
                if (maceUtils.timerTasks.containsKey(player)) {
                    TimerTask oldTask = maceUtils.timerTasks.get(player);
                    maceUtils.timerTasks.remove(player);
                    oldTask.cancel();
                }

                // remove lc cooldown
                apolloUtils.removeFallDamageReduction(player);

                // create newTask, make it the newest task & schedule it
                TimerTask fallDamageProtectionTask = maceUtils.createFallDamageTask(player);
                maceUtils.timerTasks.put(player, fallDamageProtectionTask);
                new Timer().schedule(fallDamageProtectionTask, (maceFallDamageProtection * 1000L));

                // show lc cooldown
                apolloUtils.showFallDamageReduction(player);

                // SAFETY :: re-add player to falldmgreduction
                // -> bon, faut voire mais ya des cas ou on prend les degats de chute PUIS on est envoyé en l'air, et on a pas la reduction, donc bon :^)
                if (!maceUtils.fallDamageReductionList.contains(damager)) {
                    maceUtils.fallDamageReductionList.add(damager);
                }



                //--// visual stuff (not important)
                // send actionbar stating damage
                String actionText = String.valueOf(maceDamage);
                if (actionText.contains(".")) {
                    String[] strings = actionText.split("\\.");
                    if (strings.length >= 2) {
                        actionText = (strings[0] + "." + strings[1].substring(0, 2)); // gets int.xx
                    } else {
                        actionText = actionText.substring(0, 5); // fallback
                    }
                } else {
                    actionText = actionText.substring(0, 5); // fallback
                }

                ChatColor actionColor = ChatColor.YELLOW;
                if (maceDamage >= maceMinBigGroundDamage * 2) {
                    actionColor = ChatColor.RED;
                } else if (isBigGroundHit) {
                    actionColor = ChatColor.GOLD;
                }

                ActionBarUtil.sendActionBarMessage(player, ((ChatColor.DARK_GRAY + "DMG: ") + actionColor + actionText), 1, plugin);

                // spawn hit particle
                if (target != null) {
                    boolean isTargetGrounded = target.isOnGround();

                    if (isTargetGrounded) {
                        EnumParticle particle = EnumParticle.CRIT;

                        // normal particles
                        maceUtils.spawnParticleCircle(target.getLocation(), 1.5, particle);
                        maceUtils.spawnParticleCircle(target.getLocation(), 3, particle);

                        // upward particle
                        maceUtils.spawnParticleCircle(target.getLocation(), 3, particle, 0f, 1f, 0f, 0.5f, 10);
                    }
                }

                // play hit sounds -> /playsound minecraft:ports.mace.<sound> femboyservice
                if (target != null) {
                    boolean isTargetGrounded = target.isOnGround();

                    if (isTargetGrounded) {
                        if (isBigGroundHit) {
                            playerUtils.playSound(player, ("minecraft:ports.mace.biggroundhit"), 1f, 1f);
                            if (isTargetPlayer) {playerUtils.playSound((Player) target, ("minecraft:ports.mace.biggroundhit"), 1f, 1f);}
                        } else {
                            playerUtils.playSound(player, ("minecraft:ports.mace.groundhit"), 1f, 1f);
                            if (isTargetPlayer) {playerUtils.playSound((Player) target, ("minecraft:ports.mace.groundhit"), 1f, 1f);}
                        }
                    } else {
                        playerUtils.playSound(player, ("minecraft:ports.mace.airhit"), 1f, 1f);
                        if (isTargetPlayer) {playerUtils.playSound((Player) target, ("minecraft:ports.mace.airhit"), 1f, 1f);}
                    }
                }

                // play windburst sounds
                if (target != null) {
                    if (windburstLevel > 3) {
                        playerUtils.playSound(player, ("minecraft:ports.windcharge.burst3"), 1f, 125f);
                        if (isTargetPlayer) {playerUtils.playSound((Player) target, ("minecraft:ports.windcharge.burst3"), 1f, 125f);}
                    } else {
                        playerUtils.playSound(player, ("minecraft:ports.windcharge.burst" + windburstLevel), 1f, 125f);
                        if (isTargetPlayer) {playerUtils.playSound((Player) target, ("minecraft:ports.windcharge.burst" + windburstLevel), 1f, 125f);}
                    }
                }
            }
        }
    }
}
