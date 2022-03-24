package by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.DragonEditorObject;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.DragonEditorObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SavedSkinPresets;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.*;
import java.util.HashMap;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DragonEditorRegistry{
	public static final String SAVED_FILE_NAME = "saved_customizations.json";
	public static final ResourceLocation CUSTOMIZATION = new ResourceLocation(DragonSurvivalMod.MODID, "customization.json");
	public static final HashMap<DragonType, HashMap<EnumSkinLayer, Texture[]>> CUSTOMIZATIONS = new HashMap<>();
	public static SavedSkinPresets savedCustomizations = null;
	public static HashMap<DragonType, HashMap<DragonLevel, HashMap<EnumSkinLayer, String>>> defaultSkinValues = new HashMap<>();
	public static File folder;
	public static File savedFile;
	private static final boolean init = false;

	public static String getDefaultPart(DragonType type, DragonLevel level, EnumSkinLayer layer){
		return defaultSkinValues.getOrDefault(type, new HashMap<>()).getOrDefault(level, new HashMap<>()).getOrDefault(layer, SkinCap.defaultSkinValue);
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void clientStart(FMLClientSetupEvent event){
		DragonEditorRegistry.reload(Minecraft.getInstance().getResourceManager(), CUSTOMIZATION);

		if(Minecraft.getInstance().getResourceManager() instanceof IReloadableResourceManager){
			((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener((IResourceManagerReloadListener)manager -> {
				CUSTOMIZATIONS.clear();
				DragonEditorRegistry.reload(Minecraft.getInstance().getResourceManager(), CUSTOMIZATION);
			});
		}

		folder = new File(Minecraft.getInstance().gameDirectory + "/config/dragon-survival/");
		savedFile = new File(folder + "/" + SAVED_FILE_NAME);

		if(!folder.exists()){
			folder.mkdirs();
		}

		if(!savedFile.exists()){
			try{
				savedFile.createNewFile();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				savedCustomizations = new SavedSkinPresets();

				for(DragonType type : DragonType.values()){
					if(type == DragonType.NONE){
						continue;
					}

					savedCustomizations.skinPresets.computeIfAbsent(type, (b) -> new HashMap<>());
					savedCustomizations.current.computeIfAbsent(type, (b) -> new HashMap<>());

					for(int i = 0; i < 9; i++){
						savedCustomizations.skinPresets.get(type).computeIfAbsent(i, (b) -> {
							SkinPreset preset = new SkinPreset();
							preset.initDefaults(type);
							return preset;
						});
					}

					for(DragonLevel level : DragonLevel.values()){
						savedCustomizations.current.get(type).put(level, 0);
					}
				}

				FileWriter writer = new FileWriter(savedFile);
				gson.toJson(savedCustomizations, writer);
				writer.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		if(savedFile.exists()){
			try{
				Gson gson = new Gson();
				InputStream in = new FileInputStream(savedFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				savedCustomizations = gson.fromJson(reader, SavedSkinPresets.class);
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}

	protected static void reload(IResourceManager manager, ResourceLocation location){
		try{
			Gson gson = new Gson();
			InputStream in = manager.getResource(location).getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			DragonEditorObject je = gson.fromJson(reader, DragonEditorObject.class);

			CUSTOMIZATIONS.computeIfAbsent(DragonType.SEA, (type) -> new HashMap<>());
			CUSTOMIZATIONS.computeIfAbsent(DragonType.CAVE, (type) -> new HashMap<>());
			CUSTOMIZATIONS.computeIfAbsent(DragonType.FOREST, (type) -> new HashMap<>());

			dragonType(DragonType.SEA, je.sea_dragon);
			dragonType(DragonType.CAVE, je.cave_dragon);
			dragonType(DragonType.FOREST, je.forest_dragon);

			defaultSkinValues = je.defaults;
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void dragonType(DragonType type, DragonEditorObject.Dragon je){
		if(je != null){
			if(je.layers != null){
				je.layers.forEach((layer, keys) -> {
					for(Texture key : keys){
						if(key.key == null){
							key.key = key.texture.substring(key.texture.lastIndexOf("/") + 1);
							key.key = key.key.substring(0, key.key.lastIndexOf("."));
						}
					}
					CUSTOMIZATIONS.get(type).put(layer, keys);
				});
			}
		}
	}
}