package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrincesHorseEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PrincessHorseRenderer extends GeoEntityRenderer<PrincesHorseEntity>{
	public PrincessHorseRenderer(EntityRendererManager renderManager, AnimatedGeoModel<PrincesHorseEntity> modelProvider){
		super(renderManager, modelProvider);
	}
}