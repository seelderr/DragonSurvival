package by.dragonsurvivalteam.dragonsurvival.common.capability;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;
import static by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities.DRAGON_CAPABILITY;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonStateProvider implements ICapabilityProvider<Entity, Void, DragonStateHandler> {
	private static DragonStateHandler getFakePlayerHandler(Entity entity) {
		if(FMLEnvironment.dist  == Dist.CLIENT){
			if (entity instanceof FakeClientPlayer fakeClientPlayer) {
				if (fakeClientPlayer.handler != null) {
					return fakeClientPlayer.handler;
				}
			}
		}

		return null;
	}

	public static Optional<DragonStateHandler> getCap(@Nullable final Entity entity) {
		if (entity == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(entity.getCapability(DRAGON_CAPABILITY));
	}

	public static @NotNull DragonStateHandler getOrGenerateHandler(final Entity entity) {
		if (entity == null) {
			return new DragonStateHandler();
		}

		DragonStateHandler fakePlayerHandler = getFakePlayerHandler(entity);
		if (fakePlayerHandler != null) {
			return fakePlayerHandler;
		}

		return entity.getData(DRAGON_HANDLER);
	}

	public static boolean isDragon(@Nullable Entity entity){
		if (!(entity instanceof Player)) {
			return false;
		}

		return getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	@Override
	public @Nullable DragonStateHandler getCapability(@NotNull Entity entity, @NotNull Void context) {
		if (entity.level().isClientSide()) {
			DragonStateHandler fakeState = getFakePlayerHandler(entity);

			if (fakeState != null) {
				return fakeState;
			}
		}

		if (!(entity instanceof Player)) {
			return null;
		}

		Optional<DragonStateHandler> handler = entity.getExistingData(DRAGON_HANDLER);

		return handler.orElse(null);
	}
}