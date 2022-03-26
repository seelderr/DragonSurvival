package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.ProjectileItem;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.potion.MobEffectInstance;
import net.minecraft.util.math.EntityHitResult;
import net.minecraft.util.math.HitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class Bolas extends ProjectileItem{
	public static final UUID DISABLE_MOVEMENT = UUID.fromString("eab67409-4834-43d8-bdf6-736dc96375f2");
	public static final UUID DISABLE_JUMP = UUID.fromString("d7c976cd-edba-46aa-9002-294d429d7741");

	public Bolas(Level world){
		super(DSEntities.BOLAS_ENTITY, world);
	}

	public Bolas(double p_i50156_2_, double p_i50156_4_, double p_i50156_6_, Level world){
		super(DSEntities.BOLAS_ENTITY, p_i50156_2_, p_i50156_4_, p_i50156_6_, world);
	}

	public Bolas(LivingEntity shooter, Level world){
		super(DSEntities.BOLAS_ENTITY, shooter, world);
	}


	protected Item getDefaultItem(){
		return DSItems.huntingNet;
	}

	protected void onHit(HitResult p_70227_1_){
		super.onHit(p_70227_1_);
		if(!this.level.isClientSide){
			remove();
		}
	}

	protected void onHit(EntityHitResult entityHitResult){
		super.onHit(entityHitResult);
		Entity entity = entityHitResult.get();
		if(!entity.level.isClientSide){
			if(entity instanceof LivingEntity){
				LivingEntity living = (LivingEntity)entity;
				AttributeInstance attributeInstance = living.getAttribute(Attributes.MOVEMENT_SPEED);
				AttributeModifier bolasTrap = new AttributeModifier(DISABLE_MOVEMENT, "Bolas trap", -attributeInstance.getValue(), AttributeModifier.Operation.ADDITION);
				boolean addEffect = false;
				if(!attributeInstance.hasModifier(bolasTrap)){
					attributeInstance.addTransientModifier(bolasTrap);
					addEffect = true;
				}

				AttributeInstance jump = living.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
				if(jump != null){
					AttributeModifier disableJump = new AttributeModifier(DISABLE_JUMP, "Jump debuff", 3, AttributeModifier.Operation.MULTIPLY_TOTAL);
					if(!jump.hasModifier(disableJump)){
						jump.addTransientModifier(disableJump);
						addEffect = true;
					}
				}
				if(addEffect){
					living.addEffect(new MobEffectInstance(DragonEffects.TRAPPED, Functions.secondsToTicks(20)));
				}
			}
		}
	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}