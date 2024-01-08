package by.dragonsurvivalteam.dragonsurvival.client.skins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class DragonSkins{
	public static NetSkinLoader skinLoader = new GithubSkinLoader();
	private static final ArrayList<String> hasFailedFetch = new ArrayList<>();
	public static HashMap<DragonLevel, HashMap<String, SkinObject>> SKIN_USERS = new HashMap<>();
	public static HashMap<String, ResourceLocation> playerSkinCache = new HashMap<>();
	public static HashMap<String, ResourceLocation> playerGlowCache = new HashMap<>();

	public static ResourceLocation getPlayerSkin(String playerName, DragonLevel level){
		String skinKey = playerName + "_" + level.name;
		if(playerSkinCache.containsKey(skinKey) && playerSkinCache.get(skinKey) != null){
			return playerSkinCache.get(skinKey);
		}

		if(!hasFailedFetch.contains(skinKey) && !playerSkinCache.containsKey(skinKey)){
			ResourceLocation texture = fetchSkinFile(playerName, level);

			if(texture != null){
				playerSkinCache.put(skinKey, texture);
				return texture;
			}
		}

		return null;
	}

	public static ResourceLocation getPlayerGlow(String playerName, DragonLevel level){
		String skinKey = playerName + "_" + level.name;
		if(playerGlowCache.containsKey(skinKey)){
			return playerGlowCache.get(skinKey);
		}else{
			ResourceLocation texture = fetchSkinFile(playerName, level, "glow");
			playerGlowCache.put(skinKey, texture);
			return texture;
		}
	}


	public static ResourceLocation getPlayerSkin(Player player, AbstractDragonType type, DragonLevel dragonStage){
		ResourceLocation texture = null;
		String playerKey = player.getGameProfile().getName() + "_" + dragonStage.name;

		boolean renderStage = renderStage(player, dragonStage);

		if((ClientDragonRender.renderOtherPlayerSkins || player == Minecraft.getInstance().player) && renderStage){
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

	public static ResourceLocation fetchSkinFile(String playerName, DragonLevel dragonStage, String... extra) {
		ResourceLocation resourceLocation;
		String playerKey = playerName + "_" + dragonStage.name;
		String[] text = ArrayUtils.addAll(new String[]{playerKey}, extra);
		String resourceName = StringUtils.join(text, "_");

		HashMap<String, SkinObject> playerSkinMap = SKIN_USERS.getOrDefault(dragonStage, null);
		if (playerSkinMap == null)
			return null;

		String skinName = StringUtils.join(ArrayUtils.addAll(new String[]{playerName}, extra), "_");
		SkinObject skin = playerSkinMap.getOrDefault(skinName, null);
		if (skin == null)
			return null;

		resourceLocation = new ResourceLocation(DragonSurvivalMod.MODID, resourceName.toLowerCase(Locale.ROOT));
		try (SimpleTexture simpleTexture = new SimpleTexture(resourceLocation)){
			if (Minecraft.getInstance().getTextureManager().getTexture(resourceLocation, simpleTexture) != simpleTexture)
				return resourceLocation;
		}
		try(InputStream imageStream = skinLoader.querySkinImage(skin)) {
			NativeImage customTexture = NativeImage.read(imageStream);
			Minecraft.getInstance().getTextureManager().register(resourceLocation, new DynamicTexture(customTexture));
			return resourceLocation;
		} catch (IOException e) {
			if(extra == null || extra.length == 0){ //Fetching glow layer failing must not affect normal skin fetches
				if(!hasFailedFetch.contains(playerKey)){
					DragonSurvivalMod.LOGGER.info("Custom skin for user {} doesn't exist", playerKey);
					hasFailedFetch.add(playerKey);
				}
			}
			return null;
		}
	}

	public static ResourceLocation fetchSkinFile(Player playerEntity, DragonLevel dragonStage, String... extra){
		return fetchSkinFile(playerEntity.getGameProfile().getName(), dragonStage, extra);
	}

	public static boolean renderStage(Player player, DragonLevel level){
		DragonStateHandler handler = DragonUtils.getHandler(player);

		return switch(level){
			case NEWBORN -> handler.getSkinData().renderNewborn;
			case YOUNG -> handler.getSkinData().renderYoung;
			case ADULT -> handler.getSkinData().renderAdult;
		};
	}

	public static ResourceLocation getGlowTexture(Player player, AbstractDragonType type, DragonLevel dragonStage){
		ResourceLocation texture = null;
		String playerKey = player.getGameProfile().getName() + "_" + dragonStage.name;
		boolean renderStage = renderStage(player, dragonStage);


		if((ClientDragonRender.renderOtherPlayerSkins || player == Minecraft.getInstance().player) && playerSkinCache.containsKey(playerKey) && renderStage){
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

	public static void init() {
		Collection<SkinObject> skins;
		invalidateSkins();
		String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
		NetSkinLoader first, second;
		if (currentLanguage.equals("zh_cn")) {
			first = new GitcodeSkinLoader();
			second = new GithubSkinLoader();
		} else{
			first = new GithubSkinLoader();
			second = new GitcodeSkinLoader();
		}
		if (!first.ping()) {
			if (!second.ping())
			{
				DragonSurvivalMod.LOGGER.warn("Unable to connect to skin database.");
				return;
			}
			first = second;
		}
		skinLoader = first;
        skins = skinLoader.querySkinList();
		if (skins != null)
			parseSkinObjects(skins);
	}

	public static void parseSkinObjects(Collection<SkinObject> skinObjects) {
		for(SkinObject skin : skinObjects){
			boolean isGlow = false;
			String skinName = skin.name;

			skinName = skin.name.substring(0, skinName.indexOf("."));
			if (skinName.endsWith("_glow")){
				isGlow = true;
				skinName = skin.name.substring(0, skinName.lastIndexOf("_"));
			}
			if(skinName.contains("_")){
				String name = skinName.substring(0, skinName.lastIndexOf("_"));
				if (isGlow)
					name += "_glow";

				String level = skinName.substring(skinName.lastIndexOf("_") + 1);
				DragonLevel size = level.equalsIgnoreCase("adult") ? DragonLevel.ADULT : level.equalsIgnoreCase("young") ? DragonLevel.YOUNG : level.equalsIgnoreCase("newborn") ? DragonLevel.NEWBORN : null;

				if(size != null){
					if(!SKIN_USERS.containsKey(size)){
						SKIN_USERS.put(size, new HashMap<>());
					}
					skin.short_name = name;
					skin.glow = isGlow;
					SKIN_USERS.get(size).putIfAbsent(name, skin);
				}
			}
		}
	}

	private static void invalidateSkins() {
		SKIN_USERS.clear();
		playerSkinCache.clear();
		playerGlowCache.clear();
		hasFailedFetch.clear();
	}
}