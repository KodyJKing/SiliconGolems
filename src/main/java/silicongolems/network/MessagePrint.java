package silicongolems.network;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.computer.Computer;

public class MessagePrint extends MessageComputer {
    String line;
    public MessagePrint() {}
    public MessagePrint(Computer computer, String line) {
        super(computer);
        this.line = line;
    }

    @Override
    public void runClient(MessageContext ctx, Computer computer) {
        computer.printLocal(line);
    }
}
