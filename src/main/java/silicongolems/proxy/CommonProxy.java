package silicongolems.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.computer.Computer;

public class CommonProxy {

    public Side side() {
        return Side.SERVER;
    }

    public void registerItemRenderer(Item item, int meta, String name) {
    }

    public void ignoreProperty(Block block, IProperty property) {}

    public void registerEntityRendering() {}
}
