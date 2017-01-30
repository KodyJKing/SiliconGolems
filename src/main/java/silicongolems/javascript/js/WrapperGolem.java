package silicongolems.javascript.js;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import silicongolems.common.Common;
import silicongolems.entity.EntitySiliconGolem;

public class WrapperGolem {

    private EntitySiliconGolem golem;
    private boolean autoSnap = true;

    public WrapperGolem(EntitySiliconGolem golem){
        this.golem = golem;
    }

    public void grid(boolean val){
        autoSnap = val;
    }

    public void turn(float angle){
        golem.rotationYawHead += angle;
        golem.rotationYaw = golem.rotationYawHead;
        if(autoSnap) {
            snap();
            align();
        }
    }

    public void move(){
        float dx = -MathHelper.sin(golem.rotationYaw * 0.017453292F);
        float dz = MathHelper.cos(golem.rotationYaw * 0.017453292F);
        golem.moveEntity(dx, 0, dz);
        if(autoSnap) {
            snap();
            align();
        }
        sleep(250);
    }

    public void jump(){
        if(!golem.onGround)
            return;
        golem.motionY = 0.42;
        sleep(250);
    }

    public void snap(){
        golem.setPosition(Math.floor(golem.posX) + 0.5,  golem.posY, Math.floor(golem.posZ) + 0.5);
    }

    public void align(){
        golem.rotationYawHead = Math.round(golem.rotationYawHead / 90) * 90;
        golem.rotationYaw = golem.rotationYawHead;
    }

    public boolean build(int forward, int up, int right){
        right = Common.clamp(-1, 1, right);
        up = Common.clamp(-1, 1, up);
        forward = Common.clamp(-1, 1, forward);

        EnumFacing forwardFacing = golem.getHorizontalFacing();
        EnumFacing rightFacing = forwardFacing.rotateY();

        BlockPos pos = new BlockPos(golem).offset(forwardFacing, forward).offset(rightFacing, right).add(0, up, 0);

        if(!golem.worldObj.isAirBlock(pos))
            return false;
        golem.worldObj.setBlockState(pos, Blocks.STONEBRICK.getDefaultState());
        sleep(125);
        return true;
    }

    public WrapperItemStack scanStack(int i){
        ItemStack stack = golem.inventory.getStackInSlot(i);
        return stack == null ? null : new WrapperItemStack(stack);
    }

    private void sleep(int milis){
        try{ Thread.sleep(milis); } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public String toString() {
        return "golem" + Integer.toString(golem.getEntityId());
    }
}
