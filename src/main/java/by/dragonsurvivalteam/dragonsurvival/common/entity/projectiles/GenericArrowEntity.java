package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.targeting.ProjectileTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GenericArrowEntity extends AbstractArrow {
    private final Component name;
    private final ProjectileInstance projectileInstance;
    private final Optional<EntityPredicate> canHitPredicate;
    private final List<ProjectileTargeting> tickingEffects;
    private final List<ProjectileTargeting> commonHitEffects;
    private final List<ProjectileEntityEffect> entityHitEffects;
    private final List<ProjectileBlockEffect> blockHitEffects;
    private final DragonAbilityInstance ability;

    public GenericArrowEntity(
            Component name,
            EntityType<? extends AbstractArrow> entityType,
            Optional<EntityPredicate> canHitPredicate,
            List<ProjectileTargeting> tickingEffects,
            List<ProjectileTargeting> commonHitEffects,
            List<ProjectileEntityEffect> entityHitEffects,
            List<ProjectileBlockEffect> blockHitEffects,
            DragonAbilityInstance ability,
            Level level,
            ProjectileInstance projectileInstance,
            int piercingLevel) {
        super(entityType, level);
        this.name = name;
        this.canHitPredicate = canHitPredicate;
        this.tickingEffects = tickingEffects;
        this.commonHitEffects = commonHitEffects;
        this.entityHitEffects = entityHitEffects;
        this.blockHitEffects = blockHitEffects;
        this.ability = ability;
        this.projectileInstance = projectileInstance;
        this.setPierceLevel((byte)piercingLevel);
    }

    @Override
    protected @NotNull Component getTypeName() {
        return name;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        boolean canHit = super.canHitEntity(target);
        if(canHitPredicate.isPresent() && level() instanceof ServerLevel serverLevel){
            canHit = canHit && canHitPredicate.get().matches(serverLevel, position(), target);
        }
        return canHit;
    }

    private void onHitCommon() {
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileTargeting effect : commonHitEffects) {
                effect.apply(serverLevel, player, projectileInstance, position());
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileBlockEffect effect : blockHitEffects) {
                effect.apply(serverLevel, player, projectileInstance, result.getBlockPos());
            }
        }

        onHitCommon();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        Entity attacker = getOwner();
        if(level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player)
        {
            boolean targetIsInImmunityFrames = target.invulnerableTime > 10.0F;
            for(ProjectileEntityEffect effect : entityHitEffects)
            {
                if(effect instanceof ProjectileDamageEffect damageEffect)
                {
                    DamageSource damageSource = new DamageSource(damageEffect.damageType(), this, attacker);
                    if(damageSource.is(DamageTypeTags.BYPASSES_COOLDOWN) || target.isInvulnerableTo(damageSource))
                    {
                        targetIsInImmunityFrames = true;
                    }
                }
            }

            if (getPierceLevel() == 0 && !targetIsInImmunityFrames) {
                setDeltaMovement(getDeltaMovement().scale(-0.1D));
                setYRot(getYRot() + 180.0F);
                yRotO += 180.0F;

                if (getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    remove(RemovalReason.DISCARDED);
                }
            } else {
                for (ProjectileEntityEffect effect : entityHitEffects) {
                    effect.apply(serverLevel, player, projectileInstance, target);
                }
            }
        }

        onHitCommon();
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof ServerPlayer player) {
            for (ProjectileTargeting effect : tickingEffects) {
                effect.apply(serverLevel, player, projectileInstance, position());
            }
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        // Can't return empty stack or we get an encoding error (player will never be able to pick this up anyways)
        ItemStack pickUpItem = new ItemStack(Items.STONE, 1);
        pickUpItem.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        return pickUpItem;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        // Can't return empty stack or we get an encoding error (player will never be able to pick this up anyways)
        ItemStack pickUpItem = new ItemStack(Items.STONE, 1);
        pickUpItem.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        return pickUpItem;
    }
}
