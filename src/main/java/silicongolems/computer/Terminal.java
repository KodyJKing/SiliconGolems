package silicongolems.computer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.gui.ModGuiHandler;
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

    private boolean dirty = false;
    public State state = new State();
    public static class State {
        public TextBuffer text;
        public int cursorX = 0;
        public int cursorY = 0;
    }

    public Terminal(boolean isRemote, int id) {
        this.isRemote = isRemote;
        this.id = id;
        addInstance(this);
        if (!isRemote)
            this.state.text = new TextBuffer(width, height);
    }

    public Terminal(boolean isRemote) {
        this(isRemote, idCounter++);
    }

    public void update() {
        if (dirty) {
            dirty = false;
            sendToUser(new CMUpdate(id, state));
        }
    }

    // region API
    public String getLine(int y) {
        return state.text.getLine(y);
    }

    public void setLine(int y, String text) {
        state.text.setLine(y, text);
        if (!isRemote) dirty = true; //sendToUser(new CMUpdate(id, state));
    }

    public int getShift() {
        return state.text.getShift();
    }

    public void setShift(int shift) {
        state.text.setShift(shift);
        if (!isRemote) dirty = true; //sendToUser(new CMUpdate(id, state));
    }

    // endregion

    // region network
        public void openGUI(EntityPlayerMP player) {
            this.user = player;
            sendToUser(new CMOpen(id, state));
        }

        private void sendToUser(IMessage message) {
            if (user != null)
                ModPacketHandler.INSTANCE.sendTo(message, user);
        }

        public static class CMUpdate extends SiliconGolemsMessage {
            int id;
            State state;
            public CMUpdate() {}
            public CMUpdate(int id, State state) { this.id = id; this.state = state; }

            public void fromBytes(ByteBuf buf) {
                id = buf.readInt();
                state = new State();
                state.cursorX = buf.readInt();
                state.cursorY = buf.readInt();
                state.text = new TextBuffer();
                state.text.fromBytes(buf);
            }

            public void toBytes(ByteBuf buf) {
                buf.writeInt(id);
                buf.writeInt(state.cursorX);
                buf.writeInt(state.cursorY);
                state.text.toBytes(buf);
            }

            public void runClient(MessageContext ctx) {
                Terminal terminal;
                if (INSTANCES.get(Side.CLIENT).containsKey(id))
                    terminal = INSTANCES.get(Side.CLIENT).get(id);
                else
                    terminal = new Terminal(true, id);
                terminal.state = state;
            }
        }

        public static class CMOpen extends CMUpdate {
            public CMOpen() {}
            public CMOpen(int id, State state) { this.id = id; this.state = state; }
            public void runClient(MessageContext ctx) {
                super.runClient(ctx);
                EntityPlayer player = Minecraft.getMinecraft().player;
                ModGuiHandler.openTerminal(player, id);
            }
        }

//        public static class CMSetLine extends MessageJSON.Payload {
//            int id;
//            int y;
//            String text;
//            public CMSetLine() {}
//            public CMSetLine(int id, int y, String text) { this.id = id; this.y = y; this.text = text; }
//            public void runClient(MessageContext ctx) {
//                Terminal terminal = getInstance(Side.CLIENT, id);
//                if (terminal == null) return;
//                terminal.setLine(y, text);
//            }
//        }
//
//        public static class CMSetShift extends MessageJSON.Payload {
//            int id;
//            int shift;
//            public CMSetShift() {}
//            public CMSetShift(int id, int shift) { this.id = id; this.shift = shift; }
//            public void runClient(MessageContext ctx) {
//                Terminal terminal = getInstance(Side.CLIENT, id);
//                if (terminal == null) return;
//                terminal.setShift(shift);
//            }
//        }

        public static void registerPackets() {
            ModPacketHandler.registerPacket(CMUpdate.class, Side.CLIENT);
            ModPacketHandler.registerPacket(CMOpen.class, Side.CLIENT);
//            MessageJSON.registerMessage(CMSetLine.class);
//            MessageJSON.registerMessage(CMSetShift.class);
        }
    // endregion

}