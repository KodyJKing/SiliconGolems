package silicongolems;

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

@Mod(modid = SiliconGolems.modId, version = SiliconGolems.version, acceptedMinecraftVersions = "[1.10.2]")
public class SiliconGolems
{
    public static final String modId = "silicongolems";
    public static final String name = "Silicon Golems";
    public static final String version = "1.0.0";

    @SidedProxy(clientSide = "silicongolems.proxy.ClientProxy", serverSide = "silicongolems.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(modId)
    public static SiliconGolems instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(name + " is loading!");
        ModItems.init();
        EntityRegistry.registerModEntity(EntitySiliconGolem.class, "siliconGolem", 0, instance, 80, 1, true, 0xFFFFCC, 0xCCCCA3);
        proxy.registerEntityRendering();
        ModPacketHandler.registerPackets();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ModGuiHandler());
    }

    /*
     * TODO:
     *
     * Priority 1:
     * Move all gui's into one tabbed GuiScreenHost or GuiScreenOS.
     * Clean up transition from editor screen to terminal. No packets should be sent.
     *
     * Priority 2:
     * Implement a file system.
     * Implement more os commands, possible move command handling into another class and 'OOPify' commands.
     *
     * Priority 3:
     * Consider implementing a container so the server knows when the gui is open and closed.
     * Consider text editor improvments: auto-indent, copy, mouse select, mouse drag.
     */
}
