package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public class DragonSkins{
	public static final String SKINS = "https://raw.githubusercontent.com/DragonSurvivalTeam/DragonSurvival/master/src/test/resources/";
	private static final String GITHUB_API = "https://api.github.com/repositories/280658566/contents/src/test/resources?ref=master";

	public static HashMap<DragonLevel, ArrayList<String>> SKIN_USERS = new HashMap<>();

	public static HashMap<String, ResourceLocation> playerSkinCache = new HashMap<>();
	public static HashMap<String, ResourceLocation> playerGlowCache = new HashMap<>();

	private static final ArrayList<String> hasFailedFetch = new ArrayList<>();

	public static ResourceLocation getPlayerSkin(String playerKey){
		if(playerSkinCache.containsKey(playerKey) && playerSkinCache.get(playerKey) != null){
			return playerSkinCache.get(playerKey);
		}

		if(!hasFailedFetch.contains(playerKey) && !playerSkinCache.containsKey(playerKey)){
			ResourceLocation texture = fetchSkinFile(playerKey);

			if(texture != null){
				playerSkinCache.put(playerKey, texture);
				return texture;
			}
		}

		return null;
	}

	public static ResourceLocation fetchSkinFile(String playerKey, String... extra){
		ResourceLocation resourceLocation = null;
		String[] text = ArrayUtils.addAll(new String[]{playerKey}, extra);
		String searchText = StringUtils.join(text, "_");

		try{
			URL url = new URL(SKINS + searchText + ".png");
			InputStream inputStream = url.openConnection().getInputStream();
			NativeImage customTexture = NativeImage.read(inputStream);
			resourceLocation = new ResourceLocation(DragonSurvivalMod.MODID, searchText.toLowerCase(Locale.ROOT));
			Minecraft.getInstance().getTextureManager().register(resourceLocation, new DynamicTexture(customTexture));
		}catch(IOException e){
			if(extra == null || extra.length == 0){ //Fetching glow layer failing must not affect normal skin fetches
				if(!hasFailedFetch.contains(playerKey)){
					DragonSurvivalMod.LOGGER.info("Custom skin for user {} doesn't exist", playerKey);
					hasFailedFetch.add(playerKey);
				}
			}

			return null;
		}

		return resourceLocation;
	}

	public static ResourceLocation getPlayerGlow(String playerKey){
		if(playerGlowCache.containsKey(playerKey)){
			return playerGlowCache.get(playerKey);
		}else{
			ResourceLocation texture = fetchSkinFile(playerKey, "glow");
			playerGlowCache.put(playerKey, texture);
			return texture;
		}
	}


	public static ResourceLocation getPlayerSkin(PlayerEntity player, DragonType type, DragonLevel dragonStage){
		ResourceLocation texture = null;
		String playerKey = player.getGameProfile().getName() + "_" + dragonStage.name;

		boolean renderStage = renderStage(player, dragonStage);

		if((ConfigHandler.CLIENT.renderOtherPlayerSkins.get() || player == Minecraft.getInstance().player) && renderStage){
			if(playerSkinCache.containsKey(playerKey) && playerSkinCache.get(playerKey) != null){
				return playerSkinCache.get(playerKey);
			}

			if(!hasFailedFetch.contains(playerKey) && !playerSkinCache.containsKey(playerKey)){
				texture = fetchSkinFile(player, dragonStage);

				if(texture != null){
					playerSkinCache.put(playerKey, texture);
				}
			}
		}

		return texture;
	}

	public static boolean renderStage(PlayerEntity player, DragonLevel level){
		DragonStateHandler handler = DragonUtils.getHandler(player);

		if(handler != null){
			switch(level){
				case BABY:
					return handler.getSkin().renderNewborn;

				case YOUNG:
					return handler.getSkin().renderYoung;

				case ADULT:
					return handler.getSkin().renderAdult;
			}
		}

		return false;
	}

	public static ResourceLocation fetchSkinFile(PlayerEntity playerEntity, DragonLevel dragonStage, String... extra){
		ResourceLocation resourceLocation = null;
		String name = playerEntity.getGameProfile().getName();
		String playerKey = playerEntity.getGameProfile().getName() + "_" + dragonStage.name;

		String[] text = ArrayUtils.addAll(new String[]{name, dragonStage.name}, extra);
		String searchText = StringUtils.join(text, "_");

		try{
			URL url = new URL(SKINS + searchText + ".png");
			InputStream inputStream = url.openConnection().getInputStream();
			NativeImage customTexture = NativeImage.read(inputStream);
			resourceLocation = new ResourceLocation(DragonSurvivalMod.MODID, searchText.toLowerCase(Locale.ROOT));
			Minecraft.getInstance().getTextureManager().register(resourceLocation, new DynamicTexture(customTexture));
		}catch(IOException e){
			if(extra == null || extra.length == 0){ //Fetching glow layer failing must not affect normal skin fetches
				if(!hasFailedFetch.contains(playerKey)){
					DragonSurvivalMod.LOGGER.info("Custom skin for user {} doesn't exist", name);
					hasFailedFetch.add(playerKey);
				}
			}

			return null;
		}

		return resourceLocation;
	}

	public static ResourceLocation getGlowTexture(PlayerEntity player, DragonType type, DragonLevel dragonStage){
		ResourceLocation texture = null;
		String playerKey = player.getGameProfile().getName() + "_" + dragonStage.name;
		boolean renderStage = renderStage(player, dragonStage);


		if((ConfigHandler.CLIENT.renderOtherPlayerSkins.get() || player == Minecraft.getInstance().player) && playerSkinCache.containsKey(playerKey) && renderStage){
			if(playerGlowCache.containsKey(playerKey)){
				return playerGlowCache.get(playerKey);
			}else{
				texture = fetchSkinFile(player, dragonStage, "glow");
				playerGlowCache.put(playerKey, texture);

				if(texture == null){
					DragonSurvivalMod.LOGGER.info("Custom glow for user {} doesn't exist", player.getGameProfile().getName());
				}
			}
		}

		return texture;
	}

	private static ResourceLocation constructTexture(DragonType dragonType, DragonLevel stage, String... extra){
		String[] text = ArrayUtils.addAll(new String[]{dragonType.name().toLowerCase(Locale.ROOT), stage.name}, extra);
		Collection<ResourceLocation> rs = Minecraft.getInstance().getResourceManager().listResources("textures/dragon/", (s) -> true);
		return rs.stream().filter((s) -> s.getNamespace().equals(DragonSurvivalMod.MODID) && s.getPath().equals("textures/dragon/" + StringUtils.join(text, "_") + ".png")).findFirst().orElse(null);
	}

	public static void init(){
		try{
			Gson gson = new Gson();
			URL url = new URL(GITHUB_API);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			SkinObject[] je = gson.fromJson(reader, SkinObject[].class);

			for(SkinObject skin : je){
				String skinName = skin.name;

				if(skinName.contains("_")){
					String name = skinName.substring(0, skinName.lastIndexOf("_"));
					String level = skinName.substring(skinName.lastIndexOf("_") + 1, skinName.indexOf("."));
					DragonLevel size = level.equalsIgnoreCase("adult") ? DragonLevel.ADULT : level.equalsIgnoreCase("young") ? DragonLevel.YOUNG : level.equalsIgnoreCase("newborn") ? DragonLevel.BABY : null;

					if(size != null){
						if(!SKIN_USERS.containsKey(size)){
							SKIN_USERS.put(size, new ArrayList<>());
						}

						if(!SKIN_USERS.get(size).contains(name)){
							SKIN_USERS.get(size).add(name);
						}
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static class SkinObject{
		public String name;
		public int size;
		public String html_url;
	}
}