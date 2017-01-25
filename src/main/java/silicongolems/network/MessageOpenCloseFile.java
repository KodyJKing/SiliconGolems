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

public class MessageOpenCloseFile implements IMessage {

    int computerID;
    String file;

    public MessageOpenCloseFile(){}

    public MessageOpenCloseFile(Computer computer) {
        computerID = computer.id;
        file = computer.activeFile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        computerID = ByteBufUtils.readVarInt(buf, 4);
        file = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, computerID, 4);
        ByteBufUtils.writeUTF8String(buf, file);
    }

    public static class Handler implements IMessageHandler<MessageOpenCloseFile, IMessage> {
        @Override
        public IMessage onMessage(MessageOpenCloseFile message, MessageContext ctx) {

            if(ctx.side == Side.SERVER){
                Computer computer = Computers.getOrCreate(message.computerID, ctx.getServerHandler().playerEntity.worldObj);
                computer.activeFile = message.file;
            } else {
                onMessageClient(message);
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void onMessageClient(MessageOpenCloseFile message){
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Computer computer = Computers.getOrCreate(message.computerID, player.worldObj);
            computer.activeFile = message.file;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                computer.openEditorGui(player);
            });
        }
    }
}
