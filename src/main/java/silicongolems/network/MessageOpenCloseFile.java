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

public class MessageOpenCloseFile extends MessageComputer {

    String file;

    public MessageOpenCloseFile(){}

    public MessageOpenCloseFile(Computer computer) {
        super(computer);
        file = computer.activeFile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        file = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, file);
    }

    public static class Handler extends MessageComputer.Handler<MessageOpenCloseFile> {
        @Override
        public void doServer(MessageOpenCloseFile message, MessageContext ctx, Computer computer) {
            computer.activeFile = message.file;
        }

        @Override
        public void doClient(MessageOpenCloseFile message, MessageContext ctx, Computer computer) {
            computer.activeFile = message.file;
            computer.openEditorGui(Minecraft.getMinecraft().thePlayer);
        }
    }
}
