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
    public MessageOpenCloseFile() {}
    public MessageOpenCloseFile(Computer computer, String path, String text) {
        super(computer);
        this.path = path;
        this.text = text;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void runClient(MessageContext ctx, Computer computer) {
        GuiScreenOS gui = (GuiScreenOS) Minecraft.getMinecraft().currentScreen;
        gui.editor = new WindowEditor(computer, gui, path, text);
    }

    @Override
    public void runServer(MessageContext ctx, Computer computer) {
        computer.writeFile(path, text);
    }
}
