package us.talabrek.ultimateskyblock.util.nbt;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;
import static us.talabrek.ultimateskyblock.util.reflection.ReflectionUtil.exec;
import static us.talabrek.ultimateskyblock.util.reflection.ReflectionUtil.execStatic;

/**
 * An NBTItemStackTagger using reflection for CraftBukkit based servers.
 */
public class CraftBukkitNBTTagger implements NBTItemStackTagger {
    private static final Logger log = Logger.getLogger(CraftBukkitNBTTagger.class.getName());

    @Override
    public String getNBTTag(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }
        Object nmsItem = execStatic(getCraftItemStackClass(), "asNMSCopy", itemStack);
        Object nbtTag = exec(nmsItem, "getTag");
        return nbtTag != null ? "" + nbtTag : "";
    }

    @Override
    public ItemStack setNBTTag(ItemStack itemStack, String nbtTagString) {
        if (itemStack == null || nbtTagString == null || nbtTagString.isEmpty()) {
            return itemStack;
        }
        Object nmsItem = execStatic(getCraftItemStackClass(), "asNMSCopy", itemStack);
        Object nbtTag = execStatic(getNBTTagParser(nmsItem), "parse", nbtTagString);
        exec(nmsItem, "setTag", nbtTag);
        Object item = execStatic(getCraftItemStackClass(), "asBukkitCopy", nmsItem);
        if (item instanceof ItemStack) {
            return (ItemStack) item;
        }
        return itemStack;
    }

    @Override
    public ItemStack addNBTTag(ItemStack itemStack, String nbtTagString) {
        if (itemStack == null || nbtTagString == null || nbtTagString.isEmpty()) {
            return itemStack;
        }
        Object nmsItem = execStatic(getCraftItemStackClass(), "asNMSCopy", itemStack);
        Object nbtTag = exec(nmsItem, "getTag");
        Object nbtTagNew = execStatic(getNBTTagParser(nmsItem), "parse", nbtTagString);
        nbtTag = merge(nbtTagNew, nbtTag);
        exec(nmsItem, "setTag", nbtTag);
        Object item = execStatic(getCraftItemStackClass(), "asBukkitCopy", nmsItem);
        if (item instanceof ItemStack) {
            return (ItemStack) item;
        }
        return itemStack;
    }

    /**
     * Merges two NBTTagCompound objects
     */
    private static Object merge(Object src, Object tgt) {
        if (tgt == null) {
            return src;
        }
        try {
            Field mapField = src.getClass().getDeclaredField("map");
            boolean wasAccessible = mapField.isAccessible();
            mapField.setAccessible(true);
            Map<String, Object> map = (Map<String, Object>) mapField.get(src);
            mapField.setAccessible(wasAccessible);
            Class<?> NBTBase = Class.forName("net.minecraft.server.v1_12_R1.NBTBase");
            for (String key : map.keySet()) {
                Object val = exec(src, "get", new Class[]{String.class}, key);
                exec(tgt, "set", new Class[]{String.class, NBTBase}, key, val);
            }
            return tgt;
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            log.info("Unable to transfer NBTTag from " + src + " to " + tgt + ": " + e);
        }
        return tgt;
    }

    private static Class<?> getNBTTagParser(Object nmsItem) {
        try {
            return Class.forName("net.minecraft.server.v1_12_R1.MojangsonParser");
        } catch (ClassNotFoundException e) {
            log.info("Unable to instantiate MojangsonParser: " + e);
        }
        return null;
    }

    private static Class<?> getCraftItemStackClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack");
        } catch (Exception e) {
            log.info("Unable to find CraftItemStack: " + e);
        }
        return null;
    }


}
