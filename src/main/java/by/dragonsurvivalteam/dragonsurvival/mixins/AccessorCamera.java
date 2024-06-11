package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( Camera.class )
public interface AccessorCamera {

    @Invoker("move")
    void move(double pDistanceOffset, double pVerticalOffset, double pHorizontalOffset);
}
