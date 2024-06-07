package by.dragonsurvivalteam.dragonsurvival.common.capability;

import static by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities.DRAGON_CAPABILITY;
import static by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler.DRAGON_HANDLER;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonStateProvider implements ICapabilityProvider<Entity, Void, DragonStateHandler> {
	private static DragonStateHandler getFakePlayer(Entity entity) {
		if (entity instanceof FakeClientPlayer fakeClientPlayer) {
			if (fakeClientPlayer.handler != null) {
				return fakeClientPlayer.handler;
			}
		}

		return null;
	}

	public static Optional<DragonStateHandler> getCap(final Entity entity) {
		return Optional.ofNullable(entity.getCapability(DRAGON_CAPABILITY));
	}

	public static DragonStateHandler getOrGenerateHandler(final Entity entity) {
		if (entity == null) {
			return new DragonStateHandler();
		}

		return entity.getData(DRAGON_HANDLER);
	}

	public static boolean isDragon(Entity entity){
		if (!(entity instanceof Player)) {
			return false;
		}

		return getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	@Override
	public @Nullable DragonStateHandler getCapability(@NotNull Entity entity, @NotNull Void context) {
		if (entity.level().isClientSide()) {
			DragonStateHandler fakeState = getFakePlayer(entity);

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