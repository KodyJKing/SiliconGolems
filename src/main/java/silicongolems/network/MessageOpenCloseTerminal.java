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

public class MessageOpenCloseTerminal implements IMessage {

    int computerID;
    Stack<String> output;

    public MessageOpenCloseTerminal(){}

    public MessageOpenCloseTerminal(Computer computer){
        computerID = computer.id;
        output = computer.output;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        computerID = buf.readInt();

        output = new Stack<String>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++)
            output.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(computerID);

        buf.writeInt(output.size());
        for(String line : output)
            ByteBufUtils.writeUTF8String(buf, line);
    }

    public static class Handler implements IMessageHandler<MessageOpenCloseTerminal, IMessage> {
        @Override
        public IMessage onMessage(MessageOpenCloseTerminal message, MessageContext ctx) {
            if(ctx.side == Side.CLIENT){
                onMessageClient(message);
            } else {
                Computer computer = Computers.getOrCreate(message.computerID, ctx.getServerHandler().playerEntity.worldObj);
                computer.user = null;
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        public static void onMessageClient(MessageOpenCloseTerminal message) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Computer computer = Computers.getOrCreate(message.computerID, player.worldObj);
                computer.openTerminalGui(player);
            });
        }
    }
}
