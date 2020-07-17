package silicongolems.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.SiliconGolems;
import silicongolems.computer.Terminal.Terminal;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SiliconGolems.modId);

    private static int id;
    public static void registerPacket(Class clazz, Side side) {
        INSTANCE.registerMessage(SiliconGolemsMessage.Handler.class, clazz, id++, side);
    }
    public static void registerPacket(Class clazz) {
        INSTANCE.registerMessage(SiliconGolemsMessage.Handler.class, clazz, id++, Side.CLIENT);
        INSTANCE.registerMessage(SiliconGolemsMessage.Handler.class, clazz, id++, Side.SERVER);
    }

    public static void registerPackets() {
        registerPacket(MessageHeading.class, Side.CLIENT);
        registerPacket(MessageJSON.class);
        Terminal.registerPackets();
    }

}
