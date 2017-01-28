package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;

public class MessageByte extends MessageComputer{

    public static final byte TERMINATE = 0;
    public static final byte CLEAR_SCREEN = 1;
    public static final byte CLOSE_COMPUTER = 2;

    byte msg;

    public MessageByte(){}

    public MessageByte(Computer computer, byte msg){
        super(computer);
        this.msg = msg;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        msg = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(msg);
    }

    public static class Handler extends MessageComputer.Handler<MessageByte> {
        @Override
        public void doServer(MessageByte message, MessageContext ctx, Computer computer) {
            switch (message.msg){
                case TERMINATE:
                    computer.killProcess();
                    return;
                case CLOSE_COMPUTER:
                    computer.user = null;
            }
        }

        @Override
        public void doClient(MessageByte message, MessageContext ctx, Computer computer) {
            switch (message.msg){
                case CLEAR_SCREEN:
                    computer.terminalOutput.clear();
                    return;
                case CLOSE_COMPUTER:
                    Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
    }
}
