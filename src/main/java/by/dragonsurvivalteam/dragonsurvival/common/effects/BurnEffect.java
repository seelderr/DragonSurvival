package by.dragonsurvivalteam.dragonsurvival.common.effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.EntityStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.EffectHandler;
import by.dragonsurvivalteam.dragonsurvival.common.particles.SmallFireParticleOption;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

public class BurnEffect extends ModifiableMobEffect {
    public BurnEffect(final MobEffectCategory type, int color, boolean incurable) {
        super(type, color, incurable);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(final LivingEntity entity, int amplifier) {
        if (entity.fireImmune() || entity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) || entity.isInWaterRainOrBubble()) {
            return false;
        }

        EntityStateHandler data = entity.getData(DSDataAttachments.ENTITY_HANDLER);

        if (!DragonStateProvider.isDragon(entity)) {
            ParticleOptions particle = new SmallFireParticleOption(37F, false);

            for (int i = 0; i < 4; i++) {
                EffectHandler.renderEffectParticle(entity, particle);
            }
        }

        if (data.lastPos != null) {
            double distance = entity.distanceToSqr(data.lastPos);
            float damage = (amplifier + 1) * Mth.clamp((float) distance, 0, 10);

            if (damage > 0) {
                if (!entity.isOnFire()) {
                    // Short enough fire duration to not cause fire damage but still drop cooked items
                    entity.setRemainingFireTicks(1);
                }

                // TODO :: specifically store the entity which applied this instance of the effect (same for charged / drain)
                //  probably needs additional data stored in the effect instance?
                Entity player = entity.level().getEntity(data.lastAfflicted) instanceof Player ? entity : null;
                entity.hurt(new DamageSource(DSDamageTypes.get(entity.level(), DSDamageTypes.CAVE_DRAGON_BURN), player), damage);
            }
        }

        return super.applyEffectTick(entity, amplifier);
    }
}
