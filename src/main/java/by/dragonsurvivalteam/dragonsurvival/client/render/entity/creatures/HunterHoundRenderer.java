package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.HunterHoundEntity;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class HunterHoundRenderer extends WolfRenderer{
	static final List<ResourceLocation> SKINS = Arrays.asList(ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_1.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_2.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_3.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_4.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_5.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_6.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_7.png"), ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_8.png"));

	static final ResourceLocation HECTOR_SKIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/hounds/dragon_hound_hector.png");

	public HunterHoundRenderer(EntityRendererProvider.Context rendererManager){
		super(rendererManager);
	}

	@Override
	public ResourceLocation getTextureLocation(Wolf entity){
		if(entity.getDisplayName().getString().equals("Hector") || entity.getDisplayName().getString().equals("Гектор")){
			return HECTOR_SKIN;
		}
		return SKINS.get(entity.getEntityData().get(HunterHoundEntity.variety));
	}
}