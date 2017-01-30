package silicongolems.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import silicongolems.SiliconGolems;
import silicongolems.common.Common;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;
import silicongolems.gui.ModGuiHandler;
import silicongolems.network.MessageOpenComputer;
import silicongolems.network.ModPacketHandler;

import javax.annotation.Nullable;

public class EntitySiliconGolem extends EntityLiving {

    public boolean rotationLocked = true;
    public int attackTime = 0;

    public silicongolems.computer.Computer computer;

    public InventoryBasic inventory;

    public EntitySiliconGolem(World world) {
        super(world);
        this.setSize(1.4F * 0.5F, 1);
        if(!world.isRemote){
            computer = Computers.add(new Computer(world));
            computer.entity = this;
        }
        inventory = new InventoryBasic("container.siliconGolem", false, 27);
    }

    //region Primary
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
        if(worldObj.isRemote)
            return true;

        if(!player.isSneaking() && computer.canOpen(player)){
            computer.user = (EntityPlayerMP) player;
            ModPacketHandler.INSTANCE.sendTo(new MessageOpenComputer(computer), (EntityPlayerMP) player);
        } else if(player.isSneaking()){
            System.out.println("Trying to open gui!");
            ModGuiHandler.activeGolemID = this.getEntityId();
            player.openGui(SiliconGolems.instance, 1, worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
        }

        return true;
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
    //endregion

    //region NBT
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        nbt.setTag("inventory", Common.invToNbt(inventory));

        computer.writeNBT(nbt);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        NBTTagList inventoryNbt = nbt.getTagList("inventory", 10);
        Common.nbtToInv(inventoryNbt, inventory);

        computer.readNBT(nbt);
    }
    //endregion

    //region Clear AI
    @Override
    protected void initEntityAI() {
    }

    @Override
    public boolean isAIDisabled() {
        return true;
    }
    //endregion

    //region Rotation Locking
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
    //endregion

    //region Sounds
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
    //endregion
}
