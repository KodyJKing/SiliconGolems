package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;

import java.util.Stack;

public class MessageOpenCloseTerminal extends MessageComputer {

    Stack<String> output;

    public MessageOpenCloseTerminal(){}

    public MessageOpenCloseTerminal(Computer computer){
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

    public static class Handler extends MessageComputer.Handler<MessageOpenCloseTerminal> {
        @Override
        public void doClient(MessageOpenCloseTerminal message, MessageContext ctx, Computer computer) {
            computer.output = message.output;
            computer.openTerminalGui(Minecraft.getMinecraft().thePlayer);
        }

        @Override
        public void doServer(MessageOpenCloseTerminal message, MessageContext ctx, Computer computer) {
            computer.user = null;
        }
    }
}
