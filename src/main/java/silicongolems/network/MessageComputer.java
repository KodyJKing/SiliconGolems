package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
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

    public boolean validateMessage(Computer computer, EntityPlayer player){
        return computer.canUse(player);
    }

    public static abstract class Handler<T extends MessageComputer> implements IMessageHandler<T, IMessage> {
        @Override
        public IMessage onMessage(T message, MessageContext ctx) {
            if(ctx.side == Side.SERVER){
                EntityPlayer player = ctx.getServerHandler().player;
                Computer computer = Computers.getOrCreate(message.computerID, player.world);
                if(!message.validateMessage(computer, player))
                {
                    System.out.println("Invalid message from player " + player.getName() + ".");
                    return null;
                }
                doServer(message, ctx, computer);
            } else {
                onMessageClient(message, ctx);
            }

            return null;
        }

        @SideOnly(Side.CLIENT)
        public void onMessageClient(T message, MessageContext ctx){
            EntityPlayer player = Minecraft.getMinecraft().player;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Computer computer = Computers.getOrCreate(message.computerID, player.world);
                doClient(message, ctx, computer);
            });
        }

        public void doServer(T message, MessageContext ctx, Computer computer){}
        @SideOnly(Side.CLIENT)
        public void doClient(T message, MessageContext ctx, Computer computer){}
    }
}
