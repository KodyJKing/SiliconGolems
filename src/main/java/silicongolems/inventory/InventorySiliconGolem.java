package silicongolems.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import silicongolems.entity.EntitySiliconGolem;

import javax.annotation.Nullable;

public class InventorySiliconGolem extends InventoryPlayer {

    EntitySiliconGolem siliconGolem;

    public InventorySiliconGolem(EntitySiliconGolem siliconGolem) {
        super(null);
        this.siliconGolem = siliconGolem;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSqToEntity(siliconGolem) <= 64.0D;
    }
}
