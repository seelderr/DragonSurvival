package by.dragonsurvivalteam.dragonsurvival.mixins.playeranimator;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.math.Vector3f;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.IS_BETTERCOMBAT_LOADED;

@Mixin(AnimationApplier.class)
public abstract class MixinAnimationApplier extends AnimationProcessor {
    public MixinAnimationApplier(IAnimation animation) {
        super(animation);
    }

    @Inject(method = "updatePart", at = @At("TAIL"), remap = false)
    public void offsetAttackAnimation(final String partName, final ModelPart part, final CallbackInfo callback) {
        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;

        if (player == null) {
            return;
        }

        if (partName.equals("rightArm") || partName.equals("leftArm")) {
            // TODO :: Find a way to cache this? (maybe in a general sense for all calls)
            DragonStateHandler handler = DragonUtils.getHandler(player);

            if (IS_BETTERCOMBAT_LOADED && ClientConfig.betterCombatCompatibility && instance.options.getCameraType().isFirstPerson() && handler.isDragon()) {
                double size = handler.getSize();
                float yOffset = (float) (20F - size + (size * 0.4));

                // Negative `y` value => animation is higher
                part.offsetPos(new Vector3f(0, yOffset, 0));
            }
        }
    }
}
