package by.jackraidenph.dragonsurvival.client.SkinCustomization;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.SyncPlayerCustomization;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DragonCustomizationHandler
{
	public static ResourceLocation getSkinTexture(PlayerEntity player, CustomizationLayer layer, String key, DragonType type){
		if(layer == CustomizationLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
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
	
	public static Texture getSkin(PlayerEntity player, CustomizationLayer layer, String key, DragonType type){
		if(layer == CustomizationLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
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
	
	@OnlyIn( Dist.CLIENT)
	public static void setSkinLayer(PlayerEntity player, CustomizationLayer layers, String key){
		NetworkHandler.CHANNEL.sendToServer(new SyncPlayerCustomization(player.getId(), layers, key));
	}
	
	public static ArrayList<String> getKeys(PlayerEntity player, CustomizationLayer layers){
		ArrayList<String> list = new ArrayList<>();
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		if(layers == CustomizationLayer.EXTRA || layers == CustomizationLayer.EXTRA1 || layers == CustomizationLayer.EXTRA2 || layers == CustomizationLayer.EXTRA3){
			if(handler != null){
				for(CustomizationLayer la : new CustomizationLayer[]{CustomizationLayer.EXTRA, CustomizationLayer.EXTRA1, CustomizationLayer.EXTRA2, CustomizationLayer.EXTRA3}){
				Texture[] texts = CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(handler.getType(), new HashMap<>()).getOrDefault(la, new Texture[0]);
					for(Texture texture : texts){
						list.add(texture.key);
					}
				}
			}
			
			return list;
		}
		
		if(handler != null){
			Texture[] texts = CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(handler.getType(), new HashMap<>()).getOrDefault(layers, new Texture[0]);
			for(Texture texture : texts){
				list.add(texture.key);
			}
		}
		
		return list;
	}
}
