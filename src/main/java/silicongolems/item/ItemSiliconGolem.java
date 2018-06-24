package silicongolems.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import silicongolems.util.Util;
import silicongolems.entity.EntitySiliconGolem;

public class ItemSiliconGolem extends ItemBase {
    public ItemSiliconGolem(String name) {
        super(name);
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return EnumActionResult.SUCCESS;

        ItemStack stack = player.getHeldItem(hand);

        EntitySiliconGolem golem = new EntitySiliconGolem(world);
        BlockPos loc = pos.offset(facing);

        golem.setPosition(loc.getX() + 0.5D, loc.getY(), loc.getZ() + 0.5D);
        golem.rotationYawHead = (float) Util.roundTo(player.rotationYawHead + 180, 90);
        golem.rotationYaw = golem.rotationYawHead;
        golem.rotationDirty = true;

        if (stack.hasTagCompound())
            golem.readEntityFromNBT(stack.getTagCompound());

        golem.setHealth(2);

        world.spawnEntity(golem);

        player.inventory.deleteStack(stack);
        return EnumActionResult.SUCCESS;
    }
}
