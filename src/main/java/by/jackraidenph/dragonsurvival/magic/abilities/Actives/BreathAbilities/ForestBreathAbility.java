package by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.particles.ForestDragon.LargePoisonParticleData;
import by.jackraidenph.dragonsurvival.particles.ForestDragon.SmallPoisonParticleData;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.sounds.PoisonBreathSound;
import by.jackraidenph.dragonsurvival.sounds.SoundRegistry;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.PotatoBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.TickableSound;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;

public class ForestBreathAbility extends BreathAbility
{
	public ForestBreathAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public ForestBreathAbility createInstance()
	{
		return new ForestBreathAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public int getManaCost()
	{
		return (firstUse ? ConfigHandler.SERVER.forestBreathInitialMana.get() : ConfigHandler.SERVER.forestBreathOvertimeMana.get());
	}
	
	public void tickCost(){
		if(firstUse || castingTicks % ConfigHandler.SERVER.forestBreathManaTicks.get() == 0){
			consumeMana(player);
			firstUse = false;
		}
	}
	
	@Override
	public boolean isDisabled()
	{
		return super.isDisabled() || !ConfigHandler.SERVER.forestBreath.get();
	}
	
	public static float getDamage(int level){
		return (float)(ConfigHandler.SERVER.forestBreathDamage.get() * level);
	}
	
	public float getDamage(){
		return getDamage(getLevel());
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
				startingSound = SimpleSound.forAmbientAddition(SoundRegistry.forestBreathStart);
			}
			Minecraft.getInstance().getSoundManager().play(startingSound);
			
			loopingSound = new PoisonBreathSound(this);
			Minecraft.getInstance().getSoundManager().queueTickingSound(loopingSound);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void stopSound(){
		if(SoundRegistry.forestBreathEnd != null) {
			if (endSound == null) {
				endSound = SimpleSound.forAmbientAddition(SoundRegistry.forestBreathEnd);
			}
			
			Minecraft.getInstance().getSoundManager().play(endSound);
		}
		
		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "forest_breath_loop"), SoundCategory.PLAYERS);
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
		double y = player.getY() + (size / 20F) - 0.3;
		double z = player.getZ() + f5;
		
		if(player.isFallFlying() || player.abilities.flying) {
			yComp += delta.y * 6;
		}
		
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
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.forestBreathDamage.get()));
		return list;
	}
	
}
