package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.LargePoisonParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.SmallPoisonParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.PoisonBreathSound;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.GenericCapability;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;

public class ForestBreathAbility extends BreathAbility{
	@OnlyIn( Dist.CLIENT )
	private SoundInstance startingSound;
	@OnlyIn( Dist.CLIENT )
	private TickableSoundInstance loopingSound;
	@OnlyIn( Dist.CLIENT )
	private SoundInstance endSound;

	public ForestBreathAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public ForestBreathAbility createInstance(){
		return new ForestBreathAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.forestBreathDamage.get()));
		return list;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.forestBreath.get();
	}

	@Override
	public void onBlock(BlockPos pos, BlockState blockState, Direction direction){
		if(blockState.getMaterial().isSolidBlocking()){
			if(!player.level.isClientSide){
				if(player.level.random.nextInt(100) < 30){
					AreaEffectCloud entity = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, player.level);
					entity.setWaitTime(0);
					entity.setPos(pos.above().getX(), pos.above().getY(), pos.above().getZ());
					entity.setPotion(new Potion(new MobEffectInstance(DragonEffects.DRAIN, Functions.secondsToTicks(10) * 4))); //Effect duration is divided by 4 normaly
					entity.setDuration(Functions.secondsToTicks(2));
					entity.setRadius(1);
					entity.setParticle(new LargePoisonParticleData(37, false));
					player.level.addFreshEntity(entity);
				}
			}
		}
		if(blockState.getBlock() == Blocks.POTATOES){
			if(player.level.random.nextInt(100) < 10){
				PotatoBlock bl = (PotatoBlock)blockState.getBlock();
				if(bl.isMaxAge(blockState)){
					player.level.destroyBlock(pos, false);
					player.level.addFreshEntity(new ItemEntity(player.level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, new ItemStack(Items.POISONOUS_POTATO)));
				}
			}
		}


		if(blockState.getBlock() != Blocks.GRASS_BLOCK && blockState.getBlock() != Blocks.GRASS){
			if(player.level.random.nextInt(100) < 50){
				if(blockState.getBlock() instanceof BonemealableBlock){
					if(!DragonConfigHandler.FOREST_DRAGON_BREATH_GROW_BLACKLIST.contains(blockState.getBlock())){
						BonemealableBlock igrowable = (BonemealableBlock)blockState.getBlock();
						if(igrowable.isValidBonemealTarget(player.level, pos, blockState, player.level.isClientSide)){
							if(player.level instanceof ServerLevel){
								if(igrowable.isBonemealSuccess(player.level, player.level.random, pos, blockState)){
									for(int i = 0; i < 3; i++){
										if(igrowable != null){
											igrowable.performBonemeal((ServerLevel)player.level, player.level.random, pos, blockState);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}	@Override

	public void onActivation(Player player){

		tickCost();
		super.onActivation(player);

		if(player.hasEffect(DragonEffects.STRESS)){
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
				player.level.addParticle(new LargePoisonParticleData(37, true), dx, dy, dz, xSpeed, ySpeed, zSpeed);
			}

			for(int i = 0; i < 10; i++){
				double xSpeed = speed * xComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				player.level.addParticle(new SmallPoisonParticleData(37, false), dx, dy, dz, xSpeed, ySpeed, zSpeed);
			}
		}

		hitEntities();

		if(player.tickCount % 10 == 0){
			hitBlocks();
		}
	}

	public void tickCost(){
		if(firstUse || castingTicks % ConfigHandler.SERVER.forestBreathManaTicks.get() == 0){
			consumeMana(player);
			firstUse = false;
		}
	}

	@Override
	public int getManaCost(){
		return player != null && player.hasEffect(DragonEffects.SOURCE_OF_MAGIC) ? 0 : (firstUse ? ConfigHandler.SERVER.forestBreathInitialMana.get() : ConfigHandler.SERVER.forestBreathOvertimeMana.get());
	}

	@Override
	public void stopCasting(){
		if(castingTicks > 1){
			if(player.level.isClientSide){
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> stopSound());
			}
		}

		super.stopCasting();
	}

	@OnlyIn( Dist.CLIENT )
	public void stopSound(){
		castingTicks = 0;


		if(SoundRegistry.forestBreathEnd != null){
			if(endSound == null){
				endSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.forestBreathEnd);
			}

			Minecraft.getInstance().getSoundManager().play(endSound);
		}


		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "forest_breath_loop"), SoundSource.PLAYERS);
	}

	@Override

	public boolean canHitEntity(LivingEntity entity){
		return !(entity instanceof Player) || player.canHarmPlayer(((Player)entity));
	}

	@Override
	public void onEntityHit(LivingEntity entityHit){
		super.onEntityHit(entityHit);


		if(!entityHit.level.isClientSide){
			if(entityHit.level.random.nextInt(100) < 30){

				GenericCapability cap = GenericCapabilityProvider.getGenericCapability(entityHit).orElse(null);
				if(cap != null){
					cap.lastAfflicted = player != null ? player.getId() : -1;
				}
				entityHit.addEffect(new MobEffectInstance(DragonEffects.DRAIN, Functions.secondsToTicks(10), 0, false, true));
			}
		}
	}

	@Override
	public void onDamage(LivingEntity entity){}

	public float getDamage(){
		return getDamage(getLevel());
	}

	public static float getDamage(int level){
		return (float)(ConfigHandler.SERVER.forestBreathDamage.get() * level);
	}

	@OnlyIn( Dist.CLIENT )

	public void sound(){
		if(castingTicks == 2){
			if(startingSound == null){
				startingSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.forestBreathStart);
			}
			Minecraft.getInstance().getSoundManager().play(startingSound);
			loopingSound = new PoisonBreathSound(this);

			Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "forest_breath_loop"), SoundSource.PLAYERS);
			Minecraft.getInstance().getSoundManager().play(loopingSound);
		}
	}


}