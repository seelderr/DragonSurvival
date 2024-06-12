package by.dragonsurvivalteam.dragonsurvival.common.capability;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.ENTITY_HANDLER;
import static by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities.ENTITY_CAPABILITY;

import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

// TODO: We duplicate data between here and the DragonStateHandler. We'll probably want to test and refactor that once we are compiling.
public class EntityStateProvider implements ICapabilityProvider<Entity, Void, EntityStateHandler> {
    public static Optional<? extends EntityStateHandler> getEntityCap(Entity entity){
        if (entity instanceof Player) {
            return DragonStateProvider.getCap(entity);
        }

        EntityStateHandler handler = entity.getCapability(ENTITY_CAPABILITY);

        return Optional.ofNullable(handler);
    }

    public static EntityStateHandler getEntityHandler(Entity entity){
        if (entity == null) {
            return new EntityStateHandler();
        }

        Optional<EntityStateHandler> cap = (Optional<EntityStateHandler>) getEntityCap(entity);

        return cap.orElse(new EntityStateHandler());
    }

    @Override
    public @Nullable EntityStateHandler getCapability(Entity entity, Void context) {
        if(entity instanceof Player){
            return null;
        }

        return entity.getData(ENTITY_HANDLER);
    }
}
