package silicongolems.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import silicongolems.SiliconGolems;
import silicongolems.inventory.InventorySiliconGolem;
import silicongolems.util.Util;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;
import silicongolems.gui.ModGuiHandler;
import silicongolems.item.ModItems;
import silicongolems.network.MessageHeading;
import silicongolems.network.MessageOpenComputer;
import silicongolems.network.ModPacketHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntitySiliconGolem extends EntityLiving {

    public boolean rotationLocked = true;
    public boolean rotationDirty = false;
    public int attackTime = 0;

    boolean justSpawned = true;

    public Computer computer;
    private FakePlayer fakePlayer;

    public InventorySiliconGolem inventory;

    public EntitySiliconGolem(World world) {
        super(world);
        this.setSize(1.4F * 0.5F, 1);
        inventory = new InventorySiliconGolem(this);
        if (!world.isRemote) {
            computer = Computers.add(new Computer(world));
            computer.entity = this;
        }
    }

    // region Primary
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return true;

        ItemStack stack = player.getHeldItem(hand);

        if (!player.isSneaking() && computer.canOpen(player)) {
            computer.user = (EntityPlayerMP) player;
            ModPacketHandler.INSTANCE.sendTo(new MessageOpenComputer(computer), (EntityPlayerMP) player);
        } else if (player.isSneaking()) {
            player.openGui(SiliconGolems.instance, 1, world, getEntityId(), (int) player.posY, (int) player.posZ);
        }

        return true;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (world.isRemote)
            return;

        computer.killProcess();

        ItemStack drop = new ItemStack(ModItems.siliconGolem, 1);
        drop.setTagCompound(writeToNBT(new NBTTagCompound()));

        InventoryHelper.spawnItemStack(world, posX, posY, posZ, drop);
    }

    // @Override
    // protected void despawnEntity() {
    // super.despawnEntity();
    // computer.onDestroy();
    // }

    @Override
    public void setDead() {
        super.setDead();
        if (world.isRemote)
            return;
        computer.onDestroy();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        renderYawOffset = rotationYaw;

        if (!world.isRemote) {
            if (justSpawned) {
                justSpawned = false;
                computer.runProgram("startup");
            }
            computer.updateComputer();
            if (rotationDirty) {
                ModPacketHandler.INSTANCE.sendToAllAround(new MessageHeading(this),
                        new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 100));
                rotationDirty = false;
            }
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2);
    }

    public FakePlayer getFakePlayer() {
        WorldServer server = getServer().getWorld(dimension);
        fakePlayer = FakePlayerFactory.get(server,
                new GameProfile(new UUID(0, 0), "SiliconGolem" + Integer.toString(getEntityId())));
        fakePlayer.inventory = inventory;
        inventory.player = fakePlayer;

        fakePlayer.posX = posX;
        fakePlayer.posY = posY;
        fakePlayer.posZ = posZ;
        fakePlayer.rotationYaw = rotationYaw;
        fakePlayer.rotationPitch = rotationPitch;
        fakePlayer.rotationYawHead = rotationYawHead;
        return fakePlayer;
    }

    // endregion

    // region NBT
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        nbt.setTag("inventory", Util.invToNbt(inventory));

        computer.writeNBT(nbt);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        NBTTagList inventoryNbt = nbt.getTagList("inventory", 10);
        Util.nbtToInv(inventoryNbt, inventory);

        computer.readNBT(nbt);
    }
    // endregion


    // region Clear AI
    @Override
    protected void initEntityAI() {
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }
    // endregion

    // region Movement
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
            int posRotationIncrements, boolean teleport) {
        if (isRotationLocked())
            super.setPositionAndRotationDirect(x, y, z, rotationYaw, rotationPitch, newPosRotationIncrements, teleport);
        else
            super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        if (isRotationLocked())
            return;
        super.setRotation(yaw, pitch);
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        if (isRotationLocked())
            super.setPositionAndRotation(x, y, z, rotationYaw, rotationPitch);
        else
            super.setPositionAndRotation(x, y, z, yaw, pitch);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        if (isRotationLocked())
            super.setLocationAndAngles(x, y, z, rotationYaw, rotationPitch);
        else
            super.setLocationAndAngles(x, y, z, yaw, pitch);
    }

    public boolean isRotationLocked() {
        return !world.isRemote && rotationLocked;
    }

    public void snapToGrid() {
        this.setPosition(Math.floor(this.posX) + 0.5, this.posY, Math.floor(this.posZ) + 0.5);
    }

    public void alignToGrid() {
        this.rotationYawHead = Math.round(this.rotationYawHead / 90) * 90;
        this.rotationYaw = this.rotationYawHead;
        this.rotationDirty = true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
    // endregion

    // region Sounds
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
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
    // endregion

}
