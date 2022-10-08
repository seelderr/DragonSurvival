package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

public class DragonEditorHandler{
	public static ResourceLocation getSkinTexture(Player player, EnumSkinLayer layer, String key, DragonType type){
		if(Objects.equals(layer.name, "Extra") && layer != EnumSkinLayer.EXTRA){
			return getSkinTexture(player, EnumSkinLayer.EXTRA, key, type);
		}

		if(layer == EnumSkinLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			DragonStateHandler handler = DragonUtils.getHandler(player);
			return getSkinTexture(player, layer, type.name().toLowerCase() + "_base_" + handler.getLevel().ordinal(), type);
		}

		Texture[] texts = DragonEditorRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layer, new Texture[0]);

		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return new ResourceLocation(texture.texture);
			}
		}

		return null;
	}

	public static Texture getSkin(Player player, EnumSkinLayer layer, String key, DragonType type){
		if(Objects.equals(layer.name, "Extra") && layer != EnumSkinLayer.EXTRA){
			return getSkin(player, EnumSkinLayer.EXTRA, key, type);
		}

		if(layer == EnumSkinLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			return getSkin(player, layer, type.name().toLowerCase() + "_base_" + DragonUtils.getDragonLevel(player).ordinal(), type);
		}

		Texture[] texts = DragonEditorRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layer, new Texture[0]);

		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return texture;
			}
		}

		return null;
	}

	public static ArrayList<String> getKeys(DragonType type, EnumSkinLayer layers){
		if(Objects.equals(layers.name, "Extra") && layers != EnumSkinLayer.EXTRA){
			return getKeys(type, EnumSkinLayer.EXTRA);
		}

		ArrayList<String> list = new ArrayList<>();

		Texture[] texts = DragonEditorRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layers, new Texture[0]);
		for(Texture texture : texts){
			list.add(texture.key);
		}

		return list;
	}

	public static ArrayList<String> getKeys(Player player, EnumSkinLayer layers){
		return getKeys(DragonUtils.getDragonType(player), layers);
	}

	public static void generateSkinTextures(DragonEntity dragon){
		Player player = dragon.getPlayer();
		DragonStateHandler handler = DragonUtils.getHandler(player);

		SkinPreset preset = handler.getSkin().skinPreset;
		SkinAgeGroup ageGroup = preset.skinAges.get(handler.getLevel());

		if(!RenderSystem.isOnRenderThreadOrInit()){
			RenderSystem.recordRenderCall(() -> {
				genTextures(dragon, player, handler, ageGroup);
			});
		}else{
			genTextures(dragon, player, handler, ageGroup);
		}
	}

	private static void genTextures(DragonEntity dragon, Player player, DragonStateHandler handler, SkinAgeGroup ageGroup){
		NativeImage normal = new NativeImage(512, 512, true);
		NativeImage glow = new NativeImage(512, 512, true);

		for(EnumSkinLayer layer : EnumSkinLayer.values()){
			LayerSettings settings = ageGroup.layerSettings.get(layer);
			String key = settings.selectedSkin;

			if(key != null){
				Texture text = getSkin(player, layer, key, handler.getType());

				if(text != null){
					float hueVal = settings.hue - 0.5f;
					float satVal = settings.saturation - 0.5f;
					float brightVal = settings.brightness - 0.5f;

					try{
						ResourceLocation texture = getSkinTexture(player, layer, key, handler.getType());
						Resource resource = Minecraft.getInstance().getResourceManager().getResource(texture);
						InputStream textureStream = resource.getInputStream();
						NativeImage img = NativeImage.read(textureStream);
						textureStream.close();

						for(int x = 0; x < img.getWidth(); x++){
							for(int y = 0; y < img.getHeight(); y++){
								Color c1 = getColor(settings, text, hueVal, satVal, brightVal, img, x, y);

								if(c1 == null){
									continue;
								}

								if(c1.getRed() != 0 || c1.getBlue() != 0 || c1.getGreen() != 0 || c1.getAlpha() != 0){
									Supplier<NativeImage> g2 = settings.glowing ? () -> glow : () -> normal;
									g2.get().setPixelRGBA(x, y, c1.getRGB());
								}
							}
						}
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		}

		String key = dragon.getStringUUID();
		ResourceLocation dynamicNormalKey = new ResourceLocation(DragonSurvivalMod.MODID, "dynamic_normal_" + key);
		ResourceLocation dynamicGlowKey = new ResourceLocation(DragonSurvivalMod.MODID, "dynamic_glow_" + key);

		registerCompiledTexture(normal, dynamicNormalKey);
		registerCompiledTexture(glow, dynamicGlowKey);

		handler.getSkin().recompileSkin = false;
		handler.getSkin().isCompiled = true;
	}

	@Nullable
	private static Color getColor(LayerSettings settings, Texture text, float hueVal, float satVal, float brightVal, NativeImage img, int x, int y){
		float[] hsb = new float[3];

		Color color = new Color(img.getPixelRGBA(x, y), true);
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

		if(text.colorable){
			if(settings.glowing && hsb[0] == 0 && hsb[1] == 0){
				return null;
			}

			if(hueVal > 0){
				hsb[0] = (float)Mth.lerp(Math.abs(hueVal) * 2, hsb[0], 1.0);
			}else{
				hsb[0] = (float)Mth.lerp(Math.abs(hueVal) * 2, hsb[0], 0.0);
			}

			if(satVal > 0){
				hsb[1] = (float)Mth.lerp(Math.abs(satVal) * 2, hsb[1], 1.0);
			}else{
				hsb[1] = (float)Mth.lerp(Math.abs(satVal) * 2, hsb[1], 0.0);
			}

			if(brightVal > 0){
				hsb[2] = (float)Mth.lerp(Math.abs(brightVal) * 2, hsb[2], 1.0);
			}else{
				hsb[2] = (float)Mth.lerp(Math.abs(brightVal) * 2, hsb[2], 0.0);
			}
		}

		Color c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), color.getAlpha());
	}

	private static void registerCompiledTexture(NativeImage image, ResourceLocation key){
		try(image){
			if(Minecraft.getInstance().getTextureManager().getTexture(key, null) instanceof DynamicTexture texture){
				texture.setPixels(image);
				texture.upload();
			}else{
				DynamicTexture layer = new DynamicTexture(image);
				Minecraft.getInstance().getTextureManager().register(key, layer);
			}

//			System.out.println("Compiled skin " + key);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}