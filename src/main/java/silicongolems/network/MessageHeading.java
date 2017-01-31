package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHeading implements IMessage {

    int entityId;
    float yaw, pitch, headPitch, headYaw;

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

    public static class Handler implements IMessageHandler<MessageHeading, IMessage>{
        @Override
        public IMessage onMessage(MessageHeading message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityLivingBase entity = (EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
                entity.rotationYaw = message.yaw;
                entity.rotationPitch = message.pitch;
                entity.rotationYawHead = message.headYaw;
            });
            return null;
        }
    }
}
