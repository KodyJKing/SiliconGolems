package silicongolems;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.gui.ModGuiHandler;
import silicongolems.item.ModItems;
import silicongolems.network.ModPacketHandler;
import silicongolems.proxy.CommonProxy;

import javax.script.ScriptEngineManager;

@Mod(modid = SiliconGolems.modId, version = SiliconGolems.version, acceptedMinecraftVersions = "[1.12.2]", name = "Silicon Golems")
public class SiliconGolems
{
    public static final String modId = "silicongolems";
    public static final String name = "Silicon Golems";
    public static final String version = "1.0.0";

    @SidedProxy(clientSide = "silicongolems.proxy.ClientProxy", serverSide = "silicongolems.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(modId)
    public static SiliconGolems instance;

    public static boolean devEnv = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(name + " is loading!");
        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))
            devEnv = true;

        MinecraftForge.EVENT_BUS.register(new ModItems());

        EntityRegistry.registerModEntity(
                new ResourceLocation(modId, "silicongolem"),
                EntitySiliconGolem.class, modId + ".silicongolem",
                0, this, 100, 1,
                true,
                0xFFFFCC, 0xCCCCA3);
        proxy.registerEntityRendering();
        ModPacketHandler.registerPackets();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ModGuiHandler());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
