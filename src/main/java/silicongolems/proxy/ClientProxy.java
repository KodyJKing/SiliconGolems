package silicongolems.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import silicongolems.SiliconGolems;
import silicongolems.computer.Computer;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.renderer.RenderSiliconGolem;

public class ClientProxy extends CommonProxy {

    @Override
    public Side side() {
        return Side.CLIENT;
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String name) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(SiliconGolems.modId + ":" + name, "inventory"));
    }

    @Override
    public void ignoreProperty(Block block, IProperty property) {
        ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(property).build());
    }

    @Override
    public void registerEntityRendering() {
        RenderingRegistry.registerEntityRenderingHandler(EntitySiliconGolem.class, new IRenderFactory<EntitySiliconGolem>() {
            @Override
            public Render<? super EntitySiliconGolem> createRenderFor (RenderManager manager) {
                return new RenderSiliconGolem(manager);
            }
        });
    }
}
