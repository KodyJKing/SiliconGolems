package silicongolems.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.List;

public class FakePlayerUtil {

    public static void rightClick(FakePlayer fp, EntityLivingBase actual, ItemStack stack){
        RayTraceResult mouseover = getMouseover(actual, 5);
        if(mouseover != null && mouseover.typeOfHit != RayTraceResult.Type.MISS){

            EnumActionResult result = EnumActionResult.PASS;

            if(mouseover.typeOfHit == RayTraceResult.Type.ENTITY && mouseover.entityHit instanceof EntityLivingBase){
                result = fp.interact(mouseover.entityHit, stack, EnumHand.MAIN_HAND);
            } else if(stack != null){
                Vec3d hit = mouseover.hitVec;
                result = stack.onItemUse(
                        fp, actual.worldObj, mouseover.getBlockPos(), EnumHand.MAIN_HAND, mouseover.sideHit,
                        (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            }

            if(result != EnumActionResult.PASS)
                return;
        }

        if(stack != null)
            stack.useItemRightClick(actual.worldObj, fp, EnumHand.MAIN_HAND);
    }

    public static RayTraceResult getMouseover(EntityLivingBase entity, double range){
        Vec3d eye = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);

        RayTraceResult blocks = traceBlocks(entity, range);
        RayTraceResult entities = traceEntities(entity, range);

        if(blocks == null || blocks.typeOfHit == RayTraceResult.Type.MISS)
            return entities;
        if(entities == null || entities.typeOfHit == RayTraceResult.Type.MISS)
            return blocks;

        if(blocks.hitVec.squareDistanceTo(eye) < entities.hitVec.squareDistanceTo(eye))
            return blocks;
        return entities;
    }

    public static RayTraceResult traceBlocks(Entity entity, double range){
        Vec3d eye = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
        Vec3d look = entity.getLookVec();
        Vec3d end = eye.add(look.scale(range));
        return entity.worldObj.rayTraceBlocks(eye, end, false, false, true);
    }


    public static RayTraceResult traceEntities(Entity entity, double range){
        Vec3d look = entity.getLookVec();
        Vec3d eye = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
        Vec3d end = eye.add(look.scale(range));

        List<Entity> list = entity.worldObj.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity other)
            {
                return other != null && other.canBeCollidedWith();
            }
        }));

        RayTraceResult closest = null;
        double closeDistSq = Double.MAX_VALUE;
        for(Entity other: list){

            if(other.getEntityBoundingBox().isVecInside(eye))
                return new RayTraceResult(other);

            RayTraceResult hit = other.getEntityBoundingBox().calculateIntercept(eye, end);
            if(hit == null)
                continue;

            double distSq = hit.hitVec.squareDistanceTo(eye);
            if(distSq < closeDistSq && distSq < range * range){
                closeDistSq = distSq;
                closest = hit;
                hit.typeOfHit = RayTraceResult.Type.ENTITY;
                hit.entityHit = other;
            }
        }

        return closest;
    }
}
