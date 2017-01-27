package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

import java.util.Stack;

public class MessageOpenCloseOS extends MessageComputer {

    Stack<String> output;

    public MessageOpenCloseOS(){}

    public MessageOpenCloseOS(Computer computer){
        super(computer);
        output = computer.output;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        output = new Stack<String>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++)
            output.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(output.size());
        for(String line : output)
            ByteBufUtils.writeUTF8String(buf, line);
    }

    public static class Handler extends MessageComputer.Handler<MessageOpenCloseOS> {
        @Override
        public void doClient(MessageOpenCloseOS message, MessageContext ctx, Computer computer) {
            computer.output = message.output;
            computer.openOSGui(Minecraft.getMinecraft().thePlayer);
        }

        @Override
        public void doServer(MessageOpenCloseOS message, MessageContext ctx, Computer computer) {
            computer.user = null;
        }
    }
}
