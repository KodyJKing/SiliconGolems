package silicongolems.item;

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

        Object o = getClass().getClassLoader();
        Class c = o.getClass();
        System.out.println(c.getName());
        try {
            try (
                InputStream stream = getClass().getResourceAsStream("/assets/silicongolems/js/edit.js");
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader br = new BufferedReader(reader);
                ) {
                String line;
                while((line = br.readLine()) != null)
                    System.out.println(line);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return result;
    }
}
