package scriptservice.ports.windcharge;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import scriptservice.ports.utils.ItemUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class windchargeUtils {
    // fonctions
    public String formatBracket(ChatColor color, String text) {
        return (color+"{"+text+color+"}"+ChatColor.RESET);
    }
    public String formatGreaterArrow(ChatColor color, ChatColor textColor, String text) {
        return (color+"> "+textColor+text+ChatColor.RESET);
    }

    // strings
    public final String itemName = ("" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Wind Charge");

    public final String errorPrefix = (formatBracket(ChatColor.DARK_GRAY, (ChatColor.DARK_RED+"Windcharge")) + ChatColor.RED + " ");
    public final String validPrefix = (formatBracket(ChatColor.DARK_GRAY, (ChatColor.DARK_GREEN+"Windcharge")) + ChatColor.WHITE + " ");
    public final String infoPrefix = (formatBracket(ChatColor.DARK_GRAY, (ChatColor.DARK_AQUA+"Windcharge")) + ChatColor.WHITE + " ");

    public final String enchantmentColor = "#FF55FF";
    // lists
    public Set<Entity> cooldownList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public Set<Entity> noDamageList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public HashMap<Entity, TimerTask> timerTasks = new HashMap<>();

    // const
    public double defaultRadius = 5.0;
    public double defaultPower = 1.5;
    public double defaultReduceX = 0.8;
    public double defaultReduceZ = 0.8;
    public boolean defaultPlaySound = false;
    public boolean defaultPlayParticle = true;
    public boolean defaultOpDebugMessage = false;
    public boolean defaultInfoMessage = false;
    public int defaultActivateFallDamageAfterSeconds = 4;
    public int defaultWindchargeCooldown = 2;

    // vars
    public double radius = defaultRadius;
    public double power = defaultPower;
    public double reduceX = defaultReduceX;
    public double reduceZ = defaultReduceZ;
    public boolean playSound = defaultPlaySound;
    public boolean playParticle = defaultPlayParticle;
    public boolean opDebugMessage = defaultOpDebugMessage;
    public boolean infoMessage = defaultInfoMessage;
    public int activateFallDamageAfterSeconds = defaultActivateFallDamageAfterSeconds;
    public int windchargeCooldown = defaultWindchargeCooldown;

    // after functions
    public void giveItem(Player player, int amount) {
        ItemStack newBall = new ItemStack(Material.SNOW_BALL, amount);
        ItemMeta meta = newBall.getItemMeta();

        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setDisplayName(itemName);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        newBall.setItemMeta(meta);

        player.getInventory().addItem(ItemUtil.addTag(newBall, "glint", enchantmentColor));
    }
}
