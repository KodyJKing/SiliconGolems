package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.util.Util;

import java.util.ArrayList;

public class MessageJSON extends SiliconGolemsMessage {

    private static ArrayList<Class<? extends Payload>> types = new ArrayList<>();
    public static void registerMessage(Class<? extends Payload> type) { types.add(type); }

    public static class Payload {
        public void runServer(MessageContext ctx) {}
        public void runClient(MessageContext ctx) {}
        public MessageJSON message() { return new MessageJSON(this); }
    }

    public MessageJSON() {}
    public MessageJSON(Payload payload) { this.payload = payload; }

    Payload payload;

    @Override
    public void fromBytes(ByteBuf buf) {
        Class<? extends Payload> clazz = types.get(buf.readInt());
        payload = Util.gson.fromJson(ByteBufUtils.readUTF8String(buf), clazz);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int typeId = types.indexOf(payload.getClass());
        buf.writeInt(typeId);
        ByteBufUtils.writeUTF8String(buf, Util.gson.toJson(payload));
    }

    @Override
    public void runClient(MessageContext ctx) {
        payload.runClient(ctx);
    }

    @Override
    public void runServer(MessageContext ctx) {
        payload.runServer(ctx);
    }
}
