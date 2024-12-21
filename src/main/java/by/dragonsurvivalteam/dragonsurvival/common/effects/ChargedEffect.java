package by.dragonsurvivalteam.dragonsurvival.common.effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.EntityStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.MagicHandler;
import by.dragonsurvivalteam.dragonsurvival.common.particles.LargeLightningParticleOption;
import by.dragonsurvivalteam.dragonsurvival.common.particles.SmallFireParticleOption;
import by.dragonsurvivalteam.dragonsurvival.common.particles.SmallLightningParticleOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.particle.SyncParticleTrail;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSEntityTypeTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ChargedEffect extends ModifiableMobEffect {
    @ConfigRange(max = 100)
    @Translation(key = "charged_effect_max_chain", type = Translation.Type.CONFIGURATION, comments = "Determines the max. amount of times the charged effect can chain. Set to -1 for infinite chaining")
    @ConfigOption(side = ConfigSide.SERVER, category = {"effects"}, key = "charged_effect_max_chain")
    public static Integer maxChain = 5;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "charged_effect_max_chain_targets", type = Translation.Type.CONFIGURATION, comments = "Amount of entities the charged effect can chain to at once")
    @ConfigOption(side = ConfigSide.SERVER, category = {"effects"}, key = "charged_effect_max_chain_targets")
    public static Integer maxChainTargets = 2;

    @ConfigRange(min = 0.f, max = 100.f)
    @Translation(key = "charged_effect_spread_radius", type = Translation.Type.CONFIGURATION, comments = "Determines the radius of the charged effect spread")
    @ConfigOption(side = ConfigSide.SERVER, category = {"effects"}, key = "charged_effect_spread_radius")
    public static Float spreadRadius = 3.f;

    @ConfigRange(min = 0.f, max = 100.f)
    @Translation(key = "charged_effect_damage", type = Translation.Type.CONFIGURATION, comments = "Determines the damage dealt by the charged effect")
    @ConfigOption(side = ConfigSide.SERVER, category = {"effects"}, key = "charged_effect_damage")
    public static Float damage = 1.f;

    private int duration;

    public ChargedEffect(MobEffectCategory type, int color, boolean incurable) {
        super(type, color, incurable);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        this.duration = duration;
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(new DamageSource(DSDamageTypes.get(livingEntity.level(), DSDamageTypes.ELECTRIC)), damage);
        if(!DragonStateProvider.isDragon(livingEntity)) {
            ParticleOptions particle = new SmallLightningParticleOption(37F, false);
            for (int i = 0; i < 4; i++) {
                MagicHandler.renderEffectParticle(livingEntity, particle);
            }
        }
        if(duration % 10 == 0) {
            chargedEffectChain(livingEntity, damage);
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    public static void drawParticleLine(LivingEntity source, LivingEntity target) {
        if(source.level().isClientSide()) {
            return;
        }

        Vec3 start = source.position().add(0, source.getEyeHeight() / 2, 0);
        Vec3 end = target.position().add(0, target.getEyeHeight() / 2, 0);
        Vec3 trailMidpoint = end.subtract(start).scale(0.5).add(start);
        PacketDistributor.sendToPlayersNear(
                (ServerLevel) source.level(),
                null,
                trailMidpoint.x,
                trailMidpoint.y,
                trailMidpoint.z,
                64,
                new SyncParticleTrail(start.toVector3f(), end.toVector3f(), new LargeLightningParticleOption(37F, false)));
    }

    public static void chargedEffectChain(LivingEntity source, float damage) {
        List<LivingEntity> secondaryTargets = source.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), source, source.getBoundingBox().inflate(spreadRadius));
        secondaryTargets.sort((c1, c2) -> Boolean.compare(c1.hasEffect(DSEffects.CHARGED), c2.hasEffect(DSEffects.CHARGED))); // Prioritize non-charged entities
        if (secondaryTargets.size() > maxChainTargets) {
            secondaryTargets = secondaryTargets.subList(0, maxChainTargets);
        }

        for (LivingEntity target : secondaryTargets) {
            EntityStateHandler data = target.getData(DSDataAttachments.ENTITY_HANDLER);
            Player player = target.level().getEntity(data.lastAfflicted) instanceof Player ? (Player) target.level().getEntity(data.lastAfflicted) : null;
            target.hurt(new DamageSource(DSDamageTypes.get(target.level(), DSDamageTypes.ELECTRIC), player), damage);
            drawParticleLine(source, target);

            if (!target.getType().is(DSEntityTypeTags.CHARGED_SPREAD_BLACKLIST)) {
                if (target != source) {
                    EntityStateHandler sourceData = source.getData(DSDataAttachments.ENTITY_HANDLER);
                    EntityStateHandler targetData = target.getData(DSDataAttachments.ENTITY_HANDLER);

                    targetData.chainCount = sourceData.chainCount + 1;

                    if (!target.level().isClientSide()) {
                        if (target.getRandom().nextInt(100) < 40) {
                            if (targetData.chainCount < maxChain || maxChain == -1) {
                                targetData.lastAfflicted = player != null ? player.getId() : -1;
                                target.addEffect(new MobEffectInstance(DSEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
                            }
                        }
                    }
                }
            }
        }
    }
}
