package silicongolems.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.SiliconGolems;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SiliconGolems.modId);

    public static void registerPackets(){
        int id = 0;

        INSTANCE.registerMessage(MessageOpenCloseFile.Handler.class, MessageOpenCloseFile.class, id++, Side.SERVER);
        INSTANCE.registerMessage(MessageOpenCloseFile.Handler.class, MessageOpenCloseFile.class, id++, Side.CLIENT);

        INSTANCE.registerMessage(MessageOpenCloseTerminal.Handler.class, MessageOpenCloseTerminal.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(MessageOpenCloseTerminal.Handler.class, MessageOpenCloseTerminal.class, id++, Side.SERVER);

        INSTANCE.registerMessage(MessageTerminalCommand.Handler.class, MessageTerminalCommand.class, id++, Side.SERVER);

        INSTANCE.registerMessage(MessageTerminalPrint.Handler.class, MessageTerminalPrint.class, id++, Side.CLIENT);
    }

}
