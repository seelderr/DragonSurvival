package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.Princess;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.DyeColor;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class PrincessRenderer extends VillagerRenderer{
	private static final ResourceLocation BLACK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/princess/princess_black.png");
	private static final ResourceLocation BLUE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/princess/princess_blue.png");
	private static final ResourceLocation PURPLE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/princess/princess_purple.png");
	private static final ResourceLocation RED = ResourceLocation.fromNamespaceAndPath(MODID,"textures/princess/princess_red.png");
	private static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/princess/princess_white.png");
	private static final ResourceLocation YELLOW = ResourceLocation.fromNamespaceAndPath(MODID,"textures/princess/princess_yellow.png");

	public PrincessRenderer(EntityRendererProvider.Context rendererManager){
		super(rendererManager);
		layers.removeIf(villagerEntityVillagerModelLayerRenderer -> villagerEntityVillagerModelLayerRenderer instanceof VillagerProfessionLayer);
	}

	@Override
	public ResourceLocation getTextureLocation(Villager villager){
		Princess princess = (Princess)villager;
		switch(DyeColor.byId(princess.getColor())){
			case RED:
				return RED;
			case BLUE:
				return BLUE;
			case YELLOW:
				return YELLOW;
			case BLACK:
				return BLACK;
			case PURPLE:
				return PURPLE;
			case WHITE:
				return WHITE;
		}
		return super.getTextureLocation(villager);
	}
}