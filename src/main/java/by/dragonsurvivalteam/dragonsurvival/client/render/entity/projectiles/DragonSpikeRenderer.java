package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.DragonSpikeEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class DragonSpikeRenderer extends ArrowRenderer<DragonSpikeEntity>{
	public DragonSpikeRenderer(EntityRendererManager p_i46179_1_){
		super(p_i46179_1_);
	}

	@Override
	public ResourceLocation getTextureLocation(DragonSpikeEntity entity){
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/dragon_spike_" + entity.getArrow_level() + ".png");
	}
}