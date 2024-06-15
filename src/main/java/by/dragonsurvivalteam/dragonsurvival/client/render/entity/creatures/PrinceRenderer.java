package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;


public class PrinceRenderer extends VillagerRenderer{
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/dragon_prince.png");

	public PrinceRenderer(EntityRendererProvider.Context entityRendererManager){
		super(entityRendererManager);
		layers.removeIf(VillagerProfessionLayer.class::isInstance);
	}

	@Override
	public ResourceLocation getTextureLocation(Villager villager){
		return TEXTURE;
	}
}