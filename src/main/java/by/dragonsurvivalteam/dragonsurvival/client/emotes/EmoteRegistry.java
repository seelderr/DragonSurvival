package by.dragonsurvivalteam.dragonsurvival.client.emotes;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class EmoteRegistry{
	public static final ResourceLocation CLIENT_EMOTES = new ResourceLocation(DragonSurvivalMod.MODID, "emotes.json");
	public static final ArrayList<Emote> EMOTES = new ArrayList<>();

	private static boolean hasStarted = false;

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void clientStart(FMLClientSetupEvent event){
		EmoteRegistry.reload(Minecraft.getInstance().getResourceManager(), EmoteRegistry.CLIENT_EMOTES);

		if(Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager){
			((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener)manager -> {
				EmoteRegistry.EMOTES.clear();
				EmoteRegistry.reload(Minecraft.getInstance().getResourceManager(), EmoteRegistry.CLIENT_EMOTES);
				initEmoteRotation();
			});
		}
	}

	protected static void reload(ResourceManager manager, ResourceLocation location){
		try{
			Gson gson = GsonFactory.getDefault();
			Resource resource = manager.getResource(location).orElse(null);
			if (resource == null)
				throw new RuntimeException(String.format("Resource '%s' not found!", location.getPath()));
			InputStream in = resource.open();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
				EmoteRegistryClass je = gson.fromJson(reader, EmoteRegistryClass.class);

				if (je != null) {
					List<Emote> emts = Arrays.asList(je.emotes);
					HashMap<String, Integer> nameCount = new HashMap<>();

					for (Emote emt : emts) {
						nameCount.putIfAbsent(emt.name, 0);
						nameCount.put(emt.name, nameCount.get(emt.name) + 1);
						emt.id = emt.name + "_" + nameCount.get(emt.name);
					}

					EMOTES.addAll(emts);
				}
			} catch (IOException exception) {
				DragonSurvivalMod.LOGGER.warn("Reader could not be closed", exception);
			}
		} catch (IOException exception) {
			DragonSurvivalMod.LOGGER.error("Resource [" + location + "] could not be opened", exception);
		}
	}

	// FIXME 1.20 :: Not sure what this was meant to do
	public static void initEmoteRotation(){
//		for(Emote emt : EMOTES){
//			if(emt.mirror != null && emt.animation != null){
//				AnimationFile animation = GeckoLibCache.getInstance().getAnimations().get(ClientDragonRender.dragonModel.getAnimationResource(null));
//
//				if(animation != null){
//					Animation an = animation.getAnimation(emt.animation);
//
//					for(BoneAnimation bone : an.boneAnimations){
//						if(emt.mirror.xPos){
//							bone.positionKeyFrames.xKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.yPos){
//							bone.positionKeyFrames.yKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.zPos){
//							bone.positionKeyFrames.zKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.xRot){
//							bone.rotationKeyFrames.xKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.yRot){
//							bone.rotationKeyFrames.yKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.zRot){
//							bone.rotationKeyFrames.zKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.xScale){
//							bone.scaleKeyFrames.xKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.yScale){
//							bone.scaleKeyFrames.yKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//
//						if(emt.mirror.zScale){
//							bone.scaleKeyFrames.zKeyFrames.forEach(Frame -> {
//								Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//								Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//							});
//						}
//					}
//				}
//			}
//		}
	}

	@Mod.EventBusSubscriber( Dist.CLIENT )
	public static class clientStart{
		@OnlyIn( Dist.CLIENT )
		@SubscribeEvent
		public static void clientStart(EntityJoinLevelEvent event){
			if(!hasStarted){
				initEmoteRotation();
				hasStarted = true;
			}
		}
	}

	public static class EmoteRegistryClass{
		public Emote[] emotes;
	}
}