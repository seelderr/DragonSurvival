package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class DragonEditorHandler{
	public static ResourceLocation getSkinTexture(Player player, EnumSkinLayer layer, String key, AbstractDragonType type){
		if(Objects.equals(layer.name, "Extra") && layer != EnumSkinLayer.EXTRA){
			return getSkinTexture(player, EnumSkinLayer.EXTRA, key, type);
		}

		if(layer == EnumSkinLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
			return getSkinTexture(player, layer, type.getSubtypeNameLowerCase() + "_base_" + handler.getLevel().ordinal(), type);
		}

		Texture[] texts = DragonEditorRegistry.CUSTOMIZATIONS.getOrDefault(type.getTypeNameUpperCase(), new HashMap<>()).getOrDefault(layer, new Texture[0]);

		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return ResourceLocation.parse(texture.texture);
			}
		}

		return null;
	}

	public static Texture getSkin(Player player, EnumSkinLayer layer, String key, AbstractDragonType type){
		if(Objects.equals(layer.name, "Extra") && layer != EnumSkinLayer.EXTRA){
			return getSkin(player, EnumSkinLayer.EXTRA, key, type);
		}

		if(layer == EnumSkinLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			return getSkin(player, layer, type.getTypeNameLowerCase() + "_base_" + DragonUtils.getDragonLevel(player).ordinal(), type);
		}

		Texture[] texts = DragonEditorRegistry.CUSTOMIZATIONS.getOrDefault(type.getTypeNameUpperCase(), new HashMap<>()).getOrDefault(layer, new Texture[0]);

		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return texture;
			}
		}

		return null;
	}

	public static ArrayList<String> getKeys(AbstractDragonType type, AbstractDragonBody body, EnumSkinLayer layers){
		if(Objects.equals(layers.name, "Extra") && layers != EnumSkinLayer.EXTRA){
			return getKeys(type, body, EnumSkinLayer.EXTRA);
		}

		ArrayList<String> list = new ArrayList<>();

		Texture[] texts = DragonEditorRegistry.CUSTOMIZATIONS.getOrDefault(type.getTypeNameUpperCase(), new HashMap<>()).getOrDefault(layers, new Texture[0]);
		for(Texture texture : texts){
			if (texture.bodies == null || Arrays.asList(texture.bodies).contains(body.toString())) {
				list.add(texture.key);
			}
		}

		return list;
	}

	public static ArrayList<String> getKeys(Player player, EnumSkinLayer layers){
		return getKeys(DragonUtils.getDragonType(player), DragonUtils.getDragonBody(player), layers);
	}

	public static CompletableFuture<List<Pair<NativeImage, ResourceLocation>>> generateSkinTextures(final DragonEntity dragon) {
		Player player = dragon.getPlayer();
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
		Set<DragonLevel> dragonLevels = handler.getSkinData().skinPreset.skinAges.keySet();
		List<Pair<SkinAgeGroup, String>> skinAgeGroupsAndDragonLevels = new ArrayList<>();
		for (DragonLevel dragonLevel : dragonLevels) {
			skinAgeGroupsAndDragonLevels.add(new Pair<>(handler.getSkinData().skinPreset.skinAges.get(dragonLevel).get(), dragonLevel.name));
		}

		return CompletableFuture.supplyAsync(() -> {
			try {
				return genTextures(player, handler, skinAgeGroupsAndDragonLevels);
			} catch (Throwable e) {
				DragonSurvivalMod.LOGGER.error("An error occurred while compiling the dragon skin texture", e);
			}

			return new ArrayList<>();
		}, Util.backgroundExecutor());
	}

	private static List<Pair<NativeImage, ResourceLocation>> genTextures(final Player player, final DragonStateHandler handler, final List<Pair<SkinAgeGroup, String>> skinAgeGroupAndDragonLevels) throws IOException {
		List<Pair<NativeImage, ResourceLocation>> texturesToRegister = new ArrayList<>();
		for (Pair<SkinAgeGroup, String> skinAgeGroupAndDragonLevel : skinAgeGroupAndDragonLevels) {
			SkinAgeGroup skinAgeGroup = skinAgeGroupAndDragonLevel.getFirst();
			NativeImage normal = new NativeImage(512, 512, true);
			NativeImage glow = new NativeImage(512, 512, true);

			for (EnumSkinLayer layer : EnumSkinLayer.values()) {
				LayerSettings settings = skinAgeGroup.layerSettings.get(layer).get();
				String selectedSkin = settings.selectedSkin;

				if (selectedSkin != null) {
					Texture skinTexture = getSkin(player, layer, selectedSkin, handler.getType());

					if (skinTexture != null) {
						float hueVal = settings.hue - skinTexture.average_hue;
						float satVal = settings.saturation;
						float brightVal = settings.brightness;

						ResourceLocation textureLocation = getSkinTexture(player, layer, selectedSkin, handler.getType());
						Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(textureLocation);

						if (resource.isEmpty()) {
							throw new UncheckedIOException(new IOException(String.format("Resource %s not found!", textureLocation.getPath())));
						}

						InputStream textureStream = resource.get().open();
						NativeImage tempColorPicker = NativeImage.read(textureStream);

						textureStream.close();

						for (int x = 0; x < tempColorPicker.getWidth(); x++) {
							for (int y = 0; y < tempColorPicker.getHeight(); y++) {
								Color color = getColor(settings, skinTexture, hueVal, satVal, brightVal, tempColorPicker, x, y);

								if (color == null) {
									continue;
								}

								if (color.getAlpha() != 0) {
									Supplier<NativeImage> g2 = settings.glowing ? () -> glow : () -> normal;
									g2.get().setPixelRGBA(x, y, color.getRGB());

									if (settings.glowing && layer == EnumSkinLayer.BASE) {
										normal.setPixelRGBA(x, y, color.getRGB());
									}
								}
							}
						}

						tempColorPicker.close();
					}
				}
			}

			String uuid = player.getStringUUID();
			String dragonLevel = skinAgeGroupAndDragonLevel.getSecond();
			ResourceLocation dynamicNormalKey = ResourceLocation.fromNamespaceAndPath(MODID, "dynamic_normal_" + uuid + "_" + dragonLevel);
			ResourceLocation dynamicGlowKey = ResourceLocation.fromNamespaceAndPath(MODID, "dynamic_glow_" + uuid + "_" + dragonLevel);

			texturesToRegister.add(new Pair<>(normal, dynamicNormalKey));
			texturesToRegister.add(new Pair<>(glow, dynamicGlowKey));
		}

		return texturesToRegister;
	}

	@Nullable private static Color getColor(LayerSettings settings, Texture text, float hueVal, float satVal, float brightVal, NativeImage img, int x, int y){
		float[] hsb = new float[3];

		Color color = new Color(img.getPixelRGBA(x, y), true);
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

		if(text.colorable){
			if(settings.glowing && hsb[0] == 0.5f && hsb[1] == 0.5f){
				return null;
			}

            hsb[0] = hsb[0] - hueVal;
			hsb[1] = (float)Mth.lerp(Math.abs(satVal - 0.5f) * 2 , hsb[1], satVal > 0.5f ? 1.0 : 0.0);
			hsb[2] = (float)Mth.lerp(Math.abs(brightVal - 0.5f) * 2, hsb[2], brightVal > 0.5f ? 1.0 : 0.0);
		}

		Color c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), color.getAlpha());
	}
}