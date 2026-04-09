package scriptservice.ports.utils.web;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;


// from https://lunarclient.dev/apollo/developers/modules/glint
public class ItemUtil {
    public static ItemStack addTag(ItemStack item, String key, Object value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        NBTTagCompound lunarTag = tag.getCompound("lunar");
        if (lunarTag == null) {
            lunarTag = new NBTTagCompound();
        }

        if (value instanceof Integer) {
            lunarTag.setInt(key, (Integer) value);
        } else if (value instanceof Double) {
            lunarTag.setDouble(key, (Double) value);
        } else if (value instanceof Float) {
            lunarTag.setFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            lunarTag.setBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            lunarTag.setString(key, (String) value);
        }

        tag.set("lunar", lunarTag);
        nmsItem.setTag(tag);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private ItemUtil() {}
}