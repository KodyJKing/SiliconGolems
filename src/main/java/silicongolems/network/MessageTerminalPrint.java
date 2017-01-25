package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;

public class MessageTerminalPrint implements IMessage {

    int computerID;
    String line;

    public MessageTerminalPrint(){}

    public MessageTerminalPrint(Computer computer, String line){
        computerID = computer.id;
        this.line = line;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        computerID = buf.readInt();
        line = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(computerID);
        ByteBufUtils.writeUTF8String(buf, line);
    }

    public static class Handler implements IMessageHandler<MessageTerminalPrint, IMessage> {
        @Override
        public IMessage onMessage(MessageTerminalPrint message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                System.out.println("Printing on client: " + message.line);
                Computer computer = Computers.getOrCreate(message.computerID, Minecraft.getMinecraft().theWorld);
                computer.output.push(message.line);
            });

            return null;
        }
    }
}
