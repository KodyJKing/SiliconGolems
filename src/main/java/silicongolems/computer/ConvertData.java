package silicongolems.computer;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class ConvertData {

    public static HashMap<String, Object> itemData(ItemStack stack) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Item item = stack.getItem();

        map.put("id", Item.getIdFromItem(item));
        map.put("meta", stack.getMetadata());

        map.put("stackSize", stack.getCount());
        map.put("maxStackSize", stack.getMaxStackSize());

        map.put("damage", stack.getItemDamage());
        map.put("maxDamage", stack.getMaxDamage());

        map.put("displayName", stack.getDisplayName());
        map.put("name", stack.getUnlocalizedName());
        map.put("modId", item.getRegistryName().getResourceDomain());

        return map;
    }

    public static HashMap<String, Object> blockData(World world, BlockPos pos) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        map.put("id", Block.getIdFromBlock(block));
        map.put("meta", block.getMetaFromState(state));

        map.put("power", world.isBlockPowered(pos));
        map.put("light", world.getLightFromNeighbors(pos));

        map.put("displayName", block.getLocalizedName());
        map.put("name", block.getUnlocalizedName());
        map.put("modId", block.getRegistryName().getResourceDomain());

        for (IProperty property: state.getProperties().keySet())
            map.put(property.getName(), state.getValue(property));

        return map;
    }
}
