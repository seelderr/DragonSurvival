package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncChatEvent;
import by.dragonsurvivalteam.dragonsurvival.network.status.RefreshDragon;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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

@EventBusSubscriber
public class WingObtainmentController {
    private static final Map<String, Integer> DRAGON_PHRASES = new HashMap<>();
    private static final UUID ENDER_DRAGON_UUID = UUID.fromString("426642b9-2e88-4350-afa8-f99f75af5479");
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    @OnlyIn(Dist.CLIENT)
    public static void loadDragonPhrases() {
        try {
            List<String> langs = new ArrayList<>();
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/dragonsurvival/lang");

            if (stream == null) {
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String file;

            while ((file = reader.readLine()) != null) {
                langs.add(file);
            }

            reader.close();
            Gson gson = GsonFactory.getDefault();
            Type type = new TypeToken<Map<String, String>>() {}.getType();

            for (String langFile : langs) {
                URL resource = Thread.currentThread().getContextClassLoader().getResource("assets/dragonsurvival/lang/" + langFile);
                Map<String, String> langData = gson.fromJson(new String(Files.readAllBytes(Paths.get(resource.toURI()))), type);
                int phraseCount = 0;

                for (String key : langData.keySet()) {
                    if (key.contains("ds.endmessage")) {
                        phraseCount++;
                    }
                }

                if (phraseCount > 0) {
                    DRAGON_PHRASES.put(langFile.replace(".json", ""), phraseCount);
                }
            }
        } catch (Exception exception) {
            DragonSurvival.LOGGER.error("An error occurred while trying to build the dragon phrases", exception);
        }
    }

    @SubscribeEvent
    public static void inTheEnd(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTo() == Level.END) {
            DragonStateProvider.getOptional(player).ifPresent(data -> {
                if (data.isDragon() && !data.getMovementData().spinLearned && ServerFlightHandler.enderDragonGrantsSpin) {
                    SERVICE.schedule(() -> PacketDistributor.sendToPlayer(player, new SyncChatEvent.Data(ENDER_DRAGON_UUID.toString(), "ds.endmessage")), 3, TimeUnit.SECONDS);
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT) // FIXME :: move to its own class or wait for this system to be reworked
    public static void clientMessageRecieved(SyncChatEvent.Data event) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        if (event.signerId().equals(ENDER_DRAGON_UUID.toString())) {
            Vec3 centerPoint = new Vec3(0D, 128D, 0D);
            List<EnderDragon> enderDragons = player.level().getEntitiesOfClass(EnderDragon.class, AABB.ofSize(centerPoint, 192, 192, 192));

            if (enderDragons.isEmpty()) {
                return;
            }

            String dragonName = "<" + enderDragons.get(0).getDisplayName().getString() + "> ";

            if (event.chatId().equals("ds.endmessage")) {
                String language = Minecraft.getInstance().getLanguageManager().getSelected();
                int messageId = player.getRandom().nextInt(DRAGON_PHRASES.getOrDefault(language, DRAGON_PHRASES.getOrDefault("en_us", 1))) + 1;
                player.sendSystemMessage(Component.literal(dragonName).append(Component.translatable("ds.endmessage." + messageId, player.getDisplayName().getString())));
            } else if (event.chatId().equals("ds.dragon.grants.wings")) {
                player.sendSystemMessage(Component.translatable("ds.dragon.grants.wings"));
            }
        }
    }

    @SubscribeEvent
    public static void serverChatEvent(ServerChatEvent chatEvent) {
        ServerPlayer player = chatEvent.getPlayer();

        if (!ServerFlightHandler.enderDragonGrantsSpin || player.level().dimension() != Level.END) {
            return;
        }

        DragonStateProvider.getOptional(player).ifPresent(data -> {
            if (data.isDragon() && !data.getMovementData().spinLearned && /* Check if there is an ender dragon present */ !player.serverLevel().getDragons().isEmpty()) {
                if (!chatEvent.getMessage().getString().isEmpty()) {
                    SERVICE.schedule(() -> player.sendSystemMessage(Component.translatable("ds.dragon.grants.wings")), 2, TimeUnit.SECONDS);

                    data.setHasFlight(true);
                    data.getMovementData().spinLearned = true;
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSpinStatus.Data(player.getId(), data.getMovementData().spinAttack, data.getMovementData().spinCooldown, data.getMovementData().spinLearned));
                    PacketDistributor.sendToAllPlayers(new SyncComplete.Data(player.getId(), DragonStateProvider.getData(player).serializeNBT(player.registryAccess())));
                }
            }
        });
    }

    @SubscribeEvent
    public static void teleportAway(LivingIncomingDamageEvent damageEvent) {
        if (!ServerConfig.endVoidTeleport) {
            return;
        }

        if (damageEvent.getEntity() instanceof Player player) {
            DamageSource damageSource = damageEvent.getSource();

            if (player.level().dimension() == Level.END && damageSource == player.damageSources().fellOutOfWorld() && player.position().y < -60) {
                DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
                    if (dragonStateHandler.isDragon()) {
                        DimensionTransition transition = new DimensionTransition(player.level().getServer().overworld(), player, DimensionTransition.DO_NOTHING);
                        player.changeDimension(transition);
                        PacketDistributor.sendToAllPlayers(new RefreshDragon.Data(player.getId()));
                        damageEvent.setCanceled(true);
                    }
                });
            }
        }
    }
}