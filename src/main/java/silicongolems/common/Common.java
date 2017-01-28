package silicongolems.common;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class Common {

    public static boolean blink(int period, int duration){
        return System.currentTimeMillis() % period < duration;
    }

    public static int clamp(int low, int high, int x){
        if(x < low)
            return low;
        if(x > high)
            return high;
        return x;
    }
}
