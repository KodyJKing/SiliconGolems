package silicongolems.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemDevTool extends ItemBase {

    public ItemDevTool(String name) {
        super(name);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        ActionResult<ItemStack> result = new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

        return result;
    }
}
