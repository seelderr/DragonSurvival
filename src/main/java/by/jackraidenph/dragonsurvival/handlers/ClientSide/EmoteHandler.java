package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.emotes.Emote;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;


@Mod.EventBusSubscriber( Dist.CLIENT)
public class EmoteHandler
{
	private static final UUID EMOTE_NO_MOVE = UUID.fromString("09c2716e-0da9-430b-aaaf-8653c643dc09");
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		if (player != null) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (cap.getCurrentEmote() != null) {
					Emote emote = cap.getCurrentEmote();
					cap.emoteTick++;
					
					if (player.isCrouching()) {
						EmoteMenuHandler.setEmote(null);
						
					} else {
						if (emote != null && emote.sound != null) {
							if (emote.sound.interval > 0) {
								if (cap.emoteTick % emote.sound.interval == 0) {
									player.level.playLocalSound(player.position().x, player.position().y, player.position().z, new SoundEvent(new ResourceLocation(emote.sound.key)), SoundCategory.PLAYERS, emote.sound.volume, emote.sound.pitch, false);
								}
							}
						}
						ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
						AttributeModifier noMove = new AttributeModifier(EMOTE_NO_MOVE, "EMOTE", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);
						
						if (!attributeInstance.hasModifier(noMove)) {
							attributeInstance.addTransientModifier(noMove);
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
	public static void playerTick(ClientTickEvent event){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent((cap) -> {
			if(cap.getCurrentEmote() != null && !cap.getCurrentEmote().loops){
				if(cap.emoteTick >= cap.getCurrentEmote().duration){
					EmoteMenuHandler.setEmote(null);
				}
			}
		});
	}
}
