package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;

public class MessageTerminalCommand extends MessageComputer {
    
    String command;

    public MessageTerminalCommand(){}

    public MessageTerminalCommand(Computer computer, String command){
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

    public static class Handler extends MessageComputer.Handler<MessageTerminalCommand>{
        @Override
        public void doServer(MessageTerminalCommand message, MessageContext ctx, Computer computer) {
            computer.executeCommand(message.command);
        }
    }
}
