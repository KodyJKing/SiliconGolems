package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageHeading extends SiliconGolemsMessage {

    int entityId;
    float yaw, pitch, headYaw;

    public MessageHeading() {}

    public MessageHeading(EntityLivingBase entity) {
        entityId = entity.getEntityId();
        yaw = entity.rotationYaw;
        pitch = entity.rotationPitch;
        headYaw = entity.rotationYawHead;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        headYaw = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeFloat(headYaw);
    }

    @Override
    public void runClient(MessageContext ctx) {
        EntityLivingBase entity = (EntityLivingBase) Minecraft.getMinecraft().world.getEntityByID(entityId);
        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;
        entity.rotationYawHead = headYaw;
    }
}
