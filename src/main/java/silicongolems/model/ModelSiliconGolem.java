package silicongolems.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import silicongolems.entity.EntitySiliconGolem;

public class ModelSiliconGolem extends ModelBase {

    ModelRenderer crt;
    ModelRenderer bipedBody;
    ModelRenderer bipedRightArm;
    ModelRenderer bipedLeftArm;
    ModelRenderer bipedRightLeg;
    ModelRenderer bipedLeftLeg;
    ModelRenderer waist;

    public ModelSiliconGolem()
    {
        textureWidth = 128;
        textureHeight = 128;

        crt = new ModelRenderer(this, 0, 82);
        crt.addBox(-6F, 0F, 0F, 12, 8, 7);
        crt.setRotationPoint(0F, -5.533333F, 6F);
        crt.setTextureSize(128, 128);
        crt.mirror = true;

        bipedBody = new ModelRenderer(this, 0, 40);
        bipedBody.addBox(-9F, 0F, -5F, 18, 12, 11);
        bipedBody.setRotationPoint(0F, -8F, 2F);
        bipedBody.setTextureSize(128, 128);
        bipedBody.mirror = true;

        bipedRightArm = new ModelRenderer(this, 60, 21);
        bipedRightArm.addBox(-3F, -2F, -2F, 4, 30, 6);
        bipedRightArm.setRotationPoint(-10F, -7F, 2F);
        bipedRightArm.setTextureSize(128, 128);
        bipedRightArm.mirror = true;

        bipedLeftArm = new ModelRenderer(this, 60, 58);
        bipedLeftArm.addBox(-1F, -1F, -2F, 4, 30, 6);
        bipedLeftArm.setRotationPoint(10F, -8F, 2F);
        bipedLeftArm.setTextureSize(128, 128);
        bipedLeftArm.mirror = true;

        bipedRightLeg = new ModelRenderer(this, 37, 0);
        bipedRightLeg.addBox(-2F, 0F, -2F, 6, 16, 5);
        bipedRightLeg.setRotationPoint(-5F, 8F, 3F);
        bipedRightLeg.setTextureSize(128, 128);
        bipedRightLeg.mirror = true;

        bipedLeftLeg = new ModelRenderer(this, 60, 0);
        bipedLeftLeg.addBox(-2F, 0F, -2F, 6, 16, 5);
        bipedLeftLeg.setRotationPoint(2F, 8F, 3F);
        bipedLeftLeg.setTextureSize(128, 128);
        bipedLeftLeg.mirror = true;

        waist = new ModelRenderer(this, 0, 70);
        waist.addBox(-5F, -2F, 0F, 9, 5, 6);
        waist.setRotationPoint(0F, 6F, 0F);
        waist.setTextureSize(128, 128);
        waist.mirror = true;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        GlStateManager.pushMatrix();

        //scale *= 0.5F;
        GlStateManager.translate(0, 1.5, 0);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(0, -1.5, 0);

        this.bipedBody.render(scale);
        this.crt.render(scale);
        this.waist.render(scale);
        this.bipedRightArm.render(scale);
        this.bipedLeftArm.render(scale);
        this.bipedRightLeg.render(scale);
        this.bipedLeftLeg.render(scale);

        GlStateManager.popMatrix();
    }

    public void setRotationAngles(float swingTime, float swingScale, float aa, float bb, float cc, float dd, Entity entity)
    {
        this.bipedLeftLeg.rotateAngleX = -1.5F * this.triangleWave(swingTime, 13.0F) * swingScale;
        this.bipedRightLeg.rotateAngleX = 1.5F * this.triangleWave(swingTime, 13.0F) * swingScale;
        this.bipedLeftLeg.rotateAngleY = 0.0F;
        this.bipedRightLeg.rotateAngleY = 0.0F;

        this.bipedLeftArm.rotateAngleX = 1.5F * this.triangleWave(swingTime * 1.5F, 13.0F) * swingScale * 0.5F;
        this.bipedRightArm.rotateAngleX = -1.5F * this.triangleWave(swingTime * 1.5F, 13.0F) * swingScale * 0.5F;
        this.bipedLeftArm.rotateAngleY = 0.0F;
        this.bipedRightArm.rotateAngleY = 0.0F;

        EntitySiliconGolem entitygolem = (EntitySiliconGolem)entity;
        int i = entitygolem.attackTime;

        if (i > 0)
        {
            this.bipedRightArm.rotateAngleX = -2.0F + 1.5F * this.sineWave((float)i - dd, 20.0F);
            this.bipedLeftArm.rotateAngleX = -2.0F + 1.5F * this.sineWave((float)i - dd, 20.0F);
        }
    }

    private float triangleWave(float x, float waveLength)
    {
        return (Math.abs(x % waveLength - waveLength * 0.5F) - waveLength * 0.25F) / (waveLength * 0.25F);
    }

    private float sineWave(float x, float waveLength){
        float recip = (float) (2 * Math.PI / waveLength);
        return (float) Math.cos(x*recip);
    }

    //TODO extend set invisible

}
