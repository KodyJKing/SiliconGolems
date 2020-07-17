package silicongolems.network.NetResource;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import silicongolems.network.ModPacketHandler;
import silicongolems.network.SiliconGolemsMessage;

import java.util.HashMap;
import java.util.HashSet;

public class NetResourceServer {

    private static int idCounter = 0;
    private static int getId() { return idCounter++; }

    private static HashMap<Integer, Entry> entries = new HashMap<>();
    private static class Entry {
        INetResource resource;
        HashSet<EntityPlayerMP> subscribers = new HashSet<>();
        public Entry(INetResource resource) {
            this.resource = resource;
        }
    }

    public static void addResource(INetResource resource) {
        int id = getId();
        resource.setId(id);
        entries.put(id, new Entry(resource));
    }

    public static void subscribe(EntityPlayerMP player, int id) {
        Entry entry = entries.get(id);
        if (entry == null) return;
        entry.subscribers.add(player);
        ModPacketHandler.INSTANCE.sendTo(new MessageCreate(id, entry.resource), player);
    }

    public static class MessageCreate extends SiliconGolemsMessage {
        int id;
        INetResource resource;
        public MessageCreate() {}
        public MessageCreate(int id, INetResource resource) { this.id = id; this.resource = resource; }
        public void fromBytes(ByteBuf buf) {
            id = buf.readInt();
            int typeId = buf.readInt();
            Class type = NetResourceTypeRegistry.getClass(typeId);
            try {
                Object _resource = type.newInstance();
                resource = (INetResource) _resource;
                if (resource != null)
                    resource.fromBytes(buf);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public void toBytes(ByteBuf buf) {
            buf.writeInt(id);
            int typeId = NetResourceTypeRegistry.classId(resource.getClass());
            buf.writeInt(typeId);
            resource.toBytes(buf);
        }

        public void runClient(MessageContext ctx) {
            super.runClient(ctx);
        }
    }

}
