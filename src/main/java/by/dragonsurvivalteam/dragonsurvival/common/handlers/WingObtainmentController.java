package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragons;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.bridge.game.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber
public class WingObtainmentController{

	private static final Map<String, Integer> dragonPhrases = new HashMap<String, Integer>();

	private static final UUID enderDragonUUID = UUID.fromString("426642b9-2e88-4350-afa8-f99f75af5479");

	public static void loadDragonPhrases(){
		if(FMLLoader.getDist() != Dist.CLIENT){
			return;
		}

		try{
			List<String> langs = new ArrayList<>();
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/dragonsurvival/lang");
			if(stream == null){
				return;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String file;
			while((file = reader.readLine()) != null){
				langs.add(file);
			}
			reader.close();
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, String>>(){
			}.getType();
			for(String langFile : langs){
				URL resource = Thread.currentThread().getContextClassLoader().getResource("assets/dragonsurvival/lang/" + langFile);
				Map<String, String> langData = gson.fromJson(new String(Files.readAllBytes(Paths.get(resource.toURI()))), type);
				int phraseCount = 0;
				for(String key : langData.keySet()){
					if(key.contains("ds.endmessage")){
						phraseCount++;
					}
				}
				if(phraseCount > 0){
					dragonPhrases.put(langFile.replace(".json", ""), phraseCount);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	@SubscribeEvent
	public static void inTheEnd(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent){
		Player player = changedDimensionEvent.getEntity();

		if(changedDimensionEvent.getTo() == Level.END){
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				if(dragonStateHandler.isDragon() && !dragonStateHandler.getMovementData().spinLearned && ServerFlightHandler.enderDragonGrantsSpin){
					executorService.schedule(() -> player.sendSystemMessage(Component.empty().append("ds.endmessage")), 3, TimeUnit.SECONDS);
				}
			});
		}
	}

	@SubscribeEvent
	public static void clientMessageRecieved(ClientChatReceivedEvent event){
		if(event.getMessageSigner().profileId().equals(enderDragonUUID)){
			if(event.getMessage().getString().equals("ds.endmessage")){
				Language language = Minecraft.getInstance().getLanguageManager().getSelected();
				LocalPlayer player = Minecraft.getInstance().player;
				int messageId = player.getRandom().nextInt(dragonPhrases.getOrDefault(language.getCode(), dragonPhrases.getOrDefault("en_us", 1))) + 1;
				event.setMessage(Component.translatable("ds.endmessage." + messageId, player.getDisplayName().getString()));
			}else if(event.getMessage().getString().equals("ds.dragon.grants.wings")){
				event.setMessage(Component.translatable("ds.dragon.grants.wings"));
			}
		}
	}


	@SubscribeEvent
	public static void serverChatEvent(ServerChatEvent chatEvent){
		Component message = chatEvent.getMessage();
		ServerPlayer player = chatEvent.getPlayer();
		String lowercase = message.getString().toLowerCase();
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && !dragonStateHandler.getMovementData().spinLearned && ServerFlightHandler.enderDragonGrantsSpin){
				if(player.getLevel().dimension() == Level.END){
					if(!player.getLevel().getDragons().isEmpty()){
						if(!lowercase.isEmpty()){
							executorService.schedule(() -> player.sendSystemMessage(Component.empty().append("ds.dragon.grants.wings")), 2, TimeUnit.SECONDS);

							dragonStateHandler.setHasWings(true);
							dragonStateHandler.getMovementData().spinLearned = true;
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSpinStatus(player.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
							NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new CompleteDataSync(player));
						}
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void teleportAway(LivingDamageEvent damageEvent){
		if(!ServerConfig.endVoidTeleport){
			return;
		}
		LivingEntity living = damageEvent.getEntity();
		if(living instanceof Player){
			DamageSource damageSource = damageEvent.getSource();
			if(living.level.dimension() == Level.END && damageSource == DamageSource.OUT_OF_WORLD && living.position().y < -60){
				DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
					if(dragonStateHandler.isDragon()){
						living.changeDimension(living.getServer().overworld());
						NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new RefreshDragons(living.getId()));
						damageEvent.setCanceled(true);
					}
				});
			}
		}
	}
}