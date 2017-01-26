package silicongolems.scripting.js;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import silicongolems.entity.EntitySiliconGolem;

public class WrapperGolem {

    private EntitySiliconGolem golem;

    public WrapperGolem(EntitySiliconGolem golem){
        this.golem = golem;
    }

    public void turn(float angle){
        golem.rotationYawHead += angle;
        golem.rotationYaw += angle;
    }

    public void move(){
        float x = -MathHelper.sin(golem.rotationYaw * 0.017453292F);
        float z = MathHelper.cos(golem.rotationYaw * 0.017453292F);
        golem.posX += x;
        golem.posY += z;
    }

    public void snap(){
        golem.setLocationAndAngles(Math.floor(golem.posX) + 0.5, Math.floor(golem.posY) + 0.5, Math.floor(golem.posX) + 0.5, 0 ,0);
    }
}
