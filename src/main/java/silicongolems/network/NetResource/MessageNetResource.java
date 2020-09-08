//package silicongolems.network.NetResource;
//
//import io.netty.buffer.ByteBuf;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.fml.common.network.ByteBufUtils;
//import silicongolems.network.SiliconGolemsMessage;
//
//public class MessageNetResource extends SiliconGolemsMessage {
//    int id;
//    int type;
//    NBTTagCompound nbt;
//    public MessageNetResource() {}
//    public MessageNetResource(int id, int type, NBTTagCompound nbt) {
//        this.id = id;
//        this.type = type;
//        this.nbt = nbt;
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        id = buf.readInt();
//        type = buf.readInt();
//        nbt = ByteBufUtils.readTag(buf);
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeInt(id);
//        buf.writeInt(type);
//        nbt = new NBTTagCompound();
//        ByteBufUtils.writeTag(buf, nbt);
//    }
//
//    // TODO: Simplify net resource packets
//}
