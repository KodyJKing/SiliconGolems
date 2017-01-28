package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.computer.Computer;
import silicongolems.gui.GuiScreenOS;
import silicongolems.gui.window.WindowEditor;

public class MessageOpenCloseFile extends MessageComputer {

    String path, text;

    public MessageOpenCloseFile(){}

    public MessageOpenCloseFile(Computer computer, String path, String text) {
        super(computer);
        this.path = path;
        this.text = text;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        path = ByteBufUtils.readUTF8String(buf);
        text = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, path);
        ByteBufUtils.writeUTF8String(buf, text);
    }

    public static class Handler extends MessageComputer.Handler<MessageOpenCloseFile> {
        @Override
        @SideOnly(Side.CLIENT)
        public void doClient(MessageOpenCloseFile message, MessageContext ctx, Computer computer) {
            GuiScreenOS gui = (GuiScreenOS) Minecraft.getMinecraft().currentScreen;
            gui.editor = new WindowEditor(computer, gui, message.path, message.text);
        }

        @Override
        public void doServer(MessageOpenCloseFile message, MessageContext ctx, Computer computer) {
            computer.writeFile(message.path, message.text);
        }
    }
}
