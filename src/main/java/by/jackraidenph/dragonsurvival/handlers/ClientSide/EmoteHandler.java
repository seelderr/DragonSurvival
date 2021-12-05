package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static net.minecraft.client.settings.PointOfView.THIRD_PERSON_BACK;


@Mod.EventBusSubscriber( Dist.CLIENT)
public class EmoteHandler
{
	private static final UUID EMOTE_NO_MOVE = UUID.fromString("09c2716e-0da9-430b-aaaf-8653c643dc09");
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		if (player != null) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (cap.getEmotes().getCurrentEmote() != null) {
					Emote emote = cap.getEmotes().getCurrentEmote();
					cap.getEmotes().emoteTick++;
					
					if( cap.getEmotes().emoteTick % Functions.secondsToTicks(5) == 0){
						NetworkHandler.CHANNEL.sendToServer(new SyncEmoteServer(emote.name, cap.getEmotes().emoteTick));
					}
					
					if (player.isCrouching() || player.swinging) {
						EmoteMenuHandler.setEmote(null);
						
					} else {
						if (emote.sound != null && emote.sound.interval > 0) {
							if (cap.getEmotes().emoteTick % emote.sound.interval == 0) {
								player.level.playLocalSound(player.position().x, player.position().y, player.position().z, new SoundEvent(new ResourceLocation(emote.sound.key)), SoundCategory.PLAYERS, emote.sound.volume, emote.sound.pitch, false);
							}
						}
						
						if(emote.animation != null && !emote.animation.isEmpty()) {
							ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
							AttributeModifier noMove = new AttributeModifier(EMOTE_NO_MOVE, "EMOTE", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);
							
							if (!attributeInstance.hasModifier(noMove)) {
								attributeInstance.addTransientModifier(noMove);
							}
						}
						
						if(emote.thirdPerson){
							Minecraft.getInstance().levelRenderer.needsUpdate();
							PointOfView pointofview = Minecraft.getInstance().options.getCameraType();
							
							if(pointofview.isFirstPerson()) {
								Minecraft.getInstance().options.setCameraType(THIRD_PERSON_BACK);
								
								if (pointofview.isFirstPerson() != Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
									Minecraft.getInstance().gameRenderer.checkEntityPostEffect(Minecraft.getInstance().options.getCameraType().isFirstPerson() ? Minecraft.getInstance().getCameraEntity() : null);
								}
							}
						}
					}
				}else{
					ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
					AttributeModifier noMove = new AttributeModifier(EMOTE_NO_MOVE, "EMOTE", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);
					
					if (attributeInstance.hasModifier(noMove)) {
						attributeInstance.removeModifier(EMOTE_NO_MOVE);
					}
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerAttacked(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();
		
		if(entity instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)entity;
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (cap.getEmotes().getCurrentEmote() != null) {
					EmoteMenuHandler.setEmote(null);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerTick(ClientTickEvent event){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent((cap) -> {
			if(cap.getEmotes().getCurrentEmote() != null && !cap.getEmotes().getCurrentEmote().loops){
				if(cap.getEmotes().emoteTick >= cap.getEmotes().getCurrentEmote().duration){
					EmoteMenuHandler.setEmote(null);
				}
			}
		});
	}
}
