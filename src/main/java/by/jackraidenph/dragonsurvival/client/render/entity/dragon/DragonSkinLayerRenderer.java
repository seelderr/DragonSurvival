package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.handlers.DragonSkins;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.LayerSettings;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.SkinPreset.SkinAgeGroup;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class DragonSkinLayerRenderer extends GeoLayerRenderer<DragonEntity>
{
	private final IGeoRenderer<DragonEntity> renderer;
	
	public DragonSkinLayerRenderer(IGeoRenderer<DragonEntity> entityRendererIn)
	{
		super(entityRendererIn);
		this.renderer = entityRendererIn;
	}
	
	public ConcurrentHashMap<ResourceLocation, DynamicTexture> dynamicTextures = new ConcurrentHashMap<>();
	
	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(entitylivingbaseIn.hasEffect(Effects.INVISIBILITY)) return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(entitylivingbaseIn.getPlayer()).orElse(null);
		if (handler == null) return;
		
		SkinPreset preset = handler.getSkin().skinPreset;
		SkinAgeGroup ageGroup = preset.skinAges.get(handler.getLevel());
		if(ageGroup.defaultSkin) return;
		
		for(EnumSkinLayer layer : EnumSkinLayer.values()){
			LayerSettings settings = ageGroup.layerSettings.get(layer);
			String key = settings.selectedSkin;
			
			if(key != null){
				Texture text = DragonCustomizationHandler.getSkin(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
				
				if(text != null) {
					ResourceLocation texture = DragonCustomizationHandler.getSkinTexture(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
					if(ClientDragonRender.dragonModel.getTextureLocation(entitylivingbaseIn) == texture) return;
					
					if(texture == null || DragonSkins.getPlayerSkin(entitylivingbaseIn.getPlayer(), handler.getType(), handler.getLevel()) != null) {
						return;
					}
					
					ResourceLocation dynamicTexture = text.colorable && text.defaultColor == null ? new ResourceLocation(DragonSurvivalMod.MODID, "dynamic_" + texture.getPath() + "_" + entitylivingbaseIn.getPlayer().getName().getString().toLowerCase(Locale.ROOT)) : texture;
					
					Color renderColor = new Color(1f, 1f, 1f, 1f);
					
					if(text.colorable && text.defaultColor == null) {
						if (!dynamicTextures.containsKey(dynamicTexture) || handler.getSkin().updateLayers.contains(layer)) {
							handler.getSkin().updateLayers.remove(layer);
							
							if (!dynamicTextures.containsKey(dynamicTexture)) {
								DynamicTexture texture1 = new DynamicTexture(512, 512, false);
								Minecraft.getInstance().getTextureManager().register(dynamicTexture, texture1);
								dynamicTextures.put(dynamicTexture, texture1);
							}
							updateSkin(text, texture, dynamicTexture, settings);
						}
					}else if(text.colorable){
						Color defaultColor = Color.decode(text.defaultColor);
						Color curColor = Color.getHSBColor(settings.hue, settings.saturation, settings.brightness);
						if(curColor.getRGB() == defaultColor.getRGB()){
							renderColor = defaultColor;
						}else {
							renderColor = curColor;
						}
					}
					((DragonRenderer)renderer).isLayer = true;
					
					if(settings.glowing){
						RenderType type = RenderType.eyes(dynamicTexture);
						IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
						renderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, 0, OverlayTexture.NO_OVERLAY, renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
					}else {
						RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, dynamicTexture);
						IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
						renderer.render(ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelLocation(null)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
					}
					((DragonRenderer)renderer).isLayer = false;
					
				}
			}
		}
	}
	public void updateSkin(Texture text, ResourceLocation input, ResourceLocation output, LayerSettings settings){
		Runnable run = () -> {
			try {
				InputStream textureStream = Minecraft.getInstance().getResourceManager().getResource(input).getInputStream();
				NativeImage img = NativeImage.read(textureStream);
				textureStream.close();
				
				if(text.colorable) {
					float hueVal = settings.hue - 0.5f;
					float satVal = settings.saturation - 0.5f;
					float brightVal = settings.brightness - 0.5f;
					
					float[] hsb = new float[3];
					for (int x = 0; x < img.getWidth(); x++) {
						for (int y = 0; y < img.getHeight(); y++) {
							Color color = new Color(img.getPixelRGBA(x, y), true);
							Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
							
							if(settings.glowing && hsb[0] == 0 && hsb[1] == 0) continue;
							
							if(hueVal > 0){
								hsb[0] = (float)MathHelper.lerp(Math.abs(hueVal) * 2, hsb[0], 1.0);
							}else{
								hsb[0] = (float)MathHelper.lerp(Math.abs(hueVal) * 2, hsb[0], 0.0);
							}
							
							if(satVal > 0){
								hsb[1] = (float)MathHelper.lerp(Math.abs(satVal) * 2, hsb[1], 1.0);
							}else{
								hsb[1] = (float)MathHelper.lerp(Math.abs(satVal) * 2, hsb[1], 0.0);
							}
							
							if(brightVal > 0){
								hsb[2] = (float)MathHelper.lerp(Math.abs(brightVal) * 2, hsb[2], 1.0);
							}else{
								hsb[2] = (float)MathHelper.lerp(Math.abs(brightVal) * 2, hsb[2], 0.0);
							}
							
							Color c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
							Color c1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), color.getAlpha());
							
							img.setPixelRGBA(x, y, c1.getRGB());
						}
					}
				}
					dynamicTextures.get(output).setPixels(img);
					dynamicTextures.get(output).upload();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		run.run();
	}
}
