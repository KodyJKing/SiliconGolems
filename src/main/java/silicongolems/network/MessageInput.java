package silicongolems.network;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

public class MessageInput extends MessageComputer {
    String command;
    public MessageInput() {}
    public MessageInput(Computer computer, String command) {
        super(computer);
        this.command = command;
    }

    @Override
    public void runServer(MessageContext ctx, Computer computer) {
        computer.onInput(command);
    }
}
