package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class GenericArrowEntity extends AbstractArrow {
    private int projectileLevel;
    public static final EntityDataAccessor<String> NAME = SynchedEntityData.defineId(GenericArrowEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> RES_LOCATION = SynchedEntityData.defineId(GenericArrowEntity.class, EntityDataSerializers.STRING);
    private Optional<EntityPredicate> canHitPredicate;
    private List<ProjectileTargeting> tickingEffects;
    private List<ProjectileTargeting> commonHitEffects;
    private List<ProjectileEntityEffect> entityHitEffects;
    private List<ProjectileBlockEffect> blockHitEffects;

    // Copied from AbstractArrow.java
    @Nullable private IntOpenHashSet piercingIgnoreEntityIds;

    // Copied from AbstractArrow.java
    @Nullable private Entity lastDeflectedBy;

    public GenericArrowEntity(
            ResourceLocation name,
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
        this.setName(name);
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

    private record GenericArrowEntityInstance(
            ResourceLocation name,
            ResourceLocation texture,
            Optional<EntityPredicate> canHitPredicate,
            List<ProjectileTargeting> tickingEffects,
            List<ProjectileTargeting> commonHitEffects,
            List<ProjectileEntityEffect> entityHitEffects,
            List<ProjectileBlockEffect> blockHitEffects,
            int projectileLevel,
            int piercingLevel)
    {
        public static final Codec<GenericArrowEntityInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("name").forGetter(GenericArrowEntityInstance::name),
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(GenericArrowEntityInstance::texture),
                        EntityPredicate.CODEC.optionalFieldOf("can_hit_predicate").forGetter(GenericArrowEntityInstance::canHitPredicate),
                        ProjectileTargeting.CODEC.listOf().fieldOf("ticking_effects").forGetter(GenericArrowEntityInstance::tickingEffects),
                        ProjectileTargeting.CODEC.listOf().fieldOf("common_hit_effects").forGetter(GenericArrowEntityInstance::commonHitEffects),
                        ProjectileEntityEffect.CODEC.listOf().fieldOf("entity_hit_effects").forGetter(GenericArrowEntityInstance::entityHitEffects),
                        ProjectileBlockEffect.CODEC.listOf().fieldOf("block_hit_effects").forGetter(GenericArrowEntityInstance::blockHitEffects),
                        Codec.INT.fieldOf("projectile_level").forGetter(GenericArrowEntityInstance::projectileLevel),
                        Codec.INT.fieldOf("piercing_level").forGetter(GenericArrowEntityInstance::piercingLevel)
                ).apply(instance, GenericArrowEntityInstance::new)
        );

        public void load(GenericArrowEntity entity) {
            entity.setName(name);
            entity.setResourceLocation(texture);
            entity.canHitPredicate = canHitPredicate;
            entity.tickingEffects = tickingEffects;
            entity.commonHitEffects = commonHitEffects;
            entity.entityHitEffects = entityHitEffects;
            entity.blockHitEffects = blockHitEffects;
            entity.projectileLevel = projectileLevel;
            entity.setPierceLevel((byte)piercingLevel);
        }

        public static GenericArrowEntityInstance fromEntity(GenericArrowEntity entity) {
            return new GenericArrowEntityInstance(
                    ResourceLocation.read(entity.entityData.get(NAME)).getOrThrow(),
                    entity.getResourceLocation(),
                    entity.canHitPredicate,
                    entity.tickingEffects,
                    entity.commonHitEffects,
                    entity.entityHitEffects,
                    entity.blockHitEffects,
                    entity.projectileLevel,
                    entity.getPierceLevel()
            );
        }
    }

    private void setName(ResourceLocation name) {
        this.entityData.set(NAME, name.toString());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Tag data = GenericArrowEntityInstance.CODEC.encodeStart(level().registryAccess().createSerializationContext(NbtOps.INSTANCE), GenericArrowEntityInstance.fromEntity(this)).getOrThrow();
        compound.put("generic_arrow_entity_instance", data);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        GenericArrowEntityInstance.CODEC.parse(level().registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("generic_arrow_entity_instance")).getOrThrow().load(this);
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
        pBuilder.define(NAME, ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "generic_arrow").toString());
        pBuilder.define(RES_LOCATION,  ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "generic_arrow").toString());
    }

    @Override
    protected @NotNull Component getTypeName() {
        Optional<ResourceLocation> name = ResourceLocation.read(entityData.get(NAME)).result();
        if(name.isEmpty()) {
            return super.getTypeName();
        }
        return Component.translatable(Translation.Type.PROJECTILE.wrap(name.get().getNamespace(), name.get().getPath()));
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
            boolean targetIsInvulnerableToDamageType = false;
            boolean considerImmunityFrames = true;
            for(ProjectileEntityEffect effect : entityHitEffects)
            {
                if(effect instanceof ProjectileDamageEffect damageEffect)
                {
                    if(damageEffect.damageType().is(DamageTypeTags.BYPASSES_COOLDOWN))
                    {
                        considerImmunityFrames = false;
                    }

                    DamageSource source = new DamageSource(damageEffect.damageType(), attacker);
                    if(target.isInvulnerableTo(source))
                    {
                        targetIsInvulnerableToDamageType = true;
                    }
                }
            }

            boolean targetIsInImmunityFrames = target.invulnerableTime > 10.0F;
            boolean targetIsInvulnerable = considerImmunityFrames ? targetIsInImmunityFrames && targetIsInvulnerableToDamageType : targetIsInvulnerableToDamageType;

            ProjectileDeflection deflection = target.deflection(this);
            if (target != this.lastDeflectedBy
                    && deflection != ProjectileDeflection.NONE
                    && this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1
                    && targetIsInvulnerable
                    // Short-circuit eval will prevent this from being called in situations where we don't want it
                    && this.deflect(ProjectileDeflection.REVERSE, this.getOwner(), target, target instanceof Player)) {
                this.lastDeflectedBy = target;
            } else {
                if(!targetIsInvulnerable) {
                    for (ProjectileEntityEffect effect : entityHitEffects) {
                        effect.apply(this, result.getEntity(), projectileLevel);
                    }

                    if (attacker instanceof ServerPlayer serverPlayer && !isSilent()) {
                        serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                    }
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
                } else {
                    this.discard();
                }
            }
            onHitCommon();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && !inGround) {
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
