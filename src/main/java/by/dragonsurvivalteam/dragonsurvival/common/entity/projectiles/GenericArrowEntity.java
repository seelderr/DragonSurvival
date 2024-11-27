package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class GenericArrowEntity extends AbstractArrow {
    private final int projectileLevel;
    public static final EntityDataAccessor<String> RES_LOCATION = SynchedEntityData.defineId(GenericArrowEntity.class, EntityDataSerializers.STRING);
    private final Optional<EntityPredicate> canHitPredicate;
    private final List<ProjectileTargeting> tickingEffects;
    private final List<ProjectileTargeting> commonHitEffects;
    private final List<ProjectileEntityEffect> entityHitEffects;
    private final List<ProjectileBlockEffect> blockHitEffects;

    // Copied from AbstractArrow.java
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;

    public GenericArrowEntity(
            ResourceLocation location,
            Optional<EntityPredicate> canHitPredicate,
            List<ProjectileTargeting> tickingEffects,
            List<ProjectileTargeting> commonHitEffects,
            List<ProjectileEntityEffect> entityHitEffects,
            List<ProjectileBlockEffect> blockHitEffects,
            Level level,
            int projectileLevel,
            int piercingLevel) {
        super(DSEntities.GENERIC_ARROW_ENTITY.get(), level);
        this.setResourceLocation(location);
        this.canHitPredicate = canHitPredicate;
        this.tickingEffects = tickingEffects;
        this.commonHitEffects = commonHitEffects;
        this.entityHitEffects = entityHitEffects;
        this.blockHitEffects = blockHitEffects;
        this.projectileLevel = projectileLevel;
        this.setPierceLevel((byte)piercingLevel);
    }

    public GenericArrowEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.projectileLevel = 0;
        this.canHitPredicate = Optional.empty();
        this.tickingEffects = List.of();
        this.commonHitEffects = List.of();
        this.entityHitEffects = List.of();
        this.blockHitEffects = List.of();
    }

    @Override
    public byte getPierceLevel() {
        if(!level().isClientSide) {
            return super.getPierceLevel();
        } else {
            // If we don't lie and return 0, the client will hang in a loop checking for deflects
            return 0;
        }
    }

    private void setResourceLocation(ResourceLocation location) {
        this.entityData.set(RES_LOCATION, location.toString());
    }

    public ResourceLocation getResourceLocation() {
        return ResourceLocation.read(this.entityData.get(RES_LOCATION)).getOrThrow();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(RES_LOCATION,  ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "generic_arrow").toString());
    }

    @Override
    protected @NotNull Component getTypeName() {
        return Component.translatable(getResourceLocation().getNamespace() + Translation.Type.PROJECTILE.suffix + "." + getResourceLocation().getPath());
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        boolean canHit = super.canHitEntity(target) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(target.getId()));
        if(canHitPredicate.isPresent() && level() instanceof ServerLevel serverLevel){
            canHit = canHit && canHitPredicate.get().matches(serverLevel, position(), target);
        }
        return canHit;
    }

    private void onHitCommon() {
        if (!level().isClientSide) {
            for (ProjectileTargeting effect : commonHitEffects) {
                effect.apply(this, projectileLevel);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            for (ProjectileBlockEffect effect : blockHitEffects) {
                effect.apply(this, result.getBlockPos(), projectileLevel);
            }
            onHitCommon();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity attacker = getOwner();
        if(!level().isClientSide)
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

            if (getPierceLevel() == 0 && targetIsInImmunityFrames) {
                setDeltaMovement(getDeltaMovement().scale(-0.1D));
                setYRot(getYRot() + 180.0F);
                yRotO += 180.0F;

                if (getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
            } else {
                for (ProjectileEntityEffect effect : entityHitEffects) {
                    effect.apply(this, result.getEntity(), projectileLevel);
                }

                if (attacker instanceof ServerPlayer serverPlayer && !isSilent()) {
                    serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

                // Copied from AbstractArrow.java
                if (this.getPierceLevel() > 0) {
                    if (this.piercingIgnoreEntityIds == null) {
                        this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
                    }

                    if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                        this.discard();
                        return;
                    }

                    this.piercingIgnoreEntityIds.add(target.getId());
                }
            }
            onHitCommon();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            for (ProjectileTargeting effect : tickingEffects) {
                effect.apply(this, projectileLevel);
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
