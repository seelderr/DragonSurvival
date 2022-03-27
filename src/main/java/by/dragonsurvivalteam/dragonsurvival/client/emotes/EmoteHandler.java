package by.dragonsurvivalteam.dragonsurvival.client.emotes;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.emotes.SyncEmote;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static net.minecraft.client.CameraType.THIRD_PERSON_BACK;


@OnlyIn( Dist.CLIENT )
@Mod.EventBusSubscriber( Dist.CLIENT )
public class EmoteHandler{
	private static final UUID EMOTE_NO_MOVE = UUID.fromString("09c2716e-0da9-430b-aaaf-8653c643dc09");

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START){
			return;
		}

		Player player = event.player;

		if(player != null){
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				for(Emote emote : cap.getEmotes().currentEmotes){
					int index = cap.getEmotes().currentEmotes.indexOf(emote);

					while(cap.getEmotes().emoteTicks.size() <= index){
						cap.getEmotes().emoteTicks.add(0, 0);
					}

					if(cap.getEmotes().emoteTicks.size() > index){
						cap.getEmotes().emoteTicks.set(index, cap.getEmotes().emoteTicks.get(index) + 1);
					}

					//Cancel emote if its duration is expired, this should happen even if it isnt local
					if(emote.duration != -1 && cap.getEmotes().emoteTicks.get(index) > emote.duration){
						cap.getEmotes().currentEmotes.remove(index);
						cap.getEmotes().emoteTicks.remove(index);
						NetworkHandler.CHANNEL.sendToServer(new SyncEmote(player.getId(), cap.getEmotes()));
						break;
					}

					if(Minecraft.getInstance().player != null && player.getId() == Minecraft.getInstance().player.getId()){
						if(player.isCrouching() || player.swinging){
							EmoteMenuHandler.clearEmotes();
							return;
						}

						if(emote.thirdPerson){
							Minecraft.getInstance().levelRenderer.needsUpdate();
							CameraType pointofview = Minecraft.getInstance().options.getCameraType();

							if(pointofview.isFirstPerson()){
								Minecraft.getInstance().options.setCameraType(THIRD_PERSON_BACK);

								if(pointofview.isFirstPerson() != Minecraft.getInstance().options.getCameraType().isFirstPerson()){
									Minecraft.getInstance().gameRenderer.checkEntityPostEffect(Minecraft.getInstance().options.getCameraType().isFirstPerson() ? Minecraft.getInstance().getCameraEntity() : null);
								}
							}
						}
					}

					if(!cap.getEmotes().currentEmotes.isEmpty()){
						if(emote.sound != null && emote.sound.interval > 0){
							if(cap.getEmotes().emoteTicks.get(index) % emote.sound.interval == 0){
								player.level.playLocalSound(player.position().x, player.position().y, player.position().z, new SoundEvent(new ResourceLocation(emote.sound.key)), SoundSource.PLAYERS, emote.sound.volume, emote.sound.pitch, false);
							}
						}

						if(ConfigHandler.SERVER.canMoveInEmote.get()){
							if(emote.animation != null && !emote.animation.isEmpty()){
								AttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
								AttributeModifier noMove = new AttributeModifier(EMOTE_NO_MOVE, "EMOTE", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);

								if(!attributeInstance.hasModifier(noMove)){
									attributeInstance.addTransientModifier(noMove);
								}
							}
						}
					}
				}

				if(cap.getEmotes().currentEmotes.isEmpty() && ConfigHandler.SERVER.canMoveInEmote.get()){
					AttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
					AttributeModifier noMove = new AttributeModifier(EMOTE_NO_MOVE, "EMOTE", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);

					if(attributeInstance.hasModifier(noMove)){
						attributeInstance.removeModifier(EMOTE_NO_MOVE);
					}
				}
			});
		}
	}

	@SubscribeEvent
	public static void playerAttacked(LivingHurtEvent event){
		EmoteMenuHandler.clearEmotes();
	}

	@SubscribeEvent
	public static void playerTick(ClientTickEvent event){
		if(event.phase == Phase.START){
			return;
		}

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent((cap) -> {
			if(!cap.getEmotes().currentEmotes.isEmpty()){
				for(Emote emote : cap.getEmotes().currentEmotes){
					if(!emote.loops){
						int index = cap.getEmotes().currentEmotes.indexOf(emote);

						if(cap.getEmotes().emoteTicks.get(index) >= emote.duration){
							cap.getEmotes().currentEmotes.remove(index);
							cap.getEmotes().emoteTicks.remove(index);
							NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), cap.getEmotes()));
						}
					}
				}
			}
		});
	}
}