package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;

public abstract class MessageComputer implements IMessage{

    int computerID;

    public MessageComputer() {}

    public MessageComputer(Computer computer){
        computerID = computer.id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        computerID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(computerID);
    }

    public static abstract class Handler implements IMessageHandler<MessageComputer, IMessage> {
        @Override
        public IMessage onMessage(MessageComputer message, MessageContext ctx) {
            if(ctx.side == Side.SERVER){
                Computer computer = Computers.getOrCreate(message.computerID, ctx.getServerHandler().playerEntity.worldObj);
                doServer(computer);
            } else {
                onMessageClient(message, ctx);
            }

            return null;
        }

        @SideOnly(Side.CLIENT)
        public void onMessageClient(MessageComputer message, MessageContext ctx){
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Computer computer = Computers.getOrCreate(message.computerID, player.worldObj);
                doClient(computer);
            });
        }

        @SideOnly(Side.SERVER)
        public abstract void doServer(Computer computer);
        @SideOnly(Side.CLIENT)
        public abstract void doClient(Computer computer);
    }
}
