package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrincesHorse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PrincessHorseRenderer extends GeoEntityRenderer<PrincesHorse>{
	public PrincessHorseRenderer(EntityRendererManager renderManager, AnimatedGeoModel<PrincesHorse> modelProvider){
		super(renderManager, modelProvider);
	}
}