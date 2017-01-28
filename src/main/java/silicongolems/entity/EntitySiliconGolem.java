package silicongolems.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;
import silicongolems.network.MessageOpenCloseOS;
import silicongolems.network.ModPacketHandler;

import javax.annotation.Nullable;

public class EntitySiliconGolem extends EntityLiving {

    public boolean rotationLocked = true;
    public int attackTime = 0;

    public silicongolems.computer.Computer computer;

    public EntitySiliconGolem(World world) {
        super(world);
        this.setSize(1.4F * 0.5F, 1);
        if(!world.isRemote){
            computer = Computers.add(new Computer(world));
            computer.entity = this;
        }
    }

    public void executeCommand(String command){
        System.out.println(command);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
        if(!worldObj.isRemote && !player.isSneaking() && computer.canOpen(player)){
            computer.user = (EntityPlayerMP) player;
            ModPacketHandler.INSTANCE.sendTo(new MessageOpenCloseOS(computer), (EntityPlayerMP) player);
        }

        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        computer.writeNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        computer.readNBT(compound);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        computer.killProcess();
    }

    @Override
    protected void despawnEntity() {
        super.despawnEntity();
        computer.onDestroy();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if(!worldObj.isRemote)
            computer.updateComputer();
    }

    // Boiler Plate Below -------------------------------------------

    @Override
    protected void initEntityAI() {
    }

    @Override
    public boolean isAIDisabled() {
        return true;
    }

    @Override
    public void setMoveForward(float amount) {
        super.setMoveForward(amount);
    }

    // Rotation Locking -------------------------------------------

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        if(isRotationLocked())
            super.setPositionAndRotationDirect(x, y, z, rotationYaw, rotationPitch, newPosRotationIncrements, teleport);
        else
            super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        if(isRotationLocked())
            return;
        super.setRotation(yaw, pitch);
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        if(isRotationLocked())
            super.setPositionAndRotation(x, y, z, rotationYaw, rotationPitch);
        else
            super.setPositionAndRotation(x, y, z, yaw, pitch);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        if(isRotationLocked())
            super.setLocationAndAngles(x, y, z, rotationYaw, rotationPitch);
        else
            super.setLocationAndAngles(x, y, z, yaw, pitch);
    }

    @Override
    public void setAngles(float yaw, float pitch) {
        if(isRotationLocked())
            super.setAngles(rotationYaw, rotationPitch);
        else
            super.setAngles(yaw, pitch);
    }

    public boolean isRotationLocked() {
        return !worldObj.isRemote && rotationLocked;
    }


    // Sounds -------------------------------------------

    @Nullable
    @Override
    protected SoundEvent getHurtSound() {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, getSoundVolume(), getSoundPitch());
    }

    @Override
    protected float getSoundPitch() {
        return 1.5F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }
}
