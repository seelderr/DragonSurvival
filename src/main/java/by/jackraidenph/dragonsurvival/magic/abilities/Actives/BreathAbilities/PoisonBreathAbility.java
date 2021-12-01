package by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.particles.ForestDragon.LargePoisonParticleData;
import by.jackraidenph.dragonsurvival.particles.ForestDragon.SmallPoisonParticleData;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.sounds.PoisonBreathSound;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.PotatoBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;

public class PoisonBreathAbility extends BreathAbility
{
	public PoisonBreathAbility(String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public PoisonBreathAbility createInstance()
	{
		return new PoisonBreathAbility(id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void sound(){
		if (castingTicks == 1) {
			Minecraft.getInstance().getSoundManager().play(new PoisonBreathSound(this));
		}
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		tickCost();
		super.onActivation(player);
		
		Vector3d viewVector = player.getViewVector(1.0F);
		Vector3d delta = player.getDeltaMovement();
		
		double x = player.getX() + viewVector.x;
		double y = player.getY() + 1 + viewVector.y;
		double z = player.getZ() + viewVector.z;
		
		xComp += delta.x * 6;
		zComp += delta.z * 6;
		
		if (player.hasEffect(DragonEffects.STRESS)) {
			if(player.level.isClientSide) {
				if (player.tickCount % 10 == 0) {
					player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.25F, 1F);
				}
				
				for (int i = 0; i < 12; i++) {
					double xSpeed = speed * 1f * xComp;
					double ySpeed = speed * 1f * yComp;
					double zSpeed = speed * 1f * zComp;
					player.level.addParticle(ParticleTypes.SMOKE, x, y, z, xSpeed, ySpeed, zSpeed);
				}
			}
			return;
		}
		
		if(player.level.isClientSide) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> sound());
		}
		
		if(player.level.isClientSide) {
			for (int i = 0; i < 24; i++) {
				double xSpeed = speed * 1f * xComp;
				double ySpeed = speed * 1f * yComp;
				double zSpeed = speed * 1f * zComp;
				player.level.addParticle(new LargePoisonParticleData(37, true), x, y, z, xSpeed, ySpeed, zSpeed);
			}

			for (int i = 0; i < 10; i++) {
				double xSpeed = speed * xComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				player.level.addParticle(new SmallPoisonParticleData(37, false), x, y, z, xSpeed, ySpeed, zSpeed);
			}
		}

		hitEntities();
		
		if (player.tickCount % 20 == 0) {
			hitBlocks();
		}
	}
	
	@Override
	public void onEntityHit(LivingEntity entityHit)
	{
		super.onEntityHit(entityHit);
		
		if(!entityHit.level.isClientSide) {
			if (entityHit.level.random.nextInt(100) < 30) {
				entityHit.addEffect(new EffectInstance(DragonEffects.DRAIN, Functions.secondsToTicks(10), 0, false, true));
			}
		}
	}
	
	@Override
	public boolean canHitEntity(LivingEntity entity)
	{
		return true;
	}
	
	@Override
	public void onDamage(LivingEntity entity) {}
	
	@Override
	public void onBlock(BlockPos pos, BlockState blockState)
	{
		if(blockState.getMaterial().isSolidBlocking()) {
			if(!player.level.isClientSide) {
				if(player.level.random.nextInt(100) < 30){
					AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
					entity.setWaitTime(0);
					entity.setPos(pos.above().getX(), pos.above().getY(), pos.above().getZ());
					entity.setPotion(new Potion(new EffectInstance(DragonEffects.DRAIN, Functions.secondsToTicks(10) * 4))); //Effect duration is divided by 4 normaly
					entity.setDuration(Functions.secondsToTicks(2));
					entity.setRadius(1);
					entity.setParticle(new LargePoisonParticleData(37, false));
					player.level.addFreshEntity(entity);
				}
			}
		}
		if(blockState.getBlock() == Blocks.POTATOES){
			if (player.level.random.nextInt(100) < 10) {
				PotatoBlock bl = (PotatoBlock)blockState.getBlock();
				if(bl.isMaxAge(blockState)){
					player.level.destroyBlock(pos, false);
					player.level.addFreshEntity(new ItemEntity(player.level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, new ItemStack(Items.POISONOUS_POTATO)));
				}
			}
		}
		
		if(blockState.getBlock() != Blocks.GRASS_BLOCK && blockState.getBlock() != Blocks.GRASS) {
			if (player.level.random.nextInt(100) < 50) {
				if (blockState.getBlock() instanceof IGrowable) {
					IGrowable igrowable = (IGrowable)blockState.getBlock();
					if (igrowable.isValidBonemealTarget(player.level, pos, blockState, player.level.isClientSide)) {
						if (player.level instanceof ServerWorld) {
							if (igrowable.isBonemealSuccess(player.level, player.level.random, pos, blockState)) {
								for (int i = 0; i < 3; i++) {
									igrowable.performBonemeal((ServerWorld)player.level, player.level.random, pos, blockState);
								}
							}
						}
					}
				}
			}
		}
	}
	

	public static int getDamage(int level){
		return level;
	}

	public int getDamage(){
		return getDamage(getLevel());
	}
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.damage", "+1"));
		return list;
	}
}
