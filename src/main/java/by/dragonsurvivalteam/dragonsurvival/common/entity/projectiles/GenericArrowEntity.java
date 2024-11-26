package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.BlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.DamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.EntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.PositionalTargeting;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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
    private final Optional<EntityPredicate> canHitPredicate;
    private final List<PositionalTargeting> tickingEffects;
    private final List<PositionalTargeting> commonHitEffects;
    private final List<EntityEffect> entityHitEffects;
    private final List<BlockEffect> blockHitEffects;
    private final DragonAbilityInstance ability;

    public GenericArrowEntity(EntityType<? extends AbstractArrow> entityType,
                              Optional<EntityPredicate> canHitPredicate,
                              List<PositionalTargeting> tickingEffects,
                              List<PositionalTargeting> commonHitEffects,
                              List<EntityEffect> entityHitEffects,
                              List<BlockEffect> blockHitEffects,
                              DragonAbilityInstance ability,
                              Level level,
                              int piercingLevel) {
        super(entityType, level);
        this.canHitPredicate = canHitPredicate;
        this.tickingEffects = tickingEffects;
        this.commonHitEffects = commonHitEffects;
        this.entityHitEffects = entityHitEffects;
        this.blockHitEffects = blockHitEffects;
        this.ability = ability;
        this.setPierceLevel((byte)piercingLevel);
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
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof Player player) {
            for (PositionalTargeting effect : commonHitEffects) {
                effect.apply(serverLevel, player, ability, position());
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof Player player) {
            for (BlockEffect effect : blockHitEffects) {
                effect.apply(serverLevel, player, ability, result.getBlockPos());
            }
        }

        onHitCommon();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        Entity attacker = getOwner();
        if(level() instanceof ServerLevel serverLevel && getOwner() instanceof Player player)
        {
            boolean targetIsInImmunityFrames = target.invulnerableTime > 10.0F;
            for(EntityEffect effect : entityHitEffects)
            {
                if(effect instanceof DamageEffect damageEffect)
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
                for (EntityEffect effect : entityHitEffects) {
                    effect.apply(serverLevel, player, ability, target);
                }
            }
        }

        onHitCommon();
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel serverLevel && getOwner() instanceof Player player) {
            for (PositionalTargeting effect : tickingEffects) {
                effect.apply(serverLevel, player, ability, position());
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
