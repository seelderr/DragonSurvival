package by.dragonsurvivalteam.dragonsurvival.client.emotes;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers.EMOTE_NO_MOVE;
import static net.minecraft.client.CameraType.THIRD_PERSON_BACK;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.emotes.SyncEmote;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public class EmoteHandler {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void playerTick(final PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        DragonStateProvider.getCap(player).ifPresent(cap -> {
            for (int index = 0; index < cap.getEmoteData().currentEmotes.length; index++) {
                Emote emote = cap.getEmoteData().currentEmotes[index];

                if (emote == null) {
                    cap.getEmoteData().emoteTicks[index] = 0;
                    continue;
                }

                if (cap.getEmoteData().emoteTicks.length < index || cap.getEmoteData().emoteTicks[index] == null) {
                    cap.getEmoteData().emoteTicks[index] = 0;
                }

                cap.getEmoteData().emoteTicks[index] += 1;

                //Cancel emote if its duration is expired, this should happen even if it isnt local
                if (emote.duration != -1 && cap.getEmoteData().emoteTicks[index] > emote.duration) {
                    cap.getEmoteData().currentEmotes[index] = null;
                    cap.getEmoteData().emoteTicks[index] = 0;
                    PacketDistributor.sendToServer(new SyncEmote.Data(player.getId(), cap.getEmoteData().serializeNBT(player.registryAccess())));
                    break;
                }

                if (Minecraft.getInstance().player != null && player.getId() == Minecraft.getInstance().player.getId()) {
                    if (player.isCrouching() || player.swinging) {
                        EmoteMenuHandler.clearEmotes(player);
                        return;
                    }

                    if (emote.thirdPerson) {
                        Minecraft.getInstance().levelRenderer.needsUpdate();
                        CameraType pointofview = Minecraft.getInstance().options.getCameraType();

                        if (pointofview.isFirstPerson()) {
                            Minecraft.getInstance().options.setCameraType(THIRD_PERSON_BACK);

                            if (pointofview.isFirstPerson() != Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                                Minecraft.getInstance().gameRenderer.checkEntityPostEffect(Minecraft.getInstance().options.getCameraType().isFirstPerson() ? Minecraft.getInstance().getCameraEntity() : null);
                            }
                        }
                    }
                }

                if (Arrays.stream(cap.getEmoteData().currentEmotes).anyMatch(Objects::nonNull)) {
                    if (emote.sound != null && emote.sound.interval > 0) {
                        if (cap.getEmoteData().emoteTicks[index] % emote.sound.interval == 0) {
                            player.level().playLocalSound(player.position().x, player.position().y, player.position().z, SoundEvent.createVariableRangeEvent(ResourceLocation.parse(emote.sound.key)), SoundSource.PLAYERS, emote.sound.volume, emote.sound.pitch, false);
                        }
                    }

                    if (ServerConfig.canMoveInEmote) {
                        if (emote.animation != null && !emote.animation.isEmpty()) {
                            AttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                            AttributeModifier noMove = new AttributeModifier(EMOTE_NO_MOVE, -attributeInstance.getValue(), AttributeModifier.Operation.ADD_VALUE);

                            if (!attributeInstance.hasModifier(EMOTE_NO_MOVE)) {
                                attributeInstance.addTransientModifier(noMove);
                            }
                        }
                    }
                }
            }

            if (Arrays.stream(cap.getEmoteData().currentEmotes).noneMatch(Objects::nonNull) && ServerConfig.canMoveInEmote) {
                AttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeInstance.hasModifier(EMOTE_NO_MOVE)) {
                    attributeInstance.removeModifier(EMOTE_NO_MOVE);
                }
            }

            if (Arrays.stream(cap.getEmoteData().currentEmotes).anyMatch(Objects::nonNull)) {
                for (int index = 0; index < cap.getEmoteData().currentEmotes.length; index++) {
                    Emote emote = cap.getEmoteData().currentEmotes[index];
                    if (emote != null && !emote.loops) {
                        if (cap.getEmoteData().emoteTicks[index] >= emote.duration) {
                            cap.getEmoteData().currentEmotes[index] = null;
                            cap.getEmoteData().emoteTicks[index] = 0;
                            PacketDistributor.sendToServer(new SyncEmote.Data(Minecraft.getInstance().player.getId(), cap.getEmoteData().serializeNBT(Minecraft.getInstance().player.registryAccess())));
                        }
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void playerAttacked(final LivingIncomingDamageEvent event) {
        EmoteMenuHandler.clearEmotes(event.getEntity());
    }

    // TODO Is this fine to move into the above playerTickEvent? This was a LevelTick before but it was causing problems on startup
    /*@SubscribeEvent
    public static void playerTick(final PlayerTickEvent.Post event) {
        DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
            if (Arrays.stream(cap.getEmoteData().currentEmotes).anyMatch(Objects::nonNull)) {
                for (int index = 0; index < cap.getEmoteData().currentEmotes.length; index++) {
                    Emote emote = cap.getEmoteData().currentEmotes[index];
                    if (emote != null && !emote.loops) {
                        if (cap.getEmoteData().emoteTicks[index] >= emote.duration) {
                            cap.getEmoteData().currentEmotes[index] = null;
                            cap.getEmoteData().emoteTicks[index] = 0;
                            PacketDistributor.sendToServer(new SyncEmote.Data(Minecraft.getInstance().player.getId(), cap.getEmoteData().serializeNBT(Minecraft.getInstance().player.registryAccess())));
                        }
                    }
                }
            }
        });
    }*/
}