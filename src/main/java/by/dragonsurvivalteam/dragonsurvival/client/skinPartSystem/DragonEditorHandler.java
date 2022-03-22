package by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem;

import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.DragonEditorObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DragonEditorHandler{
	public static ResourceLocation getSkinTexture(PlayerEntity player, EnumSkinLayer layer, String key, DragonType type){
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

	public static Texture getSkin(PlayerEntity player, EnumSkinLayer layer, String key, DragonType type){
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

	public static ArrayList<String> getKeys(PlayerEntity player, EnumSkinLayer layers){
		return getKeys(DragonUtils.getDragonType(player), layers);
	}
}