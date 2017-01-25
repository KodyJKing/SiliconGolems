package silicongolems.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import silicongolems.item.ItemBase;
import silicongolems.item.ItemDevTool;

public class ModItems {

    public static ItemBase devTool;

    public static void init(){
        devTool = register(new ItemDevTool("devTool"));
    }

    private static <T extends Item> T register(T item) {
        GameRegistry.register(item);

        if (item instanceof ItemBase) {
            ((ItemBase)item).init();
        }

        return item;
    }

}
