package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

public class MessageByte extends MessageComputer{

    public static final byte TERMINATE = 0;
    public static final byte CLEAR_SCREEN = 1;

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
            }
        }

        @Override
        public void doClient(MessageByte message, MessageContext ctx, Computer computer) {
            switch (message.msg){
                case CLEAR_SCREEN:
                    computer.terminalOutput.clear();
            }
        }
    }
}
