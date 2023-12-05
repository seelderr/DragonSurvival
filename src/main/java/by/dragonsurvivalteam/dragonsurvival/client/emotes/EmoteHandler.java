package by.dragonsurvivalteam.dragonsurvival.client.emotes;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
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

import java.util.Arrays;
import java.util.Objects;
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
				for(int index = 0; index < cap.getEmoteData().currentEmotes.length; index++){
					Emote emote = cap.getEmoteData().currentEmotes[index];

					if(emote == null){
						cap.getEmoteData().emoteTicks[index] = 0;
						continue;
					}

					if(cap.getEmoteData().emoteTicks.length < index || cap.getEmoteData().emoteTicks[index] == null){
						cap.getEmoteData().emoteTicks[index] = 0;
					}

					cap.getEmoteData().emoteTicks[index] += 1;

					//Cancel emote if its duration is expired, this should happen even if it isnt local
					if(emote.duration != -1 && cap.getEmoteData().emoteTicks[index] > emote.duration){
						cap.getEmoteData().currentEmotes[index] = null;
						cap.getEmoteData().emoteTicks[index] = 0;
						NetworkHandler.CHANNEL.sendToServer(new SyncEmote(player.getId(), cap.getEmoteData()));
						break;
					}

					if(Minecraft.getInstance().player != null && player.getId() == Minecraft.getInstance().player.getId()){
						if(player.isCrouching() || player.swinging){
							EmoteMenuHandler.clearEmotes(player);
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

					if(Arrays.stream(cap.getEmoteData().currentEmotes).anyMatch(Objects::nonNull)){
						if(emote.sound != null && emote.sound.interval > 0){
							if(cap.getEmoteData().emoteTicks[index] % emote.sound.interval == 0){
								player.level().playLocalSound(player.position().x, player.position().y, player.position().z, SoundEvent.createVariableRangeEvent(new ResourceLocation(emote.sound.key)), SoundSource.PLAYERS, emote.sound.volume, emote.sound.pitch, false);
							}
						}

						if(ServerConfig.canMoveInEmote){
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

				if(Arrays.stream(cap.getEmoteData().currentEmotes).noneMatch(Objects::nonNull) && ServerConfig.canMoveInEmote){
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
		EmoteMenuHandler.clearEmotes(event.getEntity());
	}

	@SubscribeEvent
	public static void playerTick(ClientTickEvent event){
		if(event.phase == Phase.START){
			return;
		}

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(Arrays.stream(cap.getEmoteData().currentEmotes).anyMatch(Objects::nonNull)){
				for(int index = 0; index < cap.getEmoteData().currentEmotes.length; index++){
					Emote emote = cap.getEmoteData().currentEmotes[index];
					if(emote != null && !emote.loops){
						if(cap.getEmoteData().emoteTicks[index] >= emote.duration){
							cap.getEmoteData().currentEmotes[index] = null;
							cap.getEmoteData().emoteTicks[index] = 0;
							NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), cap.getEmoteData()));
						}
					}
				}
			}
		});
	}
}