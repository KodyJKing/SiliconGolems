package silicongolems.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.SiliconGolems;
import silicongolems.computer.Terminal;
import silicongolems.entity.EntitySiliconGolem;

public class ModGuiHandler implements IGuiHandler {
    private static final int TERMINAL = 0;
    private static final int GOLEM_INVENTORY = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getGuiElement(ID, player, world, new BlockPos(x, y, z), true);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getGuiElement(ID, player, world, new BlockPos(x, y, z), false);
    }

    public Object getGuiElement(int ID, EntityPlayer player, World world, BlockPos pos, boolean serverSide) {
        if (ID == TERMINAL && !serverSide)
            return new GuiTerminal(Terminal.getInstance(Side.CLIENT, pos.getX()));

        if (ID == GOLEM_INVENTORY)
            return golemInvGui(player, serverSide, pos.getX());

        return null;
    }

    public Object golemInvGui(EntityPlayer player, boolean serverSide, int activeGolemID) {
        EntitySiliconGolem golem = (EntitySiliconGolem) player.world.getEntityByID(activeGolemID);

        if (serverSide)
            return new ContainerChest(player.inventory, golem.inventory, player);
        else
            return new GuiChest(player.inventory, golem.inventory);
    }

    public static void openTerminal(EntityPlayer player, int terminalId) {
        player.openGui(SiliconGolems.instance, TERMINAL, player.world, terminalId, 0, 0);
    }

    public static void openGolemInv(EntityPlayer player, EntitySiliconGolem entity) {
        player.openGui(SiliconGolems.instance, GOLEM_INVENTORY, player.world, entity.getEntityId(), 0, 0);
    }
}
