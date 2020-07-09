package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.util.Util;

public class MessageAuto implements IMessage {

    protected MessageAuto deserialized;

    public MessageAuto() {}

    // Ugh, using the deserialized field is a hack to avoid having to copy deserialized fields into the message.
    @Override
    public void fromBytes(ByteBuf buf) {
        String json = ByteBufUtils.readUTF8String(buf);
        deserialized = Util.gson.fromJson(json, this.getClass());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String json = Util.gson.toJson(this, this.getClass());
        ByteBufUtils.writeUTF8String(buf, json);
    }

    @SideOnly(Side.CLIENT)
    public void runClient(MessageContext ctx) {}

    public void runServer(MessageContext ctx) {}

    public static IThreadListener getThreadListener(MessageContext ctx) {
        if (ctx.side == Side.SERVER)
            return (WorldServer) ctx.getServerHandler().player.world;
        else
            return Minecraft.getMinecraft();
    }

    public static class Handler implements IMessageHandler<MessageAuto, IMessage> {
        @Override
        public IMessage onMessage(MessageAuto message, MessageContext ctx) {
            getThreadListener(ctx).addScheduledTask(() -> {
                if (ctx.side == Side.CLIENT)
                    message.deserialized.runClient(ctx);
                else
                    message.deserialized.runServer(ctx);
            });
            return null;
        }
    }

}
