package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragons;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.bridge.game.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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


	@SubscribeEvent
	public static void inTheEnd(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent){
		Player player = changedDimensionEvent.getPlayer();
		if(changedDimensionEvent.getTo() == Level.END){
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				if(dragonStateHandler.isDragon() && !dragonStateHandler.getMovementData().spinLearned && ConfigHandler.SERVER.enderDragonGrantsSpin.get()){
					Thread thread = new Thread(() -> {
						try{
							Thread.sleep(3000);
							player.sendMessage(new TextComponent("ds.endmessage"), enderDragonUUID);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					});
					thread.start();
				}
			});
		}
	}

	@SubscribeEvent
	public static void clientMessageRecieved(ClientChatReceivedEvent event){
		DragonSurvivalMod.LOGGER.info(event.getMessage().getString());
		if(event.getSenderUUID().equals(enderDragonUUID)){
			if(event.getMessage().getString().equals("ds.endmessage")){
				Language language = Minecraft.getInstance().getLanguageManager().getSelected();
				LocalPlayer player = Minecraft.getInstance().player;
				int messageId = player.getRandom().nextInt(dragonPhrases.getOrDefault(language.getCode(), dragonPhrases.getOrDefault("en_us", 1))) + 1;
				event.setMessage(new TranslatableComponent("ds.endmessage." + messageId, player.getDisplayName().getString()));
			}else if(event.getMessage().getString().equals("ds.dragon.grants.wings")){
				event.setMessage(new TranslatableComponent("ds.dragon.grants.wings"));
			}
		}
	}


	@SubscribeEvent
	public static void serverChatEvent(ServerChatEvent chatEvent){
		String message = chatEvent.getMessage();
		ServerPlayer player = chatEvent.getPlayer();
		String lowercase = message.toLowerCase();
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && !dragonStateHandler.getMovementData().spinLearned && ConfigHandler.SERVER.enderDragonGrantsSpin.get()){
				if(player.getLevel().dimension() == Level.END){
					if(!player.getLevel().getDragons().isEmpty()){
						if(!lowercase.isEmpty()){
							Thread thread = new Thread(() -> {
								try{
									Thread.sleep(2000);
									player.sendMessage(new TextComponent("ds.dragon.grants.wings"), enderDragonUUID);
								}catch(InterruptedException e){
									e.printStackTrace();
								}
							});
							thread.start();
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
		if(!ConfigHandler.COMMON.endVoidTeleport.get()){
			return;
		}
		LivingEntity living = damageEvent.getEntityLiving();
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