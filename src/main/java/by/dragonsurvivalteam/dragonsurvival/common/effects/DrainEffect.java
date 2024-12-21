package by.dragonsurvivalteam.dragonsurvival.common.effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.EntityStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.EffectHandler;
import by.dragonsurvivalteam.dragonsurvival.common.particles.SmallPoisonParticleOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DrainEffect extends ModifiableMobEffect {
    @ConfigRange(min = 0, max = 100)
    @Translation(key = "drain_effect_damage", type = Translation.Type.CONFIGURATION, comments = "Determines the damage dealt by the drain effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"effects", "drain"}, key = "drain_effect_damage")
    public static Float damage = 1f;

    public DrainEffect(final MobEffectCategory type, int color, boolean incurable) {
        super(type, color, incurable);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull final LivingEntity entity, int amplifier) {
        if (!DragonStateProvider.isDragon(entity)) {
            ParticleOptions particle = new SmallPoisonParticleOption(37F, false);

            for (int i = 0; i < 4; i++) {
                EffectHandler.renderEffectParticle(entity, particle);
            }
        }

        EntityStateHandler data = entity.getData(DSDataAttachments.ENTITY_HANDLER);
        Player player = entity.level().getEntity(data.lastAfflicted) instanceof Player ? (Player) entity.level().getEntity(data.lastAfflicted) : null;
        entity.hurt(new DamageSource(DSDamageTypes.get(entity.level(), DSDamageTypes.FOREST_DRAGON_DRAIN), player), damage);

        return super.applyEffectTick(entity, amplifier);
    }
}
