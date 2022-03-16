package by.jackraidenph.dragonsurvival.client.skinPartSystem;

import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DragonCustomizationHandler
{
	public static ResourceLocation getSkinTexture(PlayerEntity player, EnumSkinLayer layer, String key, DragonType type){
		if(Objects.equals(layer.name, "Extra")){
			layer = EnumSkinLayer.EXTRA;
		}
		
		if(layer == EnumSkinLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			return getSkinTexture(player, layer, type.name().toLowerCase() + "_base_" + handler.getLevel().ordinal(), type);
		}
		
		Texture[] texts = CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layer, new Texture[0]);
		
		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return new ResourceLocation(texture.texture);
			}
		}
		
		return null;
	}
	
	public static Texture getSkin(PlayerEntity player, EnumSkinLayer layer, String key, DragonType type){
		if(Objects.equals(layer.name, "Extra")){
			layer = EnumSkinLayer.EXTRA;
		}
		
		if(layer == EnumSkinLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			return getSkin(player, layer, type.name().toLowerCase() + "_base_" + handler.getLevel().ordinal(), type);
		}
		
		Texture[] texts = CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layer, new Texture[0]);
		
		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return texture;
			}
		}
		
		return null;
	}
	
	public static ArrayList<String> getKeys(DragonType type, EnumSkinLayer layers){
		if(Objects.equals(layers.name, "Extra")){
			layers = EnumSkinLayer.EXTRA;
		}
		ArrayList<String> list = new ArrayList<>();
		
		Texture[] texts = CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layers, new Texture[0]);
		for(Texture texture : texts){
			list.add(texture.key);
		}
		
		return list;
	}
	
	public static ArrayList<String> getKeys(PlayerEntity player, EnumSkinLayer layers){
		return getKeys(DragonStateProvider.getCap(player).orElse(null).getType(), layers);
	}
}
