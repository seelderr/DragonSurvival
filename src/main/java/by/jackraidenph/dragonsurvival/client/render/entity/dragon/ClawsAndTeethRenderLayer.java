package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ClawsAndTeethRenderLayer extends GeoLayerRenderer<DragonEntity>
{
	
	private final IGeoRenderer<DragonEntity> renderer;
	
	public ClawsAndTeethRenderLayer(IGeoRenderer<DragonEntity> entityRendererIn)
	{
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}
	
	@Override
	public void render(PoseStack pStack, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!((DragonRenderer)renderer).renderLayers) return;
		if(entitylivingbaseIn.hasEffect(MobEffects.INVISIBILITY)) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if (handler == null || (!handler.getClawInventory().renderClaws && (ConfigHandler.SERVER.syncClawRender.get() || entitylivingbaseIn.getPlayer() == Minecraft.getInstance().player))) return;
		
		String clawTexture = constructClaws(entitylivingbaseIn.getPlayer());
		
		if (clawTexture != null) {
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, clawTexture);
			((DragonRenderer)renderer).isLayer = true;
			renderToolLayer(pStack, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
			((DragonRenderer)renderer).isLayer = false;
		}
		
		String teethTexture = constructTeethTexture(entitylivingbaseIn.getPlayer());
		
		if (teethTexture != null) {
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, teethTexture);
			((DragonRenderer)renderer).isLayer = true;
			renderToolLayer(pStack, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
			((DragonRenderer)renderer).isLayer = false;
		}
	}
	
	public String constructClaws(Player playerEntity)
	{
		String texture = "textures/armor/";
		DragonStateHandler handler = DragonStateProvider.getCap(playerEntity).orElse(null);
		ItemStack clawItem = handler.getClawInventory().getClawsInventory().getItem(handler.getType() == DragonType.CAVE ? 1 : handler.getType() == DragonType.FOREST ? 2 : 3);
		if (!clawItem.isEmpty() && clawItem.getItem() instanceof TieredItem) {
			texture = ClientEvents.getMaterial(texture, clawItem);
		} else {
			return null;
		}
		
		return texture + "dragon_claws.png";
	}
	
	public String constructTeethTexture(Player playerEntity)
	{
		String texture = "textures/armor/";
		ItemStack swordItem = DragonStateProvider.getCap(playerEntity).orElse(null).getClawInventory().getClawsInventory().getItem(0);
		
		if (!swordItem.isEmpty() && swordItem.getItem() instanceof TieredItem) {
			texture = ClientEvents.getMaterial(texture, swordItem);
		} else {
			return null;
		}
		
		return texture + "dragon_teeth.png";
	}
	
	private void renderToolLayer(PoseStack  pStack, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation texture, IGeoRenderer<DragonEntity> renderer, GeoModelProvider entityModel)
	{
		RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, pStack, bufferIn, null, packedLightIn, texture);
		VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
		
		renderer.render(entityModel.getModel(entityModel.getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, pStack, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
