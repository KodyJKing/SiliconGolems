package silicongolems.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class InventoryFakePlayer extends InventoryPlayer {

    public InventoryBasic inventory;

    public InventoryFakePlayer(EntityPlayer playerIn, InventoryBasic inventory) {
        super(playerIn);
        this.inventory = inventory;
    }

    //region Basic Inventory Wrapper
    @Override
    @Nullable
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    @Nullable
    public ItemStack decrStackSize(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Nullable
    public ItemStack addItem(ItemStack stack) {
        return inventory.addItem(stack);
    }

    @Override
    @Nullable
    public ItemStack removeStackFromSlot(int index) {
        return inventory.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        inventory.setInventorySlotContents(index, stack);
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    public void setCustomName(String inventoryTitleIn) {
        inventory.setCustomName(inventoryTitleIn);
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inventory.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return inventory.isItemValidForSlot(index, stack);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }
    //endregion

    @Override
    public void deleteStack(ItemStack stack) {
        for(int i = 0; i < getSizeInventory(); i++){
            if(getStackInSlot(i) == stack)
                setInventorySlotContents(i, null);
        }
    }

    @Nullable
    @Override
    public ItemStack getCurrentItem() {
        return inventory.getStackInSlot(currentItem);
    }

}
