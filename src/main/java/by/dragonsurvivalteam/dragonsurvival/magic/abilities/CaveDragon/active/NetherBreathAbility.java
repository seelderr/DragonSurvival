package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.LargeFireParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.SmallFireParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.FireBreathSound;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigType;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.BurnAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;
import java.util.List;

@RegisterDragonAbility
public class NetherBreathAbility extends BreathAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreath", comment = "Whether the firebreath ability should be enabled" )
	public static Boolean fireBreath = true;
	@ConfigRange( min = 0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathDamage", comment = "The amount of damage the firebreath ability deals. This value is multiplied by the skill level." )
	public static Double fireBreathDamage = 3.0;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathInitialMana", comment = "The mana cost for starting the firebreath ability" )
	public static Integer fireBreathInitialMana = 2;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathOvertimeMana", comment = "The mana cost of sustaining the firebreath ability" )
	public static Integer fireBreathOvertimeMana = 1;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathManaTicks", comment = "How often in ticks, mana is consumed while using fire breath" )
	public static Integer fireBreathManaTicks = 40;
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathSpreadsFire", comment = "Whether the fire breath actually spreads fire when used" )
	public static Boolean fireBreathSpreadsFire = true;
	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathBlockBreaks", comment = "Blocks that have a chance to be broken by fire breath. Formatting: block/modid:id" )
	public static List<String> fireBreathBlockBreaks = List.of("minecraft:impermeable", "minecraft:crops", "minecraft:flowers", "minecraft:replaceable_plants", "minecraft:cobweb");
	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathCooldown", comment = "The cooldown in ticks of the fire breath ability" )
	public static Integer fireBreathCooldown = 100;
	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "fire_breath"}, key = "fireBreathCasttime", comment = "The cast time in ticks of the fire breath ability" )
	public static Integer fireBreathCasttime = 20;
	@OnlyIn( Dist.CLIENT )
	private SoundInstance startingSound;

	@OnlyIn( Dist.CLIENT )
	private TickableSoundInstance loopingSound;

	@OnlyIn( Dist.CLIENT )
	private SoundInstance endSound;


	@Override
	public String getName(){
		return "nether_breath";
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/nether_breath_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/nether_breath_1.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/nether_breath_2.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/nether_breath_3.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/nether_breath_4.png")};
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.damage", "+" + fireBreathDamage));
		return list;
	}

	@Override
	public int getMaxLevel(){
		return 4;
	}

	@Override
	public int getMinLevel(){
		return 1;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !fireBreath;
	}

	@Override
	public int getManaCost(){
		return fireBreathOvertimeMana;
	}

	@Override
	public Integer[] getRequiredLevels(){
		return new Integer[]{0, 10, 30, 50};
	}

	@Override
	public int getSkillCooldown(){
		return fireBreathCooldown;
	}

	@Override
	public void onBlock(BlockPos pos, BlockState blockState, Direction direction){
		if(!player.level.isClientSide){
			if(fireBreathSpreadsFire){
				BlockPos blockPos = pos.relative(direction);

				if(FireBlock.canBePlacedAt(player.level, blockPos, direction)){
					boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(player.level, player);

					if(flag){
						if(player.level.random.nextInt(100) < 50){
							BlockState blockstate1 = FireBlock.getState(player.level, blockPos);
							player.level.setBlock(blockPos, blockstate1, 3);
						}
					}
				}
			}
			DragonStateHandler handler = DragonUtils.getHandler(player);

			BurnAbility burnAbility = DragonAbilities.getAbility(player, BurnAbility.class);
			if(player.level.random.nextInt(100) < (burnAbility.level * 15)){
				BlockState blockAbove = player.level.getBlockState(pos.above());

				if(blockAbove.getBlock() == Blocks.AIR){
					AreaEffectCloud entity = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, player.level);
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

	@Override
	public void onChanneling(Player player, int castDuration){
		super.onChanneling(player, castDuration);

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

		if(player.level.isClientSide && castDuration <= 1){
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

	@OnlyIn( Dist.CLIENT )
	public  void sound(){
		if(startingSound == null){
			startingSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.fireBreathStart);
		}
		Minecraft.getInstance().getSoundManager().playDelayed(startingSound, 0);
		loopingSound = new FireBreathSound(this);

		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop"), SoundSource.PLAYERS);
		Minecraft.getInstance().getSoundManager().queueTickingSound(loopingSound);
	}

	@OnlyIn( Dist.CLIENT )
	public  void stopSound(){
		if(SoundRegistry.fireBreathEnd != null){
			if(endSound == null){
				endSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.fireBreathEnd);
			}

			Minecraft.getInstance().getSoundManager().playDelayed(endSound, 0);
		}

		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop"), SoundSource.PLAYERS);
	}

	@Override
	public boolean canHitEntity(LivingEntity entity){
		return (!(entity instanceof Player) || player.canHarmPlayer(((Player)entity))) && !entity.fireImmune();
	}

	@Override
	public void onEntityHit(LivingEntity entityHit){
		//Short enough fire duration to not cause fire damage but still drop cooked items
		if(!entityHit.isOnFire()){
			entityHit.setRemainingFireTicks(1);
		}

		super.onEntityHit(entityHit);

		if(!entityHit.level.isClientSide){
			DragonStateHandler handler = DragonUtils.getHandler(player);
			BurnAbility burnAbility = DragonAbilities.getAbility(player, BurnAbility.class);

			if(entityHit.level.random.nextInt(100) < (burnAbility.level * 15)){
				DragonUtils.getHandler(entityHit).lastAfflicted = player != null ? player.getId() : -1;
				entityHit.addEffect(new MobEffectInstance(DragonEffects.BURN, Functions.secondsToTicks(10), 0, false, true));
			}
		}
	}

	@Override
	public void onDamage(LivingEntity entity){
		entity.setSecondsOnFire(30);
	}

	public static float getDamage(int level){
		return (float)(fireBreathDamage * level);
	}

	public float getDamage(){
		return getDamage(getLevel());
	}

	@Override
	public int getSkillChargeTime(){
		return fireBreathCasttime;
	}

	@Override
	public int getChargingManaCost(){
		return 2;
	}

	@Override
	public void castComplete(Player player){
		if(player.level.isClientSide){
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)this::stopSound);
		}
	}

	@Override
	public boolean requiresStationaryCasting(){
		return false;
	}
}