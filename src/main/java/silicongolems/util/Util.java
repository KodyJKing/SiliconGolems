package silicongolems.util;

import com.google.gson.Gson;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatAllowedCharacters;
import silicongolems.SiliconGolems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    public static Random random = new Random();
    public static Gson gson = new Gson();

    public static boolean blink(int period, int duration) {
        return System.currentTimeMillis() % period < duration;
    }

    public static int clamp(int low, int high, int x) {
        if (x < low)
            return low;
        if (x > high)
            return high;
        return x;
    }

    public static NBTTagList invToNbt(IInventory inv) {
        NBTTagList inventoryNbt = new NBTTagList();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            NBTTagCompound stackNbt = new NBTTagCompound();
            if (stack != null)
                stack.writeToNBT(stackNbt);
            inventoryNbt.appendTag(stackNbt);
        }

        return inventoryNbt;
    }

    public static void nbtToInv(NBTTagList nbt, IInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++)
            inv.setInventorySlotContents(i, new ItemStack(nbt.getCompoundTagAt(i)));
    }

    public static String removeUnprintable(String str) {
        StringBuilder builder = new StringBuilder();
        for (Character c : str.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c))
                builder.append(c);
        }
        return builder.toString();
    }

    public static List<String> printableLines(String str, int maxWidth) {

        ArrayList<String> lines = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        int width = 0;

        for (Character c: str.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c))
                builder.append(c);

            if (++width >= maxWidth || c == '\n') {
                width = 0;
                lines.add(builder.toString());
                builder = new StringBuilder();
            }
        }

        if (builder.length() > 0) {
            lines.add(builder.toString());
        }

        return lines;
    }

    public static double roundTo(double val, double round) {
        return Math.round(val / round) * round;
    }

    public static int mod(int dividend, int divisor) {
        int result = dividend % divisor;
        if (result < 0) result += divisor;
        return result;
    }

    public static String getResource(String path) {
        try {
            try (
                    InputStream stream = Util.class.getResourceAsStream(path);
                    InputStreamReader reader = new InputStreamReader(stream);
                    BufferedReader br = new BufferedReader(reader);
            ) {
                return Util.readAll(br);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readAll(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }
}
