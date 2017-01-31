package silicongolems.javascript;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class ConvertData {

    public static HashMap<String, Object> itemStackData(ItemStack stack) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Item item = stack.getItem();

        map.put("itemId", Item.getIdFromItem(item));
        map.put("stackSize", stack.stackSize);
        map.put("maxStackSize", stack.getMaxStackSize());
        map.put("meta", stack.getMetadata());
        map.put("damage", stack.getItemDamage());
        map.put("maxDamage", stack.getMaxDamage());

        map.put("displayName", stack.getDisplayName());
        map.put("name", stack.getUnlocalizedName());
        map.put("modId", item.getRegistryName().getResourceDomain());

        return map;
    }
}
