package silicongolems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class SiliconGolemsMessage implements IMessage {

    public void runClient(MessageContext ctx) {}

    public void runServer(MessageContext ctx) {}

    public static IThreadListener getThreadListener(MessageContext ctx) {
        if (ctx.side == Side.SERVER)
            return (WorldServer) ctx.getServerHandler().player.world;
        else
            return Minecraft.getMinecraft();
    }

    public static class Handler implements IMessageHandler<SiliconGolemsMessage, IMessage> {
        @Override
        public IMessage onMessage(SiliconGolemsMessage message, MessageContext ctx) {
            getThreadListener(ctx).addScheduledTask(() -> {
                if (ctx.side == Side.CLIENT)
                    message.runClient(ctx);
                else
                    message.runServer(ctx);
            });
            return null;
        }
    }

}
