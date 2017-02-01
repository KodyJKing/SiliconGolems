package silicongolems.javascript;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import scala.Int;
import silicongolems.common.Common;
import silicongolems.computer.Computer;
import silicongolems.entity.EntitySiliconGolem;

import java.util.List;
import java.util.UUID;

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

    private void snap(){
        computer.addJob(() -> {golem.setPosition(Math.floor(golem.posX) + 0.5,  golem.posY, Math.floor(golem.posZ) + 0.5);});
    }

    private void align(){
        computer.addJob(() -> {
            golem.rotationYawHead = Math.round(golem.rotationYawHead / 90) * 90;
            golem.rotationYaw = golem.rotationYawHead;
            golem.rotationDirty = true;
        });
    }

    public void build(int forward, int up, int right){
        BlockPos pos = relPos(forward, up, right);

        if(!golem.worldObj.isAirBlock(pos))
            return;

        computer.addJob(() -> {golem.worldObj.setBlockState(pos, Blocks.STONEBRICK.getDefaultState());});
        computer.awaitUpdate(125);

        return;
    }

    public void use(int index, int forward, int up, int right){
        BlockPos pos = relPos(forward, up, right);
        ItemStack stack = golem.inventory.getStackInSlot(index);
        FakePlayer fp = getFakePlayer();

        computer.addJob(() -> {stack.onItemUse(fp, golem.worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.UP, pos.getX(), pos.getY(), pos.getZ());});
        computer.awaitUpdate(125);
    }

    public boolean dig(int forward, int up, int right){
        BlockPos pos = relPos(forward, up, right);

        if(golem.worldObj.isAirBlock(pos))
            return false;

        computer.addJob(() -> {golem.worldObj.destroyBlock(pos, true);});
        computer.awaitUpdate(125);

        return true;
    }

    public void suck(){
        computer.addJob(() -> {        BlockPos pos = new BlockPos(golem);
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

    @Override
    public String toString() {
        return "golem" + Integer.toString(golem.getEntityId());
    }

    private BlockPos relPos(int forward, int up, int right){
        right = Common.clamp(-1, 1, right);
        up = Common.clamp(-1, 1, up);
        forward = Common.clamp(-1, 1, forward);

        EnumFacing forwardFacing = golem.getHorizontalFacing();
        EnumFacing rightFacing = forwardFacing.rotateY();

        BlockPos pos = new BlockPos(golem).offset(forwardFacing, forward).offset(rightFacing, right).add(0, up, 0);

        return pos;
    }

    private FakePlayer getFakePlayer(){
        WorldServer server = golem.getServer().worldServerForDimension(golem.dimension);
        FakePlayer fp = FakePlayerFactory.get(server, new GameProfile(new UUID(0,0), "SiliconGolem"));
        fp.rotationYaw = golem.rotationYaw;
        fp.rotationPitch = golem.rotationPitch;
        fp.rotationYawHead = golem.rotationYawHead;
        return fp;
    }
}
