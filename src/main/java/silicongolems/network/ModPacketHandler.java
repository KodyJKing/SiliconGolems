package silicongolems.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.SiliconGolems;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SiliconGolems.modId);

    public static void registerPackets(){
        int id = 0;

        INSTANCE.registerMessage(MessageOpenCloseFile.Handler.class, MessageOpenCloseFile.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(MessageOpenCloseFile.Handler.class, MessageOpenCloseFile.class, id++, Side.SERVER);

        INSTANCE.registerMessage(MessageOpenComputer.Handler.class, MessageOpenComputer.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(MessageOpenComputer.Handler.class, MessageOpenComputer.class, id++, Side.SERVER);

        INSTANCE.registerMessage(MessageInput.Handler.class, MessageInput.class, id++, Side.SERVER);

        INSTANCE.registerMessage(MessagePrint.Handler.class, MessagePrint.class, id++, Side.CLIENT);

        INSTANCE.registerMessage(MessageByte.Handler.class, MessageByte.class, id++, Side.SERVER);
        INSTANCE.registerMessage(MessageByte.Handler.class, MessageByte.class, id++, Side.CLIENT);

        INSTANCE.registerMessage(MessageHeading.Handler.class, MessageHeading.class, id++, Side.CLIENT);
    }

}
