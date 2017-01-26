package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

public class MessageCommand extends MessageComputer {
    
    String command;

    public MessageCommand(){}

    public MessageCommand(Computer computer, String command){
        super(computer);
        this.command = command;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        command = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, command);
    }

    public static class Handler extends MessageComputer.Handler<MessageCommand>{
        @Override
        public void doServer(MessageCommand message, MessageContext ctx, Computer computer) {
            computer.executeCommand(message.command);
        }
    }
}
