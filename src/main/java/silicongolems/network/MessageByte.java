package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

public class MessageByte extends MessageComputer{
    public static final byte TERMINATE = 0;
    public static final byte CLEAR_SCREEN = 1;
    public static final byte CLOSE_COMPUTER = 2;
    byte msg;
    public MessageByte() {}
    public MessageByte(Computer computer, byte msg) {
        super(computer);
        this.msg = msg;
    }

    @Override
    public boolean validateMessage(Computer computer, EntityPlayer player) {
        if (msg == CLOSE_COMPUTER)
            return true;
        return super.validateMessage(computer, player);
    }

    @Override
    public void runServer(MessageContext ctx, Computer computer) {
        switch (msg) {
            case TERMINATE:
                computer.killProcess();
                return;
            case CLOSE_COMPUTER:
                computer.user = null;
        }
    }

    @Override
    public void runClient(MessageContext ctx, Computer computer) {
        switch (msg) {
            case CLEAR_SCREEN:
                computer.terminalOutput.clear();
                return;
            case CLOSE_COMPUTER:
                Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }
}
