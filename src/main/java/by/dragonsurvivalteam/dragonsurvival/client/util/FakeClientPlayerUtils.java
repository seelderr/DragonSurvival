package by.dragonsurvivalteam.dragonsurvival.client.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class FakeClientPlayerUtils {
	private static final ConcurrentHashMap<Integer, FakeClientPlayer> FAKE_PLAYERS = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Integer, DragonEntity> FAKE_DRAGONS = new ConcurrentHashMap<>();

	public static DragonEntity getFakeDragon(int index, final DragonStateHandler handler) {
		FakeClientPlayer fakePlayer = getFakePlayer(index, handler);

		return FAKE_DRAGONS.computeIfAbsent(index, key -> new DragonEntity(DSEntities.DRAGON, fakePlayer.getLevel()) {
			@Override
			public void registerControllers(final AnimationData animationData) {
                animationData.shouldPlayWhilePaused = true;
				animationData.addAnimationController(new AnimationController<>(this, "fake_player_controller", 2, event -> {
                    AnimationBuilder builder = new AnimationBuilder();

                    if (fakePlayer.animationSupplier != null) {
                        builder.addAnimation(fakePlayer.animationSupplier.get(), EDefaultLoopTypes.LOOP);
                    }

                    event.getController().setAnimation(builder);
                    return PlayState.CONTINUE;
				}));
			}

			@Override
			public Player getPlayer() {
				return fakePlayer;
			}
		});
	}

	public static FakeClientPlayer getFakePlayer(int index, DragonStateHandler handler) {
		FAKE_PLAYERS.computeIfAbsent(index, FakeClientPlayer::new);
		FAKE_PLAYERS.get(index).handler = handler;
		FAKE_PLAYERS.get(index).lastAccessed = System.currentTimeMillis();
		return FAKE_PLAYERS.get(index);
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent event) {
		FAKE_PLAYERS.forEach((index, player) -> {
			if (System.currentTimeMillis() - player.lastAccessed >= TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)) {
				player.remove(RemovalReason.DISCARDED);
				DragonEntity dragon = FAKE_DRAGONS.get(index);

				if (dragon != null) {
					dragon.remove(RemovalReason.DISCARDED);
					FAKE_DRAGONS.remove(index);
				}

				FAKE_PLAYERS.remove(index);
			}
		});
	}
}