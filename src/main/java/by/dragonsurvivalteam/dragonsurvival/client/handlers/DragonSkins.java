package by.dragonsurvivalteam.dragonsurvival.client.handlers;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT )
public class DragonSkins{
	public static class SkinObject{
		public String id;
		public String name;
		public String short_name;
		public int size;
		public boolean glow;
	}

	public static boolean useInChina = false;
	public static final String SKINS = "https://raw.githubusercontent.com/DragonSurvivalTeam/DragonSurvival/master/src/test/resources/";
	private static final String GITHUB_API = "https://api.github.com/repositories/280658566/contents/src/test/resources?ref=master";
	public static final String CHINA_SKINS = "https://gitcode.net/api/v4/projects/mirrors%%2FDragonSurvivalTeam%%2FDragonSurvival/repository/blobs/%s/raw?ref=master";
	private static final String GITCODE_API = "https://gitcode.net/api/v4/projects/mirrors%2FDragonSurvivalTeam%2FDragonSurvival/repository/tree?ref=master&path=src/test/resources&per_page=100&page=";
	private static final String CONNECTIVITY_TEST_URL = "https://raw.githubusercontent.com/DragonSurvivalTeam/DragonSurvival/master/README.md";

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

	protected static ResourceLocation fetchSkinFileInGitcode(String playerName, DragonLevel dragonStage, String... extra) {
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
		try (SimpleTexture simpleTexture =new SimpleTexture(resourceLocation)){
			if (Minecraft.getInstance().getTextureManager().getTexture(resourceLocation, simpleTexture) != simpleTexture)
				return resourceLocation;
		}

		try{
			InputStream inputStream = getStream(new URL(String.format(CHINA_SKINS, skin.id)), 15 * 1000);
			NativeImage customTexture = NativeImage.read(inputStream);
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

	protected static ResourceLocation fetchSkinFileInGithub(String playerName, DragonLevel dragonStage, String... extra) {
		ResourceLocation resourceLocation;
		String playerKey = playerName + "_" + dragonStage.name;
		String[] text = ArrayUtils.addAll(new String[]{playerKey}, extra);
		String searchText = StringUtils.join(text, "_");

		try{
			InputStream inputStream = getStream(new URL(SKINS + searchText + ".png"), 15 * 1000);
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

	public static ResourceLocation fetchSkinFile(String playerName, DragonLevel dragonStage, String... extra) {

		if (useInChina){
			return fetchSkinFileInGitcode(playerName, dragonStage, extra);
		}else{
			return fetchSkinFileInGithub(playerName, dragonStage, extra);
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

	@SubscribeEvent
	public static void onReloadEvent(AddReloadListenerEvent reloadEvent)
	{
		DragonSkins.init();
	}
	public static void init() {
		invalidateSkins();
		String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
		if (!currentLanguage.equals("zh_cn"))
		{
			if (!initFromGithub())
				initFromGitcode();
		}else{
			if (!initFromGitcode())
				initFromGithub();
		}
	}

	public static void parseSkinObjects(SkinObject[] skinObjects) {
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

	public static boolean initFromGitcode() {
		int page = 1;
		try{
			while(true){
				Gson gson = GsonFactory.getDefault();
				URL url = new URL(GITCODE_API + page);
				BufferedReader reader = new BufferedReader(new InputStreamReader(getStream(url, 2*1000)));
				SkinObject[] je = gson.fromJson(reader, SkinObject[].class);
				if (je.length == 0)
					break;

				parseSkinObjects(je);
				++page;
			}
			useInChina = true;
			return true;
		}catch(IOException e){
			DragonSurvivalMod.LOGGER.log(Level.WARN, "Failed to get skin information in Gitcode.");
			return false;
		}
	}

	public static boolean tryConnectGithub()
	{
		try {
			int data = getStream(new URL(CONNECTIVITY_TEST_URL), 1000).read();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean initFromGithub(){
		if (!tryConnectGithub())
			return false;
		try{
			Gson gson = GsonFactory.getDefault();
			URL url = new URL(GITHUB_API);
			BufferedReader reader = new BufferedReader(new InputStreamReader(getStream(url, 2*1000)));
			SkinObject[] je = gson.fromJson(reader, SkinObject[].class);
			parseSkinObjects(je);
			useInChina = false;
			return true;
		}catch(IOException e) {
			DragonSurvivalMod.LOGGER.log(Level.WARN, "Failed to get skin information in Github.");
			return false;
		}
	}

	private static InputStream getStream(URL url, int timeout) throws IOException{
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setConnectTimeout(timeout);
		huc.setDoInput(true);
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getInputStream();
	}

	private static void invalidateSkins()
	{
		SKIN_USERS.clear();
		playerSkinCache.clear();
		playerGlowCache.clear();
		hasFailedFetch.clear();
	}
}