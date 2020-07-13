package silicongolems.computer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.gui.ModGuiHandler;
import silicongolems.network.MessageJSON;
import silicongolems.network.ModPacketHandler;
import silicongolems.network.SiliconGolemsMessage;
import silicongolems.util.SidedIntMaps;

public class Terminal {

    // region instances
    public static final SidedIntMaps<Terminal> INSTANCES = new SidedIntMaps<>();
    public static Terminal getInstance(Side side, int id) { return INSTANCES.get(side).get(id); }
    public static void addInstance(Terminal terminal) { INSTANCES.get(terminal.isRemote).put(terminal.id, terminal); }
    public static void removeInstance(Terminal terminal) { INSTANCES.get(terminal.isRemote).remove(terminal.id); }
    static int idCounter = 0;
    // endregion

    public static final int width = 77;
    public static final int height = 37;

    public int id;
    public boolean isRemote;
    public EntityPlayerMP user;

    public State state = new State();
    public static class State {
        public TextBuffer text;
        public int scrollX = 0;
        public int scrollY = 0;
        public int cursorX = 0;
        public int cursorY = 0;
    }

    public Terminal(boolean isRemote, int id) {
        this.isRemote = isRemote;
        this.id = id;
        addInstance(this);
        if (!isRemote)
            this.state.text = new TextBuffer(width, height).init();
    }

    public Terminal(boolean isRemote) {
        this(isRemote, idCounter++);
    }

    // region api
    public void clear() {
        state.text.clear();
    }

    public void print(String text) {
        state.text.print(text);
        if (!isRemote) sendToUser(new CMPrint(id, text).message());
    }

    public char charAt(int x, int y) {
        return state.text.charAt(x, y);
    }
    // endregion

    // region network
        public void openGUI(EntityPlayerMP player) {
            this.user = player;
            sendToUser(new CMCreateAndOpen(id, state));
        }

        private void sendToUser(IMessage message) {
            if (user != null)
                ModPacketHandler.INSTANCE.sendTo(message, user);
        }

        public static class CMCreateAndOpen extends SiliconGolemsMessage {
            int id;
            State state;
            public CMCreateAndOpen() {}
            public CMCreateAndOpen(int id, State state) { this.id = id; this.state = state; }

            public void fromBytes(ByteBuf buf) {
                id = buf.readInt();
                state = new State();
                state.scrollX = buf.readInt();
                state.scrollY = buf.readInt();
                state.cursorX = buf.readInt();
                state.cursorY = buf.readInt();
                state.text = new TextBuffer(width, height);
                state.text.fromBytes(buf);
            }

            public void toBytes(ByteBuf buf) {
                buf.writeInt(id);
                buf.writeInt(state.scrollX);
                buf.writeInt(state.scrollY);
                buf.writeInt(state.cursorX);
                buf.writeInt(state.cursorY);
                state.text.toBytes(buf);
            }

            public void runClient(MessageContext ctx) {
                Terminal terminal = new Terminal(true, id);
                terminal.state = state;
                EntityPlayer player = Minecraft.getMinecraft().player;
                ModGuiHandler.openTerminal(player, id);
            }
        }

        public static class CMPrint extends MessageJSON.Payload {
            int id;
            String text;
            public CMPrint() {}
            public CMPrint(int id, String text) { this.id = id; this.text = text; }
            public void runClient(MessageContext ctx) {
                Terminal terminal = getInstance(Side.CLIENT, id);
                if (terminal == null) return;
                terminal.state.text.print(text);
            }
        }

        public static void registerPackets() {
            ModPacketHandler.registerPacket(CMCreateAndOpen.class, Side.CLIENT);
            MessageJSON.registerMessage(CMPrint.class);
        }
    // endregion

}