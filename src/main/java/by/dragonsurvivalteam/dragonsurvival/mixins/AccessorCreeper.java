package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class)
public interface AccessorCreeper {
    @Accessor("DATA_IS_POWERED")
    static net.minecraft.network.syncher.EntityDataAccessor<Boolean> getDataIsPowered() {
        throw new AssertionError();
    }
}
