package silicongolems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import silicongolems.SiliconGolems;
import silicongolems.computer.Computer;
import silicongolems.computer.Computers;
import silicongolems.util.Util;

public abstract class MessageComputer extends MessageAuto {
    int computerID;
    public MessageComputer() {}
    public MessageComputer(Computer computer) {
        computerID = computer.id;
    }
    public boolean validateMessage(Computer computer, EntityPlayer player) {
        return computer.canUse(player);
    }
    public void runServer(MessageContext ctx, Computer computer) {}
    public void runClient(MessageContext ctx, Computer computer) {}

    @Override
    public void runClient(MessageContext ctx) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Computer computer = Computers.getOrCreate(computerID, player.world);
        runClient(ctx, computer);
    }

    @Override
    public void runServer(MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        Computer computer = Computers.getOrCreate(computerID, player.world);
        if (!validateMessage(computer, player)) {
            System.out.println("Invalid message from player " + player.getName() + ": "
                    + getClass().getSimpleName() + " " + Util.gson.toJson(this));
            return;
        }
        runServer(ctx, computer);
    }
}
