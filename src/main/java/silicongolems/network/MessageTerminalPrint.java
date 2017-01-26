package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;

public class MessageTerminalPrint extends MessageComputer{
    String line;

    public MessageTerminalPrint(){}

    public MessageTerminalPrint(Computer computer, String line){
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

    public static class Handler extends MessageComputer.Handler<MessageTerminalPrint> {
        @Override
        public void doClient(MessageTerminalPrint message, MessageContext ctx, Computer computer) {
            computer.printLocal(message.line);
        }
    }
}
