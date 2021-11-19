package by.jackraidenph.dragonsurvival.renderer;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.entity.Magic.DragonSpikeEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class DragonSpikeRenderer extends ArrowRenderer<DragonSpikeEntity>
{
	public DragonSpikeRenderer(EntityRendererManager p_i46179_1_)
	{
		super(p_i46179_1_);
	}
	@Override
	public ResourceLocation getTextureLocation(DragonSpikeEntity entity)
	{
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/dragon_spike_" + entity.getLevel() + ".png");
	}
}
