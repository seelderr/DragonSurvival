package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.particle.SyncBreathParticles;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.ACTIVE_SIMPLE, AbilityInfo.Type.ACTIVE_CHANNELED})
public record BreathParticlesEffect(float spread, float speedPerSize, ParticleOptions mainParticle, ParticleOptions secondaryParticle) implements AbilityEntityEffect {
    public static final MapCodec<BreathParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("spread").forGetter(BreathParticlesEffect::spread),
            Codec.FLOAT.fieldOf("speed_per_size").forGetter(BreathParticlesEffect::speedPerSize),
            ParticleTypes.CODEC.fieldOf("main_particle").forGetter(BreathParticlesEffect::mainParticle),
            ParticleTypes.CODEC.fieldOf("secondary_particle").forGetter(BreathParticlesEffect::secondaryParticle)
    ).apply(instance, BreathParticlesEffect::new));

    // TODO: Some hardcoded constants + scaling with size from the old breath ability. Not sure how to describe these constants for the codec yet.
    @Override
    public void apply(ServerPlayer dragon, DragonAbilityInstance ability, Entity entity) {
        if(dragon != entity) {
            throw new IllegalArgumentException("Target entity must be the dragon that cast the ability for BreathParticlesEffect!");
        }

        DragonStateHandler handler = DragonStateProvider.getData(dragon);
        float yaw = (float) Math.toRadians(-dragon.getYRot());
        float pitch = (float) Math.toRadians(-dragon.getXRot());
        float speed = (float) (handler.getSize() * speedPerSize);

        float xVel = (float) (Math.sin(yaw) * Math.cos(pitch) * speed);
        float yVel = (float) Math.sin(pitch) * speed;
        float zVel = (float) (Math.cos(yaw) * Math.cos(pitch) * speed);
        Vec3 velocity = new Vec3(xVel, yVel, zVel);

        Vec3 eyePos = dragon.getEyePosition();
        Vec3 lookAngle = dragon.getLookAngle();
        Vec3 position;
        if (dragon.getAbilities().flying) {
            Vec3 forward = lookAngle.scale(2.0F);
            position = eyePos.add(forward).add(0F, -0.1 - 0.5F * (handler.getSize() / 30F), 0F);
        } else {
            Vec3 forward = lookAngle.scale(1.0F);
            position = eyePos.add(forward).add(0F, -0.1F - 0.2F * (handler.getSize() / 30F), 0F);
        }

        // Copied from BreathAbility.calculateNumberOfParticles (Wanted to avoid using the ability class as we will eventually delete it)
        int numParticles = (int) Math.max(Math.min(100, handler.getSize() * 0.6), 12);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(dragon, new SyncBreathParticles(numParticles, spread, position.toVector3f(), velocity.toVector3f(), mainParticle, secondaryParticle));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
