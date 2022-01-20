package by.jackraidenph.dragonsurvival.common.entity.projectiles;

import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.items.DSItems;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class BolasEntity extends AbstractArrow
{
    public static final UUID DISABLE_MOVEMENT = UUID.fromString("eab67409-4834-43d8-bdf6-736dc96375f2");
    public static final UUID DISABLE_JUMP = UUID.fromString("d7c976cd-edba-46aa-9002-294d429d7741");

    public BolasEntity(Level world) {
        super(DSEntities.BOLAS_ENTITY, world);
    }

    public BolasEntity(double p_i50156_2_, double p_i50156_4_, double p_i50156_6_, Level  world) {
        super(DSEntities.BOLAS_ENTITY, p_i50156_2_, p_i50156_4_, p_i50156_6_, world);
    }

    public BolasEntity(LivingEntity shooter, Level  world) {
        super(DSEntities.BOLAS_ENTITY, shooter, world);
    }
    

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (!entity.level.isClientSide) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
                AttributeModifier bolasTrap = new AttributeModifier(DISABLE_MOVEMENT, "Bolas trap", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);
                boolean addEffect = false;
                if (!attributeInstance.hasModifier(bolasTrap)) {
                    attributeInstance.addTransientModifier(bolasTrap);
                    addEffect = true;
                }

                AttributeInstance jump = livingEntity.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
                if (jump != null) {
                    AttributeModifier disableJump = new AttributeModifier(DISABLE_JUMP, "Jump debuff", 3, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    if (!jump.hasModifier(disableJump)) {
                        jump.addTransientModifier(disableJump);
                        addEffect = true;
                    }
                }
                if (addEffect)
                    livingEntity.addEffect(new MobEffectInstance(DragonEffects.TRAPPED, Functions.secondsToTicks(20)));

            }
        }
    }


    protected void onHit(HitResult p_70227_1_) {
        super.onHit(p_70227_1_);
        if (!this.level.isClientSide)
            remove(RemovalReason.DISCARDED);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    @Override
    protected ItemStack getPickupItem()
    {
        return new ItemStack( DSItems.huntingNet);
    }
}