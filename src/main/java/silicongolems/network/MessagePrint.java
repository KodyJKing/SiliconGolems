package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

public class MessagePrint extends MessageComputer{
    String line;

    public MessagePrint() {}

    public MessagePrint(Computer computer, String line) {
        super(computer);
        this.line = line;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        line = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, line);
    }

    public static class Handler extends MessageComputer.Handler<MessagePrint> {
        @Override
        public void doClient(MessagePrint message, MessageContext ctx, Computer computer) {
            computer.printLocal(message.line);
        }
    }
}
