package scriptservice.ports.utils;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import scriptservice.ports.Main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class windchargeUtils extends utilManager {
    public windchargeUtils(Main plugin) {
        super(plugin);
    }

    // init stuff
    private apolloUtils apolloUtils;
    private boolean debugNonLCmessage;
    @Getter private String infoPrefix;
    @Getter private String errorPrefix;

    @Override
    public void init() {
        // overrides
        this.apolloUtils = plugin.apolloUtils;

        this.infoPrefix = (plugin.stringUtils.formatBracket(ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, "Windcharge") + " ");
        this.errorPrefix = (plugin.stringUtils.formatBracket(ChatColor.DARK_RED, ChatColor.RED, "Windcharge") + " " + ChatColor.RED);

        debugNonLCmessage = (boolean) plugin.debugConfig.get("nonLCmessage");
    }

    // per-class
    // lists
    public final Set<Entity> cooldownList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public final Set<Entity> noDamageList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public final HashMap<Entity, TimerTask> timerTasks = new HashMap<>();


    // strings
    @Getter private final String itemName = ("" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Wind Charge");

    // functions
    public final void giveItem(Player player, int amount) {
        // create new item
        ItemStack newWindcharge = new ItemStack(Material.SNOW_BALL, amount);
        ItemMeta meta = newWindcharge.getItemMeta();

        // set meta
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setDisplayName(itemName);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        newWindcharge.setItemMeta(meta);

        // give item
        player.getInventory().addItem(newWindcharge);
    }

    public final TimerTask createFallDamageTask(Player player) {
        return new TimerTask() {
            @Override
            public void run() {
                if (noDamageList.contains(player)) {
                    noDamageList.remove(player);
                    apolloUtils.removeNoFallDuration(player);

                    if (!apolloUtils.isUsingLC(player) && debugNonLCmessage) {
                        player.sendMessage(infoPrefix + "Vous redevenez sensible au dégats de chute.");
                    }
                }
            }
        };
    }
}
