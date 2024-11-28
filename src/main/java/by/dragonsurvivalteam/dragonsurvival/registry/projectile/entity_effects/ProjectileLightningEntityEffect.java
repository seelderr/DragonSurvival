package by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects.ProjectileLightningWorldEffect;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.LightningHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;

public record ProjectileLightningEntityEffect(boolean ignoresItemsAndExperience, boolean spawnsFire, boolean canHurtSelf) implements ProjectileEntityEffect {

    public static final MapCodec<ProjectileLightningEntityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.BOOL.fieldOf("ignores_items_and_experience").forGetter(ProjectileLightningEntityEffect::ignoresItemsAndExperience),
                    Codec.BOOL.fieldOf("spawns_fire").forGetter(ProjectileLightningEntityEffect::spawnsFire),
                    Codec.BOOL.fieldOf("can_hurt_self").forGetter(ProjectileLightningEntityEffect::canHurtSelf)
            ).apply(instance, ProjectileLightningEntityEffect::new)
    );

    @Override
    public void apply(Projectile projectile, Entity target, int projectileLevel) {
        LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(projectile.level());
        lightningboltentity.moveTo(target.position());
        if(projectile.getOwner() instanceof ServerPlayer serverPlayer) {
            lightningboltentity.setCause(serverPlayer);
        }
        LightningHandler lightningHandler = new LightningHandler();
        lightningHandler.ignoresItemsAndExperience = ignoresItemsAndExperience;
        lightningHandler.spawnsFire = spawnsFire;
        lightningHandler.canHurtSelf = canHurtSelf;
        lightningboltentity.setData(DSDataAttachments.LIGHTNING_BOLT_DATA, lightningHandler);
        projectile.level().addFreshEntity(lightningboltentity);
    }

    @Override
    public MapCodec<? extends ProjectileEntityEffect> entityCodec() {
        return CODEC;
    }
}

