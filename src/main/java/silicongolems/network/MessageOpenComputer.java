package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.computer.Computer;

import java.util.Stack;

public class MessageOpenComputer extends MessageComputer {

    Stack<String> output;

    public MessageOpenComputer() {
    }

    public MessageOpenComputer(Computer computer) {
        super(computer);
        output = computer.terminalOutput;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        output = new Stack<String>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
            output.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(output.size());
        for (String line : output)
            ByteBufUtils.writeUTF8String(buf, line);
    }

    public static class Handler extends MessageComputer.Handler<MessageOpenComputer> {
        @Override
        @SideOnly(Side.CLIENT)
        public void doClient(MessageOpenComputer message, MessageContext ctx, Computer computer) {
            computer.terminalOutput = message.output;
            computer.openComputerGui(Minecraft.getMinecraft().player);
        }

        @Override
        public void doServer(MessageOpenComputer message, MessageContext ctx, Computer computer) {
            // TODO: Monitor this change for side effects.
            // computer.user = null;
        }
    }
}
