package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import java.awt.Color;

public class DragonArmorRenderLayer extends GeoLayerRenderer<DragonEntity>{

	private final GeoEntityRenderer<DragonEntity> renderer;

	public DragonArmorRenderLayer(GeoEntityRenderer<DragonEntity> entityRendererIn){
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(!ConfigHandler.CLIENT.armorRenderLayer.get()){
			return;
		}

		PlayerEntity player = entitylivingbaseIn.getPlayer();

		if(player.isSpectator()){
			return;
		}

		final IBone neck = ClientDragonRender.dragonArmorModel.getAnimationProcessor().getBone("Neck");

		if(neck != null){
			neck.setHidden(false);
		}

		ResourceLocation helmetTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.HEAD));
		ResourceLocation chestPlateTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.CHEST));
		ResourceLocation legsTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.LEGS));
		ResourceLocation bootsTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.FEET));

		renderArmorPiece(renderer.helmet, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, helmetTexture);
		renderArmorPiece(renderer.chestplate, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, chestPlateTexture);
		renderArmorPiece(renderer.leggings, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, legsTexture);
		renderArmorPiece(renderer.boots, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, bootsTexture);
	}

	private void renderArmorPiece(ItemStack stack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation helmetTexture){
		if(entitylivingbaseIn == null) return;

		Color armorColor = new Color(1f, 1f, 1f);

		if(stack.getItem() instanceof IDyeableArmorItem){
			int colorCode = ((IDyeableArmorItem)stack.getItem()).getColor(stack);
			armorColor = new Color(colorCode);
		}

		ClientDragonRender.dragonModel.setCurrentTexture(helmetTexture);
		ClientDragonRender.dragonArmor.copyPosition(entitylivingbaseIn);
		RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, helmetTexture);
		IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);

		((DragonRenderer)renderer).isLayer = true;
		renderer.render(ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelLocation(null)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, armorColor.getRed() / 255F, armorColor.getGreen() / 255F, armorColor.getBlue() / 255F, 1F);
		((DragonRenderer)renderer).isLayer = false;
	}

	public static String constructArmorTexture(PlayerEntity playerEntity, EquipmentSlotType equipmentSlot){
		String texture = "textures/armor/";
		Item item = playerEntity.getItemBySlot(equipmentSlot).getItem();
		if(item instanceof ArmorItem){
			ArmorItem armorItem = (ArmorItem)item;
			IArmorMaterial armorMaterial = armorItem.getMaterial();
			if(armorMaterial.getClass() == ArmorMaterial.class){
				if(armorMaterial == ArmorMaterial.NETHERITE){
					texture += "netherite_";
				}else if(armorMaterial == ArmorMaterial.DIAMOND){
					texture += "diamond_";
				}else if(armorMaterial == ArmorMaterial.IRON){
					texture += "iron_";
				}else if(armorMaterial == ArmorMaterial.LEATHER){
					texture += "leather_";
				}else if(armorMaterial == ArmorMaterial.GOLD){
					texture += "gold_";
				}else if(armorMaterial == ArmorMaterial.CHAIN){
					texture += "chainmail_";
				}else if(armorMaterial == ArmorMaterial.TURTLE){
					texture += "turtle_";
				}else{
					return texture + "empty_armor.png";
				}

				texture += "dragon_";
				switch(equipmentSlot){
					case HEAD:
						texture += "helmet";
						break;
					case CHEST:
						texture += "chestplate";
						break;
					case LEGS:
						texture += "leggings";
						break;
					case FEET:
						texture += "boots";
						break;
				}
				texture += ".png";
				return texture;
			}else{
				int defense = armorItem.getDefense();
				switch(equipmentSlot){
					case FEET:
						texture += MathHelper.clamp(defense, 1, 4) + "_dragon_boots";
						break;
					case CHEST:
						texture += MathHelper.clamp(defense / 2, 1, 4) + "_dragon_chestplate";
						break;
					case HEAD:
						texture += MathHelper.clamp(defense, 1, 4) + "_dragon_helmet";
						break;
					case LEGS:
						texture += MathHelper.clamp((int)(defense / 1.5), 1, 4) + "_dragon_leggings";
						break;
				}
				return texture + ".png";
			}
		}
		return texture + "empty_armor.png";
	}
}