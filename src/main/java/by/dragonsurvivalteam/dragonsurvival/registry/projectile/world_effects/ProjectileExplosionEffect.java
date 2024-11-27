package by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ProjectileExplosionEffect(Holder<DamageType> damageType, LevelBasedValue explosionPower, boolean fire, boolean breakBlocks, boolean canDamageSelf) implements ProjectileWorldEffect {
    public static final MapCodec<ProjectileExplosionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(ProjectileExplosionEffect::damageType),
            LevelBasedValue.CODEC.fieldOf("explosion_power").forGetter(ProjectileExplosionEffect::explosionPower),
            Codec.BOOL.fieldOf("fire").forGetter(ProjectileExplosionEffect::fire),
            Codec.BOOL.fieldOf("break_blocks").forGetter(ProjectileExplosionEffect::breakBlocks),
            Codec.BOOL.fieldOf("can_damage_self").forGetter(ProjectileExplosionEffect::canDamageSelf)
    ).apply(instance, ProjectileExplosionEffect::new));

    public void apply(final ServerLevel level, final ServerPlayer player, final ProjectileInstance projectile, final Vec3 position) {
        level.explode(player,
                canDamageSelf ? new DamageSource(damageType, player) : new DamageSource(damageType),
                null,
                position.x(), position.y(), position.z(),
                explosionPower.calculate(projectile.getLevel()),
                fire,
                breakBlocks ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }

    public MapCodec<? extends ProjectileWorldEffect> worldCodec() {
        return CODEC;
    }
}
