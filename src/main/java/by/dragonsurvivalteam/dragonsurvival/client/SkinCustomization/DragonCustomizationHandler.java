package by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization;

import by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.SkinCustomization.SyncPlayerCustomization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DragonCustomizationHandler{
	public static ResourceLocation getSkinTexture(Player player, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer layer, String key, DragonType type){
		if(layer == by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			return getSkinTexture(player, layer, type.name().toLowerCase() + "_base_" + handler.getLevel().ordinal(), type);
		}

		Texture[] texts = by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layer, new Texture[0]);

		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return new ResourceLocation(texture.texture);
			}
		}

		return null;
	}

	public static Texture getSkin(Player player, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer layer, String key, DragonType type){
		if(layer == by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.BASE && (key.equalsIgnoreCase("Skin") || key.equalsIgnoreCase(SkinCap.defaultSkinValue))){
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			return getSkin(player, layer, type.name().toLowerCase() + "_base_" + handler.getLevel().ordinal(), type);
		}

		Texture[] texts = by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(type, new HashMap<>()).getOrDefault(layer, new Texture[0]);

		for(Texture texture : texts){
			if(Objects.equals(texture.key, key)){
				return texture;
			}
		}

		return null;
	}

	@OnlyIn( Dist.CLIENT )
	public static void setSkinLayer(Player player, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer layers, String key){
		NetworkHandler.CHANNEL.sendToServer(new SyncPlayerCustomization(player.getId(), layers, key));
	}

	public static ArrayList<String> getKeys(Player player, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer layers){
		ArrayList<String> list = new ArrayList<>();
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);

		if(layers == by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA || layers == by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA1 || layers == by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA2 || layers == by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA3){
			if(handler != null){
				for(by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer la : new by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer[]{by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA1, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA2, by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationLayer.EXTRA3}){
					Texture[] texts = by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(handler.getType(), new HashMap<>()).getOrDefault(la, new Texture[0]);
					for(Texture texture : texts){
						list.add(texture.key);
					}
				}
			}

			return list;
		}

		if(handler != null){
			Texture[] texts = by.dragonsurvivalteam.dragonsurvival.client.SkinCustomization.CustomizationRegistry.CUSTOMIZATIONS.getOrDefault(handler.getType(), new HashMap<>()).getOrDefault(layers, new Texture[0]);
			for(Texture texture : texts){
				list.add(texture.key);
			}
		}

		return list;
	}
}