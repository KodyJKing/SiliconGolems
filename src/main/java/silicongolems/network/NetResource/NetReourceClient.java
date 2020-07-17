package silicongolems.network.NetResource;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.network.MessageJSON;

import java.util.HashMap;

public class NetReourceClient {

    public static final int SUBSCRIBE = 0;
    public static final int UNSUBSCRIBE = 0;

    private static HashMap<Integer, Entry> entries = new HashMap<>();
    private static class Entry {
        int id;
        Runnable callback;
        INetResource resource;
        public Entry(int id, Runnable callback) {
            this.id = id;
            this.callback = callback;
        }
        public void resolve(INetResource resource) {
            this.resource = resource;
            runCallback();
        }
        public void runCallback() {
            if (callback != null)
                callback.run();
        }
    }

    public static void subscribe(int id, Runnable callback) {
        Entry entry = entries.get(id);
        if (entry != null) {
            System.out.println("Warning: duplicate subscription " + id);
            entry.callback = callback;
            if (entry.resource == null)
                entry.runCallback();
        } else {
            entries.put(id, new Entry(id, callback));
        }
    }

    public static void onCreate(int id, INetResource resource) {
        Entry entry = entries.get(id);
        if (entry == null) {
            System.out.println("Received unwanted update.");
            return;
        }
        entry.resource = resource;
        entry.callback.run();
    }

    public static void unsubscribe() {
    }

    public class Message extends MessageJSON.Payload {
        int id;
        int type;
        public Message() {}
        public Message(int id, int type) { this.id = id; this.type = type; }
        public void runServer(MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (type == SUBSCRIBE) {
                NetResourceServer.subscribe(player, id);
            }
        }
    }

}
