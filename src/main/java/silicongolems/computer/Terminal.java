package silicongolems.computer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.gui.ModGuiHandler;
import silicongolems.network.ModPacketHandler;
import silicongolems.network.SiliconGolemsMessage;
import silicongolems.util.SidedIntMaps;
import silicongolems.util.Util;

public class Terminal {

    // region instances
    public static final SidedIntMaps<Terminal> INSTANCES = new SidedIntMaps<>();
    public static Terminal getInstance(Side side, int id) { return INSTANCES.get(side).get(id); }
    public static void addInstance(Terminal terminal) { INSTANCES.get(terminal.isRemote).put(terminal.id, terminal); }
    public static void removeInstance(Terminal terminal) { INSTANCES.get(terminal.isRemote).remove(terminal.id); }
    static int idCounter = 0;
    // endregion

    public static final int WIDTH = 77;
    public static final int HEIGHT = 37;

    public int id;
    public boolean isRemote;
    public EntityPlayerMP user;
    public Computer computer;
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
            this.state.text = new TextBuffer(WIDTH, HEIGHT);
    }

    public Terminal(boolean isRemote) {
        this(isRemote, idCounter++);
    }

    public void update() {
        if (dirty && user != null) {
            dirty = false;
            sendToUser(new ClientMessageUpdate(id, state));
        }
    }

    public void onDestroy() {
        removeInstance(this);
    }

    public void input(char character, int keycode, boolean isDown, boolean isRepeat) {
        ModPacketHandler.INSTANCE.sendToServer(new ServerMessageInputEvent(id, character, keycode, isDown, isRepeat));
    }

    public void openGUI(EntityPlayerMP player) {
        this.user = player;
        sendToUser(new ClientMessageOpen(id, state));
    }

    // region computer-api
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

        public static class ClientMessageUpdate extends SiliconGolemsMessage {
            int id;
            State state;
            public ClientMessageUpdate() {}
            public ClientMessageUpdate(int id, State state) { this.id = id; this.state = state; }

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

        public static class ClientMessageOpen extends ClientMessageUpdate {
            public ClientMessageOpen() {}
            public ClientMessageOpen(int id, State state) { this.id = id; this.state = state; }
            @SideOnly(Side.CLIENT)
            public void runClient(MessageContext ctx) {
                super.runClient(ctx);
                EntityPlayer player = Minecraft.getMinecraft().player;
                ModGuiHandler.openTerminal(player, id);
            }
        }

        public static class ServerMessageInputEvent extends SiliconGolemsMessage {
            int id;
            char character;
            int keycode;
            boolean isDown, isRepeat;
            public ServerMessageInputEvent() {}
            public ServerMessageInputEvent(int id, char character, int keycode, boolean isDown, boolean isRepeat) {
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
            ModPacketHandler.registerPacket(ClientMessageUpdate.class, Side.CLIENT);
            ModPacketHandler.registerPacket(ClientMessageOpen.class, Side.CLIENT);
            ModPacketHandler.registerPacket(ServerMessageInputEvent.class, Side.SERVER);
        }
    // endregion

}