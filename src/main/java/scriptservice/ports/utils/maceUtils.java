package scriptservice.ports.utils;

import lombok.Getter;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import scriptservice.ports.Main;
import scriptservice.ports.utils.web.ItemUtil;
import scriptservice.ports.utils.web.NBTEditor;
import scriptservice.ports.utils.web.RomanDigit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class maceUtils extends utilManager {
    public maceUtils(Main plugin) {
        super(plugin);
    }

    // init stuff
    private apolloUtils apolloUtils;
    private boolean debugNonLCmessage;
    @Getter private String infoPrefix;
    @Getter private String errorPrefix;

    @Override
    public void init() {
        this.apolloUtils = plugin.apolloUtils;
        // this.UTIL = plugin.UTIL

        this.infoPrefix = (plugin.stringUtils.formatBracket(ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, "Mace") + " ");
        this.errorPrefix = (plugin.stringUtils.formatBracket(ChatColor.DARK_RED, ChatColor.RED, "Mace") + " " + ChatColor.RED);

        debugNonLCmessage = (boolean) plugin.debugConfig.get("nonLCmessage");
    }

    // per-class
    // lists
    public final Set<Entity> fallDamageReductionList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public final HashMap<Entity, TimerTask> timerTasks = new HashMap<>();

    // string
    @Getter private final String itemName = ("" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Mace");

    // functions
    public final TimerTask createFallDamageTask(Player player) {
        return new TimerTask() {
            @Override
            public void run() {
                if (fallDamageReductionList.contains(player)) {
                    fallDamageReductionList.remove(player);
                    apolloUtils.removeNoFallDuration(player);

                    if (!apolloUtils.isUsingLC(player) && debugNonLCmessage) {
                        player.sendMessage(infoPrefix + "Vous redevenez sensible au dégats de chute.");
                    }
                }
            }
        };
    }

    public final void spawnParticleCircle(Location location, double radius, EnumParticle particle, float xParticleVector, float yParticleVector, float zParticleVector, float particleSpeed, int particleAmount) {
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
                            xParticleVector, yParticleVector, zParticleVector, // particle vector (x,y,z)
                            particleSpeed, // particle speed ?
                            particleAmount // amount
                    );

            for (Player player : world.getPlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    public final void spawnParticleCircle(Location location, double radius, EnumParticle particle) {
        spawnParticleCircle(location, radius, particle, 0f, 0f, 0f, 0f, 1);
    }

    public final void giveItem(Player player, int amount, int windburstLevel) {
        // create new mace
        ItemStack newMace = new ItemStack(Material.WOOD_SWORD, amount);
        ItemMeta meta = newMace.getItemMeta();

        // set enchanted loo
        meta.addEnchant(Enchantment.DURABILITY, 50, true);
        meta.setDisplayName(getItemName());

        // set item lore
        if (windburstLevel != 0) {
            meta.setLore(
                    Arrays.asList(
                            ChatColor.RESET+""+ChatColor.GRAY+"Windburst " + RomanDigit.convert(windburstLevel),
                            ChatColor.RESET+""+ChatColor.DARK_GRAY+"Unbreakable"
                    )
            );
        } else {
            meta.setLore(
                    Collections.singletonList(
                            ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Unbreakable"
                    )
            );
        }

        // add flags
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        newMace.setItemMeta(meta);

        String chosenColor = "#ababab";
        if (windburstLevel == 1) {
            chosenColor = "#541c54";
        } else if (windburstLevel == 2) {
            chosenColor = "#ab38ab";
        } else if (windburstLevel == 3) {
            chosenColor = "#FF54FF";
        } else if (windburstLevel > 3) {
            chosenColor = "#ab3838";
        }

        ItemStack coloredMace = ItemUtil.addTag(newMace, "glint", chosenColor);
        ItemStack unstackableMace = ItemUtil.addTag(coloredMace, "blehh ", System.currentTimeMillis());
        ItemStack unbreakableMace = NBTEditor.set( unstackableMace, true, "Unbreakable" );
        ItemStack givenMace = unbreakableMace;

        if (windburstLevel != 0) {
            givenMace = NBTEditor.set(unbreakableMace, windburstLevel, NBTEditor.CUSTOM_DATA, "Windburst");
        }

        player.getInventory().addItem(givenMace);
    }
}
