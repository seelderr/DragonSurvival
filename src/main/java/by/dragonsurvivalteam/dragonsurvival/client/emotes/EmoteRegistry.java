package by.dragonsurvivalteam.dragonsurvival.client.emotes;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.eliotlash.mclib.math.Constant;
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
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.keyframe.BoneAnimation;
import software.bernie.geckolib.loading.object.BakedAnimations;

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
	public static void initEmoteRotation() {
//		BakedAnimations bakedAnimations = GeckoLibCache.getBakedAnimations().get(ClientDragonRender.dragonModel.getAnimationResource(null));
//
//		for (Emote emote : EMOTES) {
//			if (emote.mirror != null && emote.animation != null) {
//				Animation animation = bakedAnimations.getAnimation(emote.name);
//
//				if (animation == null) {
//					DragonSurvivalMod.LOGGER.warn("Emote animation [{}] could not be found", emote.name);
//					continue;
//				}
//
//				for (BoneAnimation bone : animation.boneAnimations()) {
//					if (emote.mirror.xPos) {
//						bone.positionKeyFrames().xKeyframes().forEach(keyFrame -> {
//							keyFrame.setStartValue(new Constant(keyFrame.getStartValue().get() * -1));
//							keyFrame.setEndValue(new Constant(keyFrame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.yPos) {
//						bone.positionKeyFrames().yKeyframes().forEach(keyFrame -> {
//							keyFrame.setStartValue(new Constant(keyFrame.getStartValue().get() * -1));
//							keyFrame.setEndValue(new Constant(keyFrame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.zPos) {
//						bone.positionKeyFrames().zKeyframes().forEach(Frame -> {
//							Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//							Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.xRot) {
//						bone.rotationKeyFrames().xKeyframes().forEach(Frame -> {
//							Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//							Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.yRot) {
//						bone.rotationKeyFrames().yKeyframes().forEach(Frame -> {
//							Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//							Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.zRot) {
//						bone.rotationKeyFrames().zKeyframes().forEach(Frame -> {
//							Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//							Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.xScale) {
//						bone.scaleKeyFrames().xKeyframes().forEach(Frame -> {
//							Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//							Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.yScale) {
//						bone.scaleKeyFrames().yKeyframes().forEach(Frame -> {
//							Frame.setStartValue(new Constant(Frame.getStartValue().get() * -1));
//							Frame.setEndValue(new Constant(Frame.getEndValue().get() * -1));
//						});
//					}
//
//					if (emote.mirror.zScale) {
//						bone.scaleKeyFrames().zKeyframes().forEach(keyFrame -> {
//							keyFrame.setStartValue(new Constant(keyFrame.getStartValue().get() * -1));
//							keyFrame.setEndValue(new Constant(keyFrame.getEndValue().get() * -1));
//						});
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