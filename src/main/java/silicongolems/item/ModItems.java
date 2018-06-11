package silicongolems.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import silicongolems.SiliconGolems;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    private static List<Item> toRegister = new ArrayList<>();
    public static ItemBase devTool, siliconGolem;

    public void init() {
        if (SiliconGolems.devEnv)
            devTool = add(new ItemDevTool("devtool"));
        siliconGolem = add(new ItemSiliconGolem("silicongolem"));
    }

    @SubscribeEvent
    public void onItemRegistry(RegistryEvent.Register<Item> event) {
        init();
        IForgeRegistry<Item> registry = event.getRegistry();
        for (Item item: toRegister) {
            registry.register(item);
            if (item instanceof ItemBase) {
                ((ItemBase)item).init();
            } else if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock)item;
                Block block = itemBlock.getBlock();
//                if (block instanceof BlockBase)
//                    ((BlockBase)block).init(itemBlock);
            }
        }
        toRegister.clear();
    }

    public static <T extends Item> T add(T item) {
        toRegister.add(item);
        return item;
    }

}
