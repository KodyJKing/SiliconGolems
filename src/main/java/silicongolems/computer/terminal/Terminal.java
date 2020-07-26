package silicongolems.computer.terminal;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.graalvm.polyglot.HostAccess;
import silicongolems.computer.Computer;
import silicongolems.computer.TextBuffer;
import silicongolems.computer.Event;
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
    private Computer computer;

    private State state;
    public static class State {
        public TextBuffer text;
        public int cursorX = 0;
        public int cursorY = 0;
        public void toBytes(ByteBuf buf) {
            buf.writeInt(cursorX);
            buf.writeInt(cursorY);
            text.toBytes(buf);
        }
        public void fromBytes(ByteBuf buf) {
            cursorX = buf.readInt();
            cursorY = buf.readInt();
            text = new TextBuffer();
            text.fromBytes(buf);
        }
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

    public Terminal(Computer computer) {
        this(false);
        this.computer = computer;
    }

    public void update() {
        if (dirty && user != null) {
            dirty = false;
            sendToUser(new CMUpdate(id, state));
        }
    }

    public void onDestroy() {
        TerminalRegistry.removeInstance(this);
    }

    public void input(KeyboardEvent event) {
        ModPacketHandler.INSTANCE.sendToServer(new SMInput(id, event));
    }

    public boolean isLoaded() { return state != null; }

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
            public void toBytes(ByteBuf buf) {
                buf.writeInt(id);
                state.toBytes(buf);
            }
            public void fromBytes(ByteBuf buf) {
                id = buf.readInt();
                state = new State();
                state.fromBytes(buf);
            }

            public void runClient(MessageContext ctx) {
                Terminal terminal = TerminalRegistry.getInstance(Side.CLIENT, id);
                if (terminal != null) terminal.state = state;
            }
        }

        public static class SMSetUser extends SiliconGolemsMessage {
            int id; boolean use;
            public SMSetUser() {}
            public SMSetUser(int id, boolean use ) { this.id = id; this.use = use; }
            public void toBytes(ByteBuf buf) { buf.writeInt(id); buf.writeBoolean(use); }
            public void fromBytes(ByteBuf buf) { id = buf.readInt(); use = buf.readBoolean(); }

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
            int id;
            KeyboardEvent event;
            public SMInput() {}
            public SMInput(int id, KeyboardEvent event) { this.id = id; this.event = event; }
            public void toBytes(ByteBuf buf) {
                buf.writeInt(id);
                event.toBytes(buf); }
            public void fromBytes(ByteBuf buf) {
                id = buf.readInt();
                event = new KeyboardEvent();
                event.fromBytes(buf);
            }

            public void runServer(MessageContext ctx) {
//                System.out.println(Util.gson.toJson(this));
                Terminal terminal = TerminalRegistry.getInstance(Side.SERVER, id);
                if (terminal != null)
                    terminal.computer.queueEvent(event);
            }
        }

        public static class KeyboardEvent extends Event {
            @HostAccess.Export public char character;
            @HostAccess.Export public int keycode;
            @HostAccess.Export public boolean isDown;
            @HostAccess.Export public boolean isRepeat;
            public KeyboardEvent() { super("keyboard"); }
            public void toBytes(ByteBuf buf) {
                buf.writeChar(character);
                buf.writeInt(keycode);
                buf.writeBoolean(isDown);
                buf.writeBoolean(isRepeat);
            }
            public void fromBytes(ByteBuf buf) {
                character = buf.readChar();
                keycode = buf.readInt();
                isDown = buf.readBoolean();
                isRepeat = buf.readBoolean();
            }
        }

        public static void registerPackets() {
            System.out.println("Registering CMUpdate");
            ModPacketHandler.registerPacket(CMUpdate.class, Side.CLIENT);
            System.out.println("Registering SMInput");
            ModPacketHandler.registerPacket(SMInput.class, Side.SERVER);
            System.out.println("Registering SMSetUser");
            ModPacketHandler.registerPacket(SMSetUser.class, Side.SERVER);
        }
    // endregion

}