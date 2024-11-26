package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GenericArrowEntity extends AbstractArrow {
    private final double damage;
    private final float explosionPower;
    private final ResourceKey<DamageType> damageType;
    private final Optional<EntityPredicate> canHitPredicate;

    public GenericArrowEntity(EntityType<? extends AbstractArrow> entityType,
                              Optional<EntityPredicate> canHitPredicate,
                              ResourceKey<DamageType> damageType,
                              Level level,
                              double damage,
                              int piercingLevel,
                              float explosionPower) {
        super(entityType, level);
        this.damage = damage;
        this.explosionPower = explosionPower;
        this.damageType = damageType;
        this.canHitPredicate = canHitPredicate;
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

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        Entity attacker = getOwner();
        DamageSource damagesource;
        if (attacker == null) {
            damagesource = damageSources().source(damageType, this, this);
        } else {
            damagesource = damageSources().source(damageType, this, attacker);
            if (attacker instanceof LivingEntity livingEntity) {
                livingEntity.setLastHurtMob(target);
            }
        }
        float damage = (float) getBaseDamage();

        boolean targetIsInImmunityFrames = (target.invulnerableTime > 10.0F && !damagesource.is(DamageTypeTags.BYPASSES_COOLDOWN));
        if (TargetingFunctions.attackTargets(getOwner(), ent -> ent.hurt(damagesource, damage), target)) {
            if (target instanceof LivingEntity livingentity) {
                if (!level().isClientSide()) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                if (!level().isClientSide() && attacker instanceof LivingEntity) {
                    EnchantmentHelper.doPostAttackEffects((ServerLevel) level(), livingentity, damagesource);
                }

                doPostHurtEffects(livingentity);

                if(explosionPower > 0) {
                    level().explode(attacker, damagesource, null, getX(), getY(), getZ(), explosionPower, false, Level.ExplosionInteraction.TRIGGER);
                }

                if (attacker instanceof ServerPlayer serverPlayer && !isSilent()) {
                    serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }
            }

            if (getPierceLevel() <= 0) {
                remove(RemovalReason.DISCARDED);
            }
        } else if (getPierceLevel() == 0 && !targetIsInImmunityFrames) {
            setDeltaMovement(getDeltaMovement().scale(-0.1D));
            setYRot(getYRot() + 180.0F);
            yRotO += 180.0F;

            if (getDeltaMovement().lengthSqr() < 1.0E-7D) {
                remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public double getBaseDamage() {
        return damage;
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
