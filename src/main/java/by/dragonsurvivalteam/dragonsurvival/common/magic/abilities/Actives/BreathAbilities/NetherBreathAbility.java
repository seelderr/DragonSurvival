<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
package by.jackraidenph.dragonsurvival.common.magic.abilities.Actives.BreathAbilities;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.particles.CaveDragon.LargeFireParticleData;
import by.jackraidenph.dragonsurvival.client.particles.CaveDragon.SmallFireParticleData;
import by.jackraidenph.dragonsurvival.client.sounds.FireBreathSound;
import by.jackraidenph.dragonsurvival.client.sounds.SoundRegistry;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.caps.GenericCapability;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.Functions;
=======
package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.LargeFireParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.SmallFireParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.FireBreathSound;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.GenericCapability;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;

public class NetherBreathAbility extends BreathAbility{
	@OnlyIn( Dist.CLIENT )
	private ISound startingSound;
	@OnlyIn( Dist.CLIENT )
	private TickableSound loopingSound;
	@OnlyIn( Dist.CLIENT )
	private ISound endSound;	@Override
	public int getManaCost(){
		return player != null && player.hasEffect(DragonEffects.SOURCE_OF_MAGIC) ? 0 : (firstUse ? ConfigHandler.SERVER.fireBreathInitialMana.get() : ConfigHandler.SERVER.fireBreathOvertimeMana.get());
	}

	public NetherBreathAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}	public void tickCost(){
		if(firstUse || player.tickCount % ConfigHandler.SERVER.fireBreathManaTicks.get() == 0){
			consumeMana(player);
			firstUse = false;
		}
	}

	@Override
	public NetherBreathAbility createInstance(){
		return new NetherBreathAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.fireBreathDamage.get()));
		return list;
	}	@Override
	public void stopCasting(){
		if(castingTicks > 1){
			if(player.level.isClientSide){
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> stopSound());
			}
		}

		super.stopCasting();
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	public void onActivation(Player player)
	{
=======
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.fireBreath.get();
	}	@Override
	public void onActivation(PlayerEntity player){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
		tickCost();
		super.onActivation(player);

		if(player.isInWaterRainOrBubble() || player.level.isRainingAt(player.blockPosition())){
			if(player.level.isClientSide){
				if(player.tickCount % 10 == 0){
					player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.25F, 1F);
				}

				for(int i = 0; i < 12; i++){
					double xSpeed = speed * 1f * xComp;
					double ySpeed = speed * 1f * yComp;
					double zSpeed = speed * 1f * zComp;
					player.level.addParticle(ParticleTypes.SMOKE, dx, dy, dz, xSpeed, ySpeed, zSpeed);
				}
			}
			return;
		}

		if(player.level.isClientSide){
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> sound());
		}

		if(player.level.isClientSide){
			for(int i = 0; i < 24; i++){
				double xSpeed = speed * 1f * xComp;
				double ySpeed = speed * 1f * yComp;
				double zSpeed = speed * 1f * zComp;
				player.level.addParticle(new SmallFireParticleData(37, true), dx, dy, dz, xSpeed, ySpeed, zSpeed);
			}

			for(int i = 0; i < 10; i++){
				double xSpeed = speed * xComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				player.level.addParticle(new LargeFireParticleData(37, false), dx, dy, dz, xSpeed, ySpeed, zSpeed);
			}
		}

		hitEntities();

		if(player.tickCount % 10 == 0){
			hitBlocks();
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	@OnlyIn(Dist.CLIENT)
	private SimpleSoundInstance startingSound;
	
	@OnlyIn(Dist.CLIENT)
	private TickableSoundInstance loopingSound;
	
	@OnlyIn(Dist.CLIENT)
	private SimpleSoundInstance endSound;
	
	@OnlyIn(Dist.CLIENT)
=======







	@OnlyIn( Dist.CLIENT )
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	public void sound(){
		if(castingTicks == 2){
			if(startingSound == null){
				startingSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.fireBreathStart);
			}
			Minecraft.getInstance().getSoundManager().play(startingSound);
			loopingSound = new FireBreathSound(this);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
			
			Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop"), SoundSource.PLAYERS);
=======

			Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop"), SoundCategory.PLAYERS);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
			Minecraft.getInstance().getSoundManager().play(loopingSound);
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void stopSound(){
		castingTicks = 0;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
		
		if(SoundRegistry.fireBreathEnd != null) {
			if (endSound == null) {
				endSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.fireBreathEnd);
=======

		if(SoundRegistry.fireBreathEnd != null){
			if(endSound == null){
				endSound = SimpleSound.forAmbientAddition(SoundRegistry.fireBreathEnd);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
			}

			Minecraft.getInstance().getSoundManager().play(endSound);
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
		
		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop"), SoundSource.PLAYERS);
=======

		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop"), SoundCategory.PLAYERS);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	public boolean canHitEntity(LivingEntity entity)
	{
		return (!(entity instanceof Player) || player.canHarmPlayer(((Player)entity))) && !entity.fireImmune();
=======
	public boolean canHitEntity(LivingEntity entity){
		return (!(entity instanceof PlayerEntity) || player.canHarmPlayer(((PlayerEntity)entity))) && !entity.fireImmune();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	}

	@Override
	public void onEntityHit(LivingEntity entityHit){
		//Short enough fire duration to not cause fire damage but still drop cooked items
		if(!entityHit.isOnFire()){
			entityHit.setRemainingFireTicks(1);
		}

		super.onEntityHit(entityHit);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
		
		if(!entityHit.level.isClientSide) {
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			
			if(handler != null) {
				if (entityHit.level.random.nextInt(100) < (handler.getMagic().getAbilityLevel(DragonAbilities.BURN) * 15)) {
=======

		if(!entityHit.level.isClientSide){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler != null){
				if(entityHit.level.random.nextInt(100) < (handler.getMagic().getAbilityLevel(DragonAbilities.BURN) * 15)){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
					GenericCapability cap = GenericCapabilityProvider.getGenericCapability(entityHit).orElse(null);
					if(cap != null){
						cap.lastAfflicted = player != null ? player.getId() : -1;
					}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
					
					entityHit.addEffect(new MobEffectInstance(DragonEffects.BURN, Functions.secondsToTicks(10), 0, false, true));
=======

					entityHit.addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(10), 0, false, true));
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
				}
			}
		}
	}

	@Override
	public void onDamage(LivingEntity entity){
		entity.setSecondsOnFire(30);
	}

	@Override
	public void onBlock(BlockPos pos, BlockState blockState, Direction direction){
		if(!player.level.isClientSide){
			if(ConfigHandler.SERVER.fireBreathSpreadsFire.get()){
				BlockPos blockPos = pos.relative(direction);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
				
				if(BaseFireBlock.canBePlacedAt(player.level, blockPos, direction)) {
					boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(player.level, player);
					
					if (flag) {
						if (player.level.random.nextInt(100) < 50) {
							BlockState blockstate1 = BaseFireBlock.getState(player.level, blockPos);
=======

				if(AbstractFireBlock.canBePlacedAt(player.level, blockPos, direction)){
					boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(player.level, player);

					if(flag){
						if(player.level.random.nextInt(100) < 50){
							BlockState blockstate1 = AbstractFireBlock.getState(player.level, blockPos);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
							player.level.setBlock(blockPos, blockstate1, 3);
						}
					}
				}
			}
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler != null){
				if(player.level.random.nextInt(100) < (handler.getMagic().getAbilityLevel(DragonAbilities.BURN) * 15)){
					BlockState blockAbove = player.level.getBlockState(pos.above());
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
					
					if(blockAbove.getBlock() == Blocks.AIR) {
						AreaEffectCloud entity = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, player.level);
=======

					if(blockAbove.getBlock() == Blocks.AIR){
						AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
						entity.setWaitTime(0);
						entity.setPos(pos.above().getX(), pos.above().getY(), pos.above().getZ());
						entity.setPotion(new Potion(new MobEffectInstance(DragonEffects.BURN, Functions.secondsToTicks(10) * 4))); //Effect duration is divided by 4 normaly
						entity.setDuration(Functions.secondsToTicks(2));
						entity.setRadius(1);
						entity.setParticle(new SmallFireParticleData(37, false));
						player.level.addFreshEntity(entity);
					}
				}
			}
		}


		if(player.level.isClientSide){
			for(int z = 0; z < 4; ++z){
				if(player.level.random.nextInt(100) < 20){
					player.level.addParticle(ParticleTypes.LAVA, pos.above().getX(), pos.above().getY(), pos.above().getZ(), 0, 0.05, 0);
				}
			}
		}

		if(player.level.isClientSide){
			if(blockState.getBlock() == Blocks.WATER){
				for(int z = 0; z < 4; ++z){
					if(player.level.random.nextInt(100) < 90){
						player.level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, pos.above().getX(), pos.above().getY(), pos.above().getZ(), 0, 0.05, 0);
					}
				}
			}
		}
	}

	public static float getDamage(int level){
		return (float)(ConfigHandler.SERVER.fireBreathDamage.get() * level);
	}

	public float getDamage(){
		return getDamage(getLevel());
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.fireBreathDamage.get()));
		return list;
	}
}
=======


}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/BreathAbilities/NetherBreathAbility.java
