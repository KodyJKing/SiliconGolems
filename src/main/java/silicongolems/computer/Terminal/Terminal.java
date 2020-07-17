package silicongolems.computer.Terminal;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.computer.TextBuffer;
import silicongolems.network.ModPacketHandler;
import silicongolems.network.SiliconGolemsMessage;
import silicongolems.util.Util;

public class Terminal {
    public static final int WIDTH = 77;
    public static final int HEIGHT = 37;

    public int id;
    boolean isRemote;
    private EntityPlayerMP user;
    private boolean dirty = false;

    public State state;
    public static class State {
        public TextBuffer text;
        public int cursorX = 0;
        public int cursorY = 0;
    }

    Terminal(boolean isRemote, int id) {
        this.isRemote = isRemote;
        this.id = id;
        TerminalRegistry.addInstance(this);
        if (!isRemote) {
            this.state = new State();
            this.state.text = new TextBuffer(WIDTH, HEIGHT);
        }
    }

    Terminal(boolean isRemote) {
        this(isRemote, TerminalRegistry.idCounter++);
    }

    public Terminal() { this(false); }

    public void update() {
        if (dirty && user != null) {
            dirty = false;
            sendToUser(new CMUpdate(id, state));
        }
    }

    public void onDestroy() {
        TerminalRegistry.removeInstance(this);
    }

    public void input(char character, int keycode, boolean isDown, boolean isRepeat) {
        ModPacketHandler.INSTANCE.sendToServer(new SMInput(id, character, keycode, isDown, isRepeat));
    }

    public void onClientOpen() {
        ModPacketHandler.INSTANCE.sendToServer(new SMSetUser(id, true));
    }

    public void onClientClose() {
        TerminalRegistry.removeInstance(this);
        ModPacketHandler.INSTANCE.sendToServer(new SMSetUser(id, false));
    }

    // region api
    public String getLine(int y) {
        return state.text.getLine(y);
    }

    public void setLine(int y, String text) {
        state.text.setLine(y, text);
        if (!isRemote) dirty = true;
    }

    public int getShift() {
        return state.text.getShift();
    }

    public void setShift(int shift) {
        state.text.setShift(shift);
        if (!isRemote) dirty = true;
    }
    // endregion

    // region network
        private void sendToUser(IMessage message) {
            if (user != null)
                ModPacketHandler.INSTANCE.sendTo(message, user);
        }

        public static class CMUpdate extends SiliconGolemsMessage {
            int id; State state;
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
                Terminal terminal = TerminalRegistry.getInstance(Side.CLIENT, id);
                terminal.state = state;
            }
        }

        public static class SMSetUser extends SiliconGolemsMessage {
            int id; boolean use;
            public SMSetUser() {}
            public SMSetUser(int id, boolean use ) { this.id = id; this.use = use; }
            public void fromBytes(ByteBuf buf) { id = buf.readInt(); use = buf.readBoolean(); }
            public void toBytes(ByteBuf buf) { buf.writeInt(id); buf.writeBoolean(use); }

            public void runServer(MessageContext ctx) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Terminal terminal = TerminalRegistry.getInstance(Side.SERVER, id);
                if (terminal == null) return;
                if (use) {
                    terminal.user = player;
                    terminal.sendToUser(new CMUpdate(id, terminal.state));
                } else {
                    terminal.user = null;
                }
            }
        }

        public static class SMInput extends SiliconGolemsMessage {
            int id; char character; int keycode; boolean isDown, isRepeat;
            public SMInput() {}
            public SMInput(int id, char character, int keycode, boolean isDown, boolean isRepeat) {
                this.id = id;
                this.character = character;
                this.keycode = keycode;
                this.isDown = isDown;
                this.isRepeat = isRepeat;
            }
            public void fromBytes(ByteBuf buf) {
                id = buf.readInt();
                character = buf.readChar();
                keycode = buf.readInt();
                isDown = buf.readBoolean();
                isRepeat = buf.readBoolean();
            }
            public void toBytes(ByteBuf buf) {
                buf.writeInt(id);
                buf.writeChar(character);
                buf.writeInt(keycode);
                buf.writeBoolean(isDown);
                buf.writeBoolean(isRepeat);
            }

            public void runServer(MessageContext ctx) {
                System.out.println(Util.gson.toJson(this));
            }
        }

        public static void registerPackets() {
            ModPacketHandler.registerPacket(CMUpdate.class, Side.CLIENT);
            ModPacketHandler.registerPacket(SMInput.class, Side.SERVER);
            ModPacketHandler.registerPacket(SMSetUser.class, Side.SERVER);
        }
    // endregion

}