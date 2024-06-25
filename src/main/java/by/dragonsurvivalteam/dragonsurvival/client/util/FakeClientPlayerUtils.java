package by.dragonsurvivalteam.dragonsurvival.client.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class FakeClientPlayerUtils {
	private static final ConcurrentHashMap<Integer, FakeClientPlayer> FAKE_PLAYERS = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<Integer, DragonEntity> FAKE_DRAGONS = new ConcurrentHashMap<>();

	public static DragonEntity getFakeDragon(int index, final DragonStateHandler handler) {
		FakeClientPlayer clientPlayer = getFakePlayer(index, handler);

		FAKE_DRAGONS.computeIfAbsent(index, key -> new DragonEntity(DSEntities.DRAGON.get(), clientPlayer.level()) {
			@Override
			public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
				fakeAnimationController = new AnimationController<>(this, "fake_controller", 2, state -> {
					if (handler.refreshBody) {
						fakeAnimationController.forceAnimationReset();
						handler.refreshBody = false;
						return PlayState.STOP;
					}

					if (clientPlayer.animationSupplier != null) {
						return state.setAndContinue(RawAnimation.begin().thenLoop(clientPlayer.animationSupplier.get()));
					}

					return PlayState.STOP;
				});
				AnimationController<DragonEntity> ac = fakeAnimationController;
				if (getPlayer() instanceof FakeClientPlayer fcp) {
					fcp.animationController = ac;
				}
				controllers.add(ac);
			}

			@Override
			public Player getPlayer() {
				return clientPlayer;
			}
		});

		return FAKE_DRAGONS.get(index);
	}

	public static FakeClientPlayer getFakePlayer(int num, DragonStateHandler handler){
		FAKE_PLAYERS.computeIfAbsent(num, FakeClientPlayer::new);
		FAKE_PLAYERS.get(num).handler = handler;
		FAKE_PLAYERS.get(num).lastAccessed = System.currentTimeMillis();
		return FAKE_PLAYERS.get(num);
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Pre event){
		FAKE_PLAYERS.forEach((i, v) -> {
			if(System.currentTimeMillis() - v.lastAccessed >= TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)){
				v.remove(RemovalReason.DISCARDED);
				FAKE_DRAGONS.get(i).remove(RemovalReason.DISCARDED);

				FAKE_DRAGONS.remove(i);
				FAKE_PLAYERS.remove(i);
			}
		});
	}
}