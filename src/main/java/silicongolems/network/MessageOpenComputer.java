package silicongolems.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.computer.Computer;

import java.util.Stack;

public class MessageOpenComputer extends MessageComputer {
    Stack<String> output;
    public MessageOpenComputer() {}
    public MessageOpenComputer(Computer computer) {
        super(computer);
        output = computer.terminalOutput;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void runClient(MessageContext ctx, Computer computer) {
        computer.terminalOutput = output;
        computer.openComputerGui(Minecraft.getMinecraft().player);
    }
}
