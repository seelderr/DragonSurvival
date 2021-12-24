package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
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
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(entitylivingbaseIn.hasEffect(Effects.INVISIBILITY)) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if (handler == null || !handler.getClawInventory().renderClaws) return;
		
		String clawTexture = constructClaws(entitylivingbaseIn.getPlayer());
		
		if (clawTexture != null) {
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, clawTexture);
			renderToolLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
		}
		
		String teethTexture = constructTeethTexture(entitylivingbaseIn.getPlayer());
		
		if (teethTexture != null) {
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, teethTexture);
			renderToolLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, texture, renderer, getEntityModel());
		}
	}
	
	public String constructClaws(PlayerEntity playerEntity)
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
	
	public String constructTeethTexture(PlayerEntity playerEntity)
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
	
	private void renderToolLayer(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation texture, IGeoRenderer<DragonEntity> renderer, GeoModelProvider entityModel)
	{
		RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, texture);
		IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
		
		renderer.render(entityModel.getModel(entityModel.getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
