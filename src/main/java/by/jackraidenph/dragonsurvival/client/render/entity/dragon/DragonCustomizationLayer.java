package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.handlers.DragonSkins;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DragonCustomizationLayer extends GeoLayerRenderer<DragonEntity>
{
	private final IGeoRenderer<DragonEntity> renderer;
	
	public DragonCustomizationLayer(IGeoRenderer<DragonEntity> entityRendererIn)
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
		
		for(CustomizationLayer layer : CustomizationLayer.values()){
			String key = handler.getSkin().playerSkinLayers.getOrDefault(handler.getLevel(), new HashMap<>()).getOrDefault(layer, null);
			
			if(key != null){
				Texture text = DragonCustomizationHandler.getSkin(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
				
				if(text != null && !text.glowing) {
					ResourceLocation texture = DragonCustomizationHandler.getSkinTexture(entitylivingbaseIn.getPlayer(), layer, key, handler.getType());
					if(ClientDragonRender.dragonModel.getTextureLocation(entitylivingbaseIn) == texture) return;
					
					if(DragonSkins.getPlayerSkin(entitylivingbaseIn.getPlayer(), handler.getType(), handler.getLevel()) != null) {
						return;
					}
					
					Double curHue = handler.getSkin().skinLayerHue.getOrDefault(handler.getLevel(), new HashMap<>()).getOrDefault(layer, 0.0);
					ResourceLocation dynamicTexture = new ResourceLocation(DragonSurvivalMod.MODID, "dynamic_" + texture.getPath());
					
					if(!dynamicTextures.containsKey(dynamicTexture) || handler.getSkin().hueChanged.contains(layer)) {
						handler.getSkin().hueChanged.remove(layer);
						
						if(!dynamicTextures.containsKey(dynamicTexture)) {
							DynamicTexture texture1 = new DynamicTexture(512, 512, false);
							Minecraft.getInstance().getTextureManager().register(dynamicTexture, texture1);
							dynamicTextures.put(dynamicTexture, texture1);
						}
						
						updateSkin(text, texture, dynamicTexture, curHue);
					}
					
					RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, dynamicTexture);
					IVertexBuilder vertexConsumer = bufferIn.getBuffer(type);
					((DragonRenderer)renderer).isLayer = true;
					renderer.render(ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelLocation(null)), entitylivingbaseIn, partialTicks, type, matrixStackIn, bufferIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1F);
					((DragonRenderer)renderer).isLayer = false;
				}
			}
		}
	}
	public void updateSkin(Texture text, ResourceLocation input, ResourceLocation output, double hue){
		Runnable run = () -> {
			try {
				InputStream textureStream = Minecraft.getInstance().getResourceManager().getResource(input).getInputStream();
				NativeImage img = NativeImage.read(textureStream);
				textureStream.close();
				
				if(text.recolor) {
					float[] hsb = new float[3];
					for (int x = 0; x < img.getWidth(); x++) {
						for (int y = 0; y < img.getHeight(); y++) {
							Color color = new Color(img.getPixelRGBA(x, y), true);
							Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
							
							if(hue > 0){
								hsb[0] = (float)MathHelper.lerp(hue / 180f, hsb[0], 1.0);
							}else{
								hsb[0] = (float)MathHelper.lerp(hue / -180f, hsb[0], 0.0);
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
