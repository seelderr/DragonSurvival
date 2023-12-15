package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import java.awt.Color;

public class DragonArmorRenderLayer extends GeoLayerRenderer<DragonEntity>{

	private final GeoEntityRenderer<DragonEntity> renderer;

	public DragonArmorRenderLayer(GeoEntityRenderer<DragonEntity> entityRendererIn){
		super(entityRendererIn);
		renderer = entityRendererIn;
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		if(!ClientDragonRender.armorRenderLayer)
			return;

		Player player = entitylivingbaseIn.getPlayer();

		if(player.isSpectator())
			return;

		IBone neck = ClientDragonRender.dragonArmorModel.getAnimationProcessor().getBone("Neck");

		if(neck != null)
			neck.setHidden(false);

		ResourceLocation helmetTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlot.HEAD));
		ResourceLocation chestPlateTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlot.CHEST));
		ResourceLocation legsTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlot.LEGS));
		ResourceLocation bootsTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlot.FEET));

		((DragonRenderer)renderer).isRenderLayers = true;

		GeoModel model = ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelResource(null));

		renderArmorPiece(model, player.getItemBySlot(EquipmentSlot.HEAD), matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, helmetTexture);
		renderArmorPiece(model, player.getItemBySlot(EquipmentSlot.CHEST), matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, chestPlateTexture);
		renderArmorPiece(model, player.getItemBySlot(EquipmentSlot.LEGS), matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, legsTexture);
		renderArmorPiece(model, player.getItemBySlot(EquipmentSlot.FEET), matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, partialTicks, bootsTexture);

		((DragonRenderer)renderer).isRenderLayers = false;
	}

	private void renderArmorPiece(GeoModel model, ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation helmetTexture){
		if(entitylivingbaseIn == null)
			return;

		if(stack == null || stack.isEmpty()) return;

		Color armorColor = new Color(1f, 1f, 1f);

		if(stack.getItem() instanceof DyeableArmorItem){
			int colorCode = ((DyeableArmorItem)stack.getItem()).getColor(stack);
			armorColor = new Color(colorCode);
		}

		ClientDragonRender.dragonModel.setCurrentTexture(helmetTexture);
		ClientDragonRender.dragonArmor.copyPosition(entitylivingbaseIn);
		RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, helmetTexture);
		VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
		renderer.render(model, entitylivingbaseIn,
		                partialTicks,
		                type,
		                matrixStackIn,
		                bufferIn,
		                vertexConsumer,
		                packedLightIn,
		                OverlayTexture.NO_OVERLAY,
		                armorColor.getRed() / 255F,
		                armorColor.getGreen() / 255F,
		                armorColor.getBlue() / 255F, 1F);
	}

	public static String constructArmorTexture(Player playerEntity, EquipmentSlot equipmentSlot){
		String texture = "textures/armor/";
		try {
			Item item = playerEntity.getItemBySlot(equipmentSlot).getItem();
			String texture2 = itemToResLoc(item);
			if (texture2 != null) {
				texture2 = texture + texture2;
				if (Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(DragonSurvivalMod.MODID, texture2)).isPresent()) {
					return texture2;
				}
			}
			if(item instanceof ArmorItem armorItem){
				ArmorMaterial armorMaterial = armorItem.getMaterial();
				if(armorMaterial instanceof ArmorMaterials){
					if(armorMaterial == ArmorMaterials.NETHERITE)
						texture += "netherite_";
					else if(armorMaterial == ArmorMaterials.DIAMOND)
						texture += "diamond_";
					else if(armorMaterial == ArmorMaterials.IRON)
						texture += "iron_";
					else if(armorMaterial == ArmorMaterials.LEATHER)
						texture += "leather_";
					else if(armorMaterial == ArmorMaterials.GOLD)
						texture += "gold_";
					else if(armorMaterial == ArmorMaterials.CHAIN)
						texture += "chainmail_";
					else if(armorMaterial == ArmorMaterials.TURTLE)
						texture += "turtle_";
					else
						return texture + "empty_armor.png";
	
					texture += "dragon_";
					switch(equipmentSlot){
						case HEAD -> texture += "helmet";
						case CHEST -> texture += "chestplate";
						case LEGS -> texture += "leggings";
						case FEET -> texture += "boots";
					}
					texture += ".png";
					return texture;
				}
				int defense = armorItem.getDefense();
				switch(equipmentSlot){
					case FEET -> texture += Mth.clamp(defense, 1, 4) + "_dragon_boots";
					case CHEST -> texture += Mth.clamp(defense / 2, 1, 4) + "_dragon_chestplate";
					case HEAD -> texture += Mth.clamp(defense, 1, 4) + "_dragon_helmet";
					case LEGS -> texture += Mth.clamp((int)(defense / 1.5), 1, 4) + "_dragon_leggings";
				}
				texture += ".png";
				return texture;
			}
			return texture + "empty_armor.png";
		} catch (ResourceLocationException e) {
			return texture + "empty_armor.png";
		}
	}
	
	public static String itemToResLoc(Item item) {
		if (item == Items.AIR) return null;

		ResourceLocation registryName = ResourceHelper.getKey(item);
		if (registryName != null) {
			String[] reg = registryName.toString().split(":");
			String loc = reg[0] + "/" + reg[1] + ".png";
			// filters certain characters (non [a-z0-9/._-]) to prevent crashes
			// this probably should never be relevant, but you can never be too safe
			loc = loc.chars()
				.filter(ch -> ResourceLocation.validPathChar((char) ch))
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
			return loc;
		}
		return null;
	}
}