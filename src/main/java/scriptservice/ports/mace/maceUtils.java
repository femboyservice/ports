package scriptservice.ports.mace;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import scriptservice.ports.utils.ItemUtil;
import scriptservice.ports.utils.NBTEditor;
import scriptservice.ports.utils.RomanDigit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class maceUtils {
    // fonctions
    public String formatBracket(ChatColor color, String text) {
        return (color+"{"+text+color+"}"+ChatColor.RESET);
    }
    public String formatGreaterArrow(ChatColor color, ChatColor textColor, String text) {
        return (color+"> "+textColor+text+ChatColor.RESET);
    }

    // strings
    public final String itemName = ("" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Mace");

    public final String errorPrefix = (formatBracket(ChatColor.DARK_GRAY, (ChatColor.DARK_RED+"Mace")) + ChatColor.RED + " ");
    public final String validPrefix = (formatBracket(ChatColor.DARK_GRAY, (ChatColor.DARK_GREEN+"Mace")) + ChatColor.WHITE + " ");
    public final String infoPrefix = (formatBracket(ChatColor.DARK_GRAY, (ChatColor.DARK_AQUA+"Mace")) + ChatColor.WHITE + " ");

    public final String enchantment3Color = "#FF54FF";
    public final String enchantment2Color = "#ab38ab";
    public final String enchantment1Color = "#541c54";
    public final String noEnchantmentColor = "#ababab";
    public final String adminColor = "#ab3838";

    // consts
    public final int defaultFallDamageProtection = 4;
    public final double defaultMinY = 0.6;
    public final double defaultMinDamage = 5.5;
    public final boolean defaultInfoMessage = false;

    // vars
    public int fallDamageProtection = defaultFallDamageProtection;
    public double minY = defaultMinY;
    public double minDamage = defaultMinDamage;
    public boolean infoMessage = defaultInfoMessage;

    // lists
    public Set<Entity> fallDamageReductionList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public HashMap<Entity, TimerTask> timerTasks = new HashMap<>();

    // after functions
    public void giveItem(Player player, int amount, int windburstLevel) {
        // create new mace
        ItemStack itemStack = new ItemStack(Material.WOOD_SWORD, amount);
        ItemMeta meta = itemStack.getItemMeta();

        // set enchanted loo
        meta.addEnchant(Enchantment.DURABILITY, 50, true);
        meta.setDisplayName(itemName);

        // set item lore
        if (windburstLevel != 0) {
            meta.setLore(
                    Arrays.asList(
                            ChatColor.RESET+""+ChatColor.GRAY+"Windburst " + RomanDigit.convert(windburstLevel),
                            ChatColor.RESET+""+ChatColor.GRAY+"Unbreakable"
                    )
            );
        } else {
            meta.setLore(
                    Arrays.asList(
                            ChatColor.RESET+""+ChatColor.GRAY+"Unbreakable"
                    )
            );
        }

        // add flags
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(meta);

        String chosenColor = noEnchantmentColor;
        if (windburstLevel == 1) {
            chosenColor = enchantment1Color;
        } else if (windburstLevel == 2) {
            chosenColor = enchantment2Color;
        } else if (windburstLevel == 3) {
            chosenColor = enchantment3Color;
        } else if (windburstLevel > 3) {
            chosenColor = adminColor;
        }

        ItemStack coloredMace = ItemUtil.addTag(itemStack, "glint", chosenColor);
        ItemStack unstackableMace = ItemUtil.addTag(coloredMace, "bleh :P", System.currentTimeMillis());
        ItemStack unbreakableMace = NBTEditor.set( unstackableMace, true, "Unbreakable" );
        ItemStack givenMace = unbreakableMace;

        if (windburstLevel != 0) {
            givenMace = NBTEditor.set(unbreakableMace, windburstLevel, NBTEditor.CUSTOM_DATA, "Windburst");
        }

        player.getInventory().addItem(givenMace);
    }
}
