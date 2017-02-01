package silicongolems.javascript;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.common.util.FakePlayer;
import silicongolems.util.FakePlayerUtil;
import silicongolems.util.Util;
import silicongolems.computer.Computer;
import silicongolems.entity.EntitySiliconGolem;

import java.util.List;

public class WrapperGolem {

    private EntitySiliconGolem golem;
    private Computer computer;
    private boolean autoSnap = true;

    public WrapperGolem(EntitySiliconGolem golem){
        this.golem = golem;
        computer = golem.computer;
    }

    public void grid(boolean val){
        autoSnap = val;
    }

    public void turn(float angle){

        computer.addJob(() -> {
            golem.rotationYawHead += angle;
            golem.rotationYaw = golem.rotationYawHead;
            if(autoSnap) {
                snap();
                align();
            }
            golem.rotationDirty = true;
        });


        computer.awaitUpdate(0);
    }

    public boolean move(){
        int oldx = (int) golem.posX;
        int oldz = (int) golem.posZ;

        float dx = -MathHelper.sin(golem.rotationYaw * 0.017453292F); //0.017453292F = PI / 180, degrees to radians
        float dz = MathHelper.cos(golem.rotationYaw * 0.017453292F);

        computer.addJob(() -> {
            golem.moveEntity(dx, 0, dz);
            if(autoSnap) {
                snap();
                align();
            }});

        computer.awaitUpdate(250);

        return oldx != ((int) golem.posX) || oldz != ((int) golem.posZ);

    }

    public boolean jump(){
        int oldy = (int) golem.posY;

        if(!golem.onGround)
            return false;
        golem.motionY = 0.42;
        computer.awaitUpdate(250);

        return ((int) golem.posY) != oldy;
    }

    public void snap(){
        computer.addJob(() -> {golem.setPosition(Math.floor(golem.posX) + 0.5,  golem.posY, Math.floor(golem.posZ) + 0.5);});
    }

    public void align(){
        computer.addJob(() -> {
            golem.rotationYawHead = Math.round(golem.rotationYawHead / 90) * 90;
            golem.rotationYaw = golem.rotationYawHead;
            golem.rotationDirty = true;
        });
    }

    public void use(int index, int forward, int up, int right){
        EnumFacing dir = up == 0 ? golem.getHorizontalFacing().getOpposite() : (up == -1 ? EnumFacing.UP : EnumFacing.DOWN);

        BlockPos pos = relPos(forward, up, right);
        ItemStack stack = golem.inventory.getStackInSlot(index);
        FakePlayer fp = golem.getFakePlayer();

        computer.addJob(() -> {
            fp.inventory.currentItem = index;
            fp.interactionManager.processRightClickBlock(fp, golem.worldObj, stack, EnumHand.MAIN_HAND, pos, dir, pos.getX(), pos.getY(), pos.getZ());
            if(stack != null && stack.stackSize == 0)
                golem.inventory.setInventorySlotContents(index, null);
        });
        computer.awaitUpdate(125);
    }

    public void use(int index){use(index, 1, 0, 0);}

    public void click(int index, float pitch){
        golem.rotationPitch = pitch;
        FakePlayer fp = golem.getFakePlayer();

        ItemStack stack = golem.inventory.getStackInSlot(index);

        computer.addJob(() -> {
            fp.inventory.currentItem = index;
            FakePlayerUtil.rightClick(fp, golem, stack);

            if(stack != null && stack.stackSize == 0)
                golem.inventory.setInventorySlotContents(index, null);
        });
        computer.awaitUpdate(125);
    }
    public void click(int index){click(index, 0);}

    public boolean dig(int forward, int up, int right, boolean drop){
        BlockPos pos = relPos(forward, up, right);

        if(golem.worldObj.isAirBlock(pos))
            return false;

        computer.addJob(() -> {
            IBlockState state = golem.worldObj.getBlockState(pos);
            golem.worldObj.destroyBlock(pos, drop);

            if(!drop){
                List<ItemStack> drops = state.getBlock().getDrops(golem.worldObj, pos, state, 0);
                for(ItemStack stack: drops){
                    ItemStack remainder = golem.inventory.addItem(stack);
                    if(remainder != null && remainder.stackSize > 0)
                        Block.spawnAsEntity(golem.worldObj, pos, remainder);
                }
            }
        });
        computer.awaitUpdate(125);

        return true;
    }
    public void dig(int forward, int up, int right) {dig(forward, up, right, false);}
    public void dig(){dig(1, 0, 0, false);}

    public void suck(){
        computer.addJob(() -> {
            BlockPos pos = new BlockPos(golem);
            List<EntityItem> items = golem.worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expandXyz(1));
            for(EntityItem item: items){
                ItemStack remainder = golem.inventory.addItem(item.getEntityItem());
                item.setEntityItemStack(remainder);
                if(remainder == null || remainder.stackSize == 0)
                    item.setDead();
            }});
        computer.awaitUpdate(62);
    }

    public Object scanStack(int i){
        ItemStack stack = golem.inventory.getStackInSlot(i);
        return stack == null ? null : ConvertData.itemData(stack);
    }

    public Object scan(int forward, int up, int right){
        BlockPos pos = relPos(forward, up, right);

        return ConvertData.blockData(golem.worldObj, pos);
    }

    public Object scan(){return scan(1, 0, 0);}

    @Override
    public String toString() {
        return "golem" + Integer.toString(golem.getEntityId());
    }

    private BlockPos relPos(int forward, int up, int right){
        right = Util.clamp(-1, 1, right);
        up = Util.clamp(-1, 1, up);
        forward = Util.clamp(-1, 1, forward);

        EnumFacing forwardFacing = golem.getHorizontalFacing();
        EnumFacing rightFacing = forwardFacing.rotateY();

        BlockPos pos = new BlockPos(golem).offset(forwardFacing, forward).offset(rightFacing, right).add(0, up, 0);

        return pos;
    }
}
