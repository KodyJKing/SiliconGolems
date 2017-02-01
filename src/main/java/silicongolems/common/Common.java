package silicongolems.common;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
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

    public static NBTTagList invToNbt(IInventory inv){
        NBTTagList inventoryNbt = new NBTTagList();
        for(int i = 0; i < inv.getSizeInventory(); i++){
            ItemStack stack = inv.getStackInSlot(i);
            NBTTagCompound stackNbt = new NBTTagCompound();
            if(stack != null)
                stack.writeToNBT(stackNbt);
            inventoryNbt.appendTag(stackNbt);
        }

        return inventoryNbt;
    }

    public static void nbtToInv(NBTTagList nbt, IInventory inv){
        for(int i = 0; i < inv.getSizeInventory(); i++)
            inv.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(nbt.getCompoundTagAt(i)));
    }

    public static String removeUnprintable(String str){
        StringBuilder builder = new StringBuilder();
        for(Character c : str.toCharArray()){
            if(ChatAllowedCharacters.isAllowedCharacter(c))
                builder.append(c);
        }
        return builder.toString();
    }

    public static List<String> printableLines(String str, int maxWidth){

        ArrayList<String> lines = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        int width = 0;

        for(Character c: str.toCharArray()){
            if(ChatAllowedCharacters.isAllowedCharacter(c))
                builder.append(c);

            if(++width >= maxWidth || c == '\n'){
                width = 0;
                lines.add(builder.toString());
                builder = new StringBuilder();
            }
        }

        if(builder.length() > 0){
            lines.add(builder.toString());
        }

        return lines;
    }

//    public static boolean isGamePaused(){
//        if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
//            return isGamePausedClient();
//        return false;
//    }
//
//    @SideOnly(Side.CLIENT)
//    private static boolean isGamePausedClient(){
//        return Minecraft.getMinecraft().isGamePaused();
//    }

    public static double roundTo(double val, double round){
        return Math.round(val / round) * round;
    }
}
