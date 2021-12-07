package by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.particles.CaveDragon.LargeFireParticleData;
import by.jackraidenph.dragonsurvival.particles.CaveDragon.SmallFireParticleData;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.sounds.FireBreathSound;
import by.jackraidenph.dragonsurvival.sounds.SoundRegistry;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;

public class FireBreathAbility extends BreathAbility
{
	public FireBreathAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public FireBreathAbility createInstance()
	{
		return new FireBreathAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public int getManaCost()
	{
		return (firstUse ? ConfigHandler.SERVER.fireBreathInitialMana.get() : ConfigHandler.SERVER.fireBreathOvertimeMana.get());
	}
	
	public void tickCost(){
		if(firstUse || player.tickCount % ConfigHandler.SERVER.fireBreathManaTicks.get() == 0){
			consumeMana(player);
			firstUse = false;
		}
	}
	
	@Override
	public boolean isDisabled()
	{
		return super.isDisabled() || !ConfigHandler.SERVER.fireBreath.get();
	}
	
	
	@Override
	public void stopCasting()
	{
		if(castingTicks > 1) {
			if (player.level.isClientSide) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> stopSound());
			}
		}
		
		super.stopCasting();
	}
	

	@Override
	public void onActivation(PlayerEntity player)
	{
		tickCost();
		super.onActivation(player);
		
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		if(handler == null) return;
		
		Vector3d delta = player.getDeltaMovement();
		float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);

		float f4 = MathHelper.sin(f1);
		float f5 = MathHelper.cos(f1);
		
		float size = DragonStateProvider.getCap(player).map((cap) -> cap.getSize()).get();
		
		double x = player.getX() + f4;
		double y = player.getY() + (size / 20F) - 0.2;
		double z = player.getZ() + f5;
		
		if(player.isFallFlying() || player.abilities.flying) {
			yComp += delta.y * 6;
		}
		
		xComp += delta.x * 6;
		zComp += delta.z * 6;
		
		if(player.isInWaterRainOrBubble() || player.level.isRainingAt(player.blockPosition())){
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
				player.level.addParticle(new SmallFireParticleData(37, true),x, y, z, xSpeed, ySpeed, zSpeed);
			}

			for (int i = 0; i < 10; i++) {
				double xSpeed = speed * xComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				player.level.addParticle(new LargeFireParticleData(37, false), x, y, z, xSpeed, ySpeed, zSpeed);
			}
		}
		
		hitEntities();
		
		if (player.tickCount % 20 == 0) {
			hitBlocks();
		}
	}
	@OnlyIn(Dist.CLIENT)
	private ISound startingSound;
	
	@OnlyIn(Dist.CLIENT)
	private TickableSound loopingSound;
	
	@OnlyIn(Dist.CLIENT)
	private ISound endSound;
	
	@OnlyIn(Dist.CLIENT)
	public void sound(){
		if (castingTicks == 1) {
			if(startingSound == null){
				startingSound = SimpleSound.forAmbientAddition(SoundRegistry.fireBreathStart);
			}
			Minecraft.getInstance().getSoundManager().play(startingSound);
		}
		
		if(loopingSound == null){
			loopingSound = new FireBreathSound(this);
		}
		
		if((!loopingSound.isLooping()
                    || loopingSound.isStopped()
                    || !Minecraft.getInstance().getSoundManager().isActive(loopingSound))
					|| player.tickCount % Functions.secondsToTicks(6) == 0){
			Minecraft.getInstance().getSoundManager().queueTickingSound(loopingSound);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void stopSound(){
		if(SoundRegistry.fireBreathEnd != null) {
			if (endSound == null) {
				endSound = SimpleSound.forAmbientAddition(SoundRegistry.fireBreathEnd);
			}
			
			Minecraft.getInstance().getSoundManager().play(endSound);
		}
	}
	
	@Override
	public boolean canHitEntity(LivingEntity entity)
	{
		return !entity.fireImmune();
	}
	
	@Override
	public void onEntityHit(LivingEntity entityHit)
	{
		//Short enough fire duration to not cause fire damage but still drop cooked items
		if(!entityHit.isOnFire()){
			entityHit.setRemainingFireTicks(1);
		}
		
		super.onEntityHit(entityHit);
		
		if(!entityHit.level.isClientSide) {
			if (entityHit.level.random.nextInt(100) < 30) {
				entityHit.addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(10), 0, false, true));
			}
		}
	}
	
	@Override
	public void onDamage(LivingEntity entity)
	{
		entity.setSecondsOnFire(30);
	}
	
	@Override
	public void onBlock(BlockPos pos, BlockState blockState)
	{
		if(!player.level.isClientSide) {
			if (ConfigHandler.SERVER.fireBreathSpreadsFire.get()) {
				Direction direction = player.getDirection().getOpposite();
				BlockPos blockPos = pos.relative(direction);
				
				if(AbstractFireBlock.canBePlacedAt(player.level, blockPos,direction)) {
					boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(player.level, player);
					
					if (flag) {
						if (player.level.random.nextInt(100) < 5) {
							BlockState blockstate1 = AbstractFireBlock.getState(player.level, blockPos);
							player.level.setBlock(blockPos, blockstate1, 3);
						}
					}
				}
			}
				
			if(player.level.random.nextInt(100) < 30){
				BlockState blockAbove = player.level.getBlockState(pos.above());
				
				if(blockAbove.getBlock() == Blocks.AIR) {
					AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
					entity.setWaitTime(0);
					entity.setPos(pos.above().getX(), pos.above().getY(), pos.above().getZ());
					entity.setPotion(new Potion(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(10) * 4))); //Effect duration is divided by 4 normaly
					entity.setDuration(Functions.secondsToTicks(2));
					entity.setRadius(1);
					entity.setParticle(new SmallFireParticleData(37, false));
					player.level.addFreshEntity(entity);
				}
			}
		}
		
			
		if(player.level.isClientSide) {
			for (int z = 0; z < 4; ++z) {
				if (player.level.random.nextInt(100) < 20) {
					player.level.addParticle(ParticleTypes.LAVA,pos.above().getX(), pos.above().getY(), pos.above().getZ(), 0, 0.05, 0);
				}
			}
		}
		
		if(player.level.isClientSide){
			if (blockState.getBlock() == Blocks.WATER) {
				for (int z = 0; z < 4; ++z) {
					if (player.level.random.nextInt(100) < 90) {
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
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.fireBreathDamage.get()));
		return list;
	}
}
