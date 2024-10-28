package by.dragonsurvivalteam.dragonsurvival.common.capability;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;
import static by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities.DRAGON_CAPABILITY;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonStateProvider implements ICapabilityProvider<Player, Void, DragonStateHandler> {
    @Override
    public @Nullable DragonStateHandler getCapability(@NotNull Player player, @Nullable Void context) {
        return getData(player);
    }

    public static @NotNull DragonStateHandler getData(@NotNull final Player player) {
        DragonStateHandler fakeData = getFakePlayerHandler(player);

        if (fakeData != null) {
            return fakeData;
        }

        return player.getData(DRAGON_HANDLER);
    }

    public static Optional<DragonStateHandler> getOptional(@Nullable final Entity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(entity.getCapability(DRAGON_CAPABILITY));
    }

    public static boolean isDragon(@Nullable Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }

        return getOptional(entity).filter(DragonStateHandler::isDragon).isPresent();
    }

    private static DragonStateHandler getFakePlayerHandler(@NotNull Entity entity) {
        if (!entity.level().isClientSide()) {
            return null;
        }

        if (entity instanceof FakeClientPlayer fakeClientPlayer) {
            if (fakeClientPlayer.handler != null) {
                return fakeClientPlayer.handler;
            }
        }

        return null;
    }
}