package silicongolems.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import silicongolems.computer.Computer;
import silicongolems.gui.window.WindowEditor;

public class ModGuiHandler implements IGuiHandler {

    public static Computer activeComputer;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getGuiElement(ID, player, world, new BlockPos(x, y, z), true);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getGuiElement(ID, player, world, new BlockPos(x, y, z), false);
    }

    public Object getGuiElement(int ID, EntityPlayer player, World world, BlockPos pos, boolean serverSide) {
        if(serverSide)
            return null;

        if(ID == 0)
            return new GuiScreenOS(activeComputer);

        return null;
    }
}
