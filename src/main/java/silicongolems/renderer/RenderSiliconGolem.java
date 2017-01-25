package silicongolems.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import silicongolems.SiliconGolems;
import silicongolems.entity.EntitySiliconGolem;
import silicongolems.model.ModelSiliconGolem;

public class RenderSiliconGolem extends RenderLiving<EntitySiliconGolem> {

    private static final ResourceLocation golemNeutral = new ResourceLocation(SiliconGolems.modId + ":" + "textures/models/silicon_golem_happy.png");

    public RenderSiliconGolem(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSiliconGolem(), 0.25F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySiliconGolem entity) {
        return golemNeutral;
    }
}
