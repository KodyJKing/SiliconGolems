package silicongolems.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.SiliconGolems;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SiliconGolems.modId);

    private static int id;
    public static void registerPacket(Class clazz, Side side) {
        INSTANCE.registerMessage(MessageAuto.Handler.class, clazz, id++, side);
    }
    public static void registerPacket(Class clazz) {
        INSTANCE.registerMessage(MessageAuto.Handler.class, clazz, id++, Side.CLIENT);
        INSTANCE.registerMessage(MessageAuto.Handler.class, clazz, id++, Side.SERVER);
    }

    public static void registerPackets() {
        registerPacket(MessageOpenCloseFile.class);
        registerPacket(MessageOpenComputer.class);
        registerPacket(MessageInput.class, Side.SERVER);
        registerPacket(MessagePrint.class, Side.CLIENT);
        registerPacket(MessageByte.class);
        INSTANCE.registerMessage(MessageHeading.Handler.class, MessageHeading.class, id++, Side.CLIENT);
    }

}
