package silicongolems.item;

import com.eclipsesource.v8.V8;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.io.*;
import java.net.URL;

public class ItemDevTool extends ItemBase {

    public ItemDevTool(String name) {
        super(name);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ActionResult<ItemStack> result = new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

        try {
            V8 engine = V8.createV8Runtime();
            Object out = engine.executeIntegerScript("10 + 32");
            System.out.println(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
