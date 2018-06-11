package silicongolems.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import silicongolems.computer.Computer;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.window.WindowEditor;

public class ModGuiHandler implements IGuiHandler {

    public static Computer activeComputer;
    public static int activeGolemID;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getGuiElement(ID, player, world, new BlockPos(x, y, z), true);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getGuiElement(ID, player, world, new BlockPos(x, y, z), false);
    }

    public Object getGuiElement(int ID, EntityPlayer player, World world, BlockPos pos, boolean serverSide) {
        if(ID == 0 && !serverSide)
            return new GuiScreenOS(activeComputer);

        if(ID == 1)
            return golemInvGui(player, serverSide);

        return null;
    }

    public Object golemInvGui(EntityPlayer player, boolean serverSide){
        EntitySiliconGolem golem = (EntitySiliconGolem) player.world.getEntityByID(activeGolemID);

        if(serverSide)
            return new ContainerChest(player.inventory, golem.inventory, player);
        else
            return new GuiChest(player.inventory, golem.inventory);
    }
}
