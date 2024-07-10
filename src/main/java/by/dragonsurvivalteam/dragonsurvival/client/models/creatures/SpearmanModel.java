package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.SpearmanEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SpearmanModel extends GeoModel<SpearmanEntity> {
    @Override
    public ResourceLocation getModelResource(SpearmanEntity object){
        return ResourceLocation.fromNamespaceAndPath(MODID, "geo/hunter_spearman.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpearmanEntity object){
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/hunters/spearman.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpearmanEntity animatable){
        return ResourceLocation.fromNamespaceAndPath(MODID, "animations/hunter_spearman.animation.json");
    }

    @Override
    public void applyMolangQueries(final AnimationState<SpearmanEntity> animationState, double currentTick) {
        super.applyMolangQueries(animationState, currentTick);

        Vec3 lookAngle = animationState.getAnimatable().getLookAngle();
        MathParser.setVariable("query.look_angle_x", () -> lookAngle.x);
        MathParser.setVariable("query.look_angle_y", () -> lookAngle.y);
        MathParser.setVariable("query.look_angle_z", () -> lookAngle.z);
    }
}
