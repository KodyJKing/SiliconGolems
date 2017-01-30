package silicongolems.javascript.js;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WrapperItemStack {

    public final int itemId, stackSize, maxStackSize, meta, damage, maxDamage;
    public final String displayName, name, modid;

    public WrapperItemStack(ItemStack stack) {
        Item item = stack.getItem();
        itemId = Item.getIdFromItem(item);
        stackSize = stack.stackSize;
        maxStackSize = stack.getMaxStackSize();
        meta = stack.getMetadata();
        damage = stack.getItemDamage();
        maxDamage = stack.getMaxDamage();

        displayName = stack.getDisplayName();
        name = stack.getUnlocalizedName();
        modid = item.getRegistryName().getResourceDomain();
    }
}
