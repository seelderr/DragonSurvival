package by.jackraidenph.dragonsurvival.emotes;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import com.google.gson.Gson;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EmoteRegistry
{
	public static final ResourceLocation CLIENT_EMOTES = new ResourceLocation(DragonSurvivalMod.MODID, "emotes.json");
	public static final ArrayList<Emote> EMOTES = new ArrayList<>();
	
	@OnlyIn( Dist.CLIENT)
	@SubscribeEvent
	public static void clientStart(FMLClientSetupEvent event){
		EmoteRegistry.reload(Minecraft.getInstance().getResourceManager(), EmoteRegistry.CLIENT_EMOTES);
		
		if (Minecraft.getInstance().getResourceManager() instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(
					(IResourceManagerReloadListener) manager -> {
						EmoteRegistry.EMOTES.clear();
						EmoteRegistry.reload(Minecraft.getInstance().getResourceManager(), EmoteRegistry.CLIENT_EMOTES);
					});
		}
	}
	
	protected static void reload(IResourceManager manager, ResourceLocation location){
		try {
			Gson gson = new Gson();
			InputStream in = manager.getResource(location).getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			EmoteRegistryClass je = gson.fromJson(reader, EmoteRegistryClass.class);
			
			if(je != null){
				EMOTES.addAll(Arrays.asList(je.emotes));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class EmoteRegistryClass{
		public Emote[] emotes;
	}
}
