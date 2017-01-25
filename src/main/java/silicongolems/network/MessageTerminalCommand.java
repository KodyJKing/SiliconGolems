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

public class MessageTerminalCommand implements IMessage {

    int computerID;
    String command;

    public MessageTerminalCommand(){}

    public MessageTerminalCommand(Computer computer, String command){
        computerID = computer.id;
        this.command = command;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        computerID = buf.readInt();
        command = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(computerID);
        ByteBufUtils.writeUTF8String(buf, command);
    }

    public static class Handler implements IMessageHandler<MessageTerminalCommand, IMessage>{
        @Override
        public IMessage onMessage(MessageTerminalCommand message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            Computer computer = Computers.getOrCreate(message.computerID, player.worldObj);
            computer.executeCommand(message.command);
            return null;
        }
    }
}
