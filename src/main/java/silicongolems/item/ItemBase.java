package silicongolems.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import silicongolems.SiliconGolems;

import java.util.List;

public class ItemBase extends Item {
    protected String name;

    public ItemBase(String name) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
    }

    public void init() {
        SiliconGolems.proxy.registerItemRenderer(this, 0, name);
    }
}
