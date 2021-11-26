package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.entity.particle.LargeFireParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SmallFireParticleData;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BreathAbility extends ActiveDragonAbility
{
	private DragonType type;
	
	public BreathAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.type = type;
	}
	
	@Override
	public BreathAbility createInstance()
	{
		return new BreathAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	private static final int RANGE = 10;
	private static final int ARC = 45;
	
	public int channelCost = 1;
	private boolean firstUse = true;
	
	public boolean canConsumeMana(PlayerEntity player) {
		return player.isCreative() || DragonStateProvider.getCurrentMana(player) >= (firstUse ? this.getManaCost() : channelCost)
		       || (player.totalExperience / 10) >= (firstUse ? this.getManaCost() : channelCost) || player.experienceLevel > 0;
	}
	
	public void stopCasting() {
		super.stopCasting();
		
		if(getCooldown() == 0 && !firstUse){
			startCooldown();
			firstUse = true;
		}
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		if(firstUse) {
			DragonStateProvider.consumeMana(player, this.getManaCost());
			firstUse = false;
		}else{
			if(player.tickCount % Functions.secondsToTicks(2) == 0){
				DragonStateProvider.consumeMana(player, channelCost);
			}
		}
		
		float yaw = (float) Math.toRadians(-player.yRot);
		float pitch = (float) Math.toRadians(-player.xRot);
		float speed = 0.56f; //Changes distance
		float spread = 0.25f;
		float xComp = (float) (Math.sin(yaw) * Math.cos(pitch));
		float yComp = (float) (Math.sin(pitch));
		float zComp = (float) (Math.cos(yaw) * Math.cos(pitch));
		
		World level = player.level;
		
		if(level.isClientSide) {
			for (int i = 0; i < 24; i++) {
				double xSpeed = speed * 1f * xComp;
				double ySpeed = speed * 1f * yComp;
				double zSpeed = speed * 1f * zComp;
				level.addParticle(new SmallFireParticleData(37f, true), player.getX(), player.getY() + 0.5, player.getZ(), xSpeed, ySpeed, zSpeed);
			}
			
			for (int i = 0; i < 6; i++) {
				double xSpeed = speed * xComp + (spread * 0.7 * (level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
				double ySpeed = speed * yComp + (spread * 0.7 * (level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
				double zSpeed = speed * zComp + (spread * 0.7 * (level.random.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
				level.addParticle(new LargeFireParticleData(37f, true), player.getX(), player.getY() + 0.5, player.getZ(), xSpeed, ySpeed, zSpeed);
			}
		}
		
		hitEntities();
		hitBlocks();
	}
	
	public void hitEntities() {
		List<LivingEntity> entitiesHit = getEntityLivingBaseNearby(RANGE, RANGE, RANGE, RANGE);
		float damage = getLevel();
		for (LivingEntity entityHit : entitiesHit) {
			if (entityHit == player) continue;
			
			float entityHitYaw = (float) ((Math.atan2(entityHit.getZ() - player.getZ(), entityHit.getX() - player.getX()) * (180 / Math.PI) - 90) % 360);
			float entityAttackingYaw = player.yRot % 360;
			if (entityHitYaw < 0) {
				entityHitYaw += 360;
			}
			if (entityAttackingYaw < 0) {
				entityAttackingYaw += 360;
			}
			float entityRelativeYaw = entityHitYaw - entityAttackingYaw;
			
			float xzDistance = (float) Math.sqrt((entityHit.getZ() - player.getZ()) * (entityHit.getZ() - player.getZ()) + (entityHit.getX() - player.getX()) * (entityHit.getX() - player.getX()));
			double hitY = entityHit.getY() + entityHit.getBbHeight() / 2.0;
			float entityHitPitch = (float) ((Math.atan2((hitY - player.getY()), xzDistance) * (180 / Math.PI)) % 360);
			float entityAttackingPitch = -player.xRot % 360;
			if (entityHitPitch < 0) {
				entityHitPitch += 360;
			}
			if (entityAttackingPitch < 0) {
				entityAttackingPitch += 360;
			}
			float entityRelativePitch = entityHitPitch - entityAttackingPitch;
			
			float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - player.getZ()) * (entityHit.getZ() - player.getZ()) + (entityHit.getX() - player.getX()) * (entityHit.getX() - player.getX()) + (hitY - player.getY()) * (hitY - player.getY()));
			
			boolean inRange = entityHitDistance <= RANGE;
			boolean yawCheck = (entityRelativeYaw <= ARC / 2f && entityRelativeYaw >= -ARC / 2f) || (entityRelativeYaw >= 360 - ARC / 2f || entityRelativeYaw <= -360 + ARC / 2f);
			boolean pitchCheck = (entityRelativePitch <= ARC / 2f && entityRelativePitch >= -ARC / 2f) || (entityRelativePitch >= 360 - ARC / 2f || entityRelativePitch <= -360 + ARC / 2f);
			if (inRange && yawCheck && pitchCheck) {
				// Raytrace to mob center to avoid damaging through walls
				Vector3d from = player.position();
				Vector3d to = entityHit.position().add(0, entityHit.getEyeHeight() / 2.0f, 0);
				BlockRayTraceResult result = player.level.clip(new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
				
				if (result.getType() == RayTraceResult.Type.BLOCK) {
					continue;
				}
				
				if (entityHit.hurt(DamageSource.mobAttack(player), damage)) {
					entityHit.setSecondsOnFire(30);
					entityHit.setDeltaMovement(entityHit.getDeltaMovement().multiply(0.25, 1, 0.25));
				}
			}
		}
	}
	
	public void hitBlocks() {
		int checkDist = 10;
		for (int i = (int)player.getX() - checkDist; i < (int)player.getX() + checkDist; i++) {
			for (int j = (int)player.getY() - checkDist; j < (int)player.getY() + checkDist; j++) {
				for (int k = (int)player.getZ() - checkDist; k < (int)player.getZ() + checkDist; k++) {
					BlockPos pos = new BlockPos(i, j, k);
					
					BlockState blockState = player.level.getBlockState(pos);
					BlockState blockStateAbove = player.level.getBlockState(pos.above());
					if (blockState.getBlock() != Blocks.WATER || blockStateAbove.getBlock() != Blocks.AIR) {
						continue;
					}
					
					float blockHitYaw = (float) ((Math.atan2(pos.getZ() - player.getZ(), pos.getX() - player.getX()) * (180 / Math.PI) - 90) % 360);
					float entityAttackingYaw = player.yRot % 360;
					if (blockHitYaw < 0) {
						blockHitYaw += 360;
					}
					if (entityAttackingYaw < 0) {
						entityAttackingYaw += 360;
					}
					float blockRelativeYaw = blockHitYaw - entityAttackingYaw;
					
					float xzDistance = (float) Math.sqrt((pos.getZ() - player.getZ()) * (pos.getZ() - player.getZ()) + (pos.getX() - player.getX()) * (pos.getX() - player.getX()));
					float blockHitPitch = (float) ((Math.atan2((pos.getY() - player.getY()), xzDistance) * (180 / Math.PI)) % 360);
					float entityAttackingPitch = -player.xRot % 360;
					if (blockHitPitch < 0) {
						blockHitPitch += 360;
					}
					if (entityAttackingPitch < 0) {
						entityAttackingPitch += 360;
					}
					float blockRelativePitch = blockHitPitch - entityAttackingPitch;
					
					float blockHitDistance = (float) Math.sqrt((pos.getZ() - player.getZ()) * (pos.getZ() - player.getZ()) + (pos.getX() - player.getX()) * (pos.getX() - player.getX()) + (pos.getY() - player.getY()) * (pos.getY() - player.getY()));
					
					boolean inRange = blockHitDistance <= RANGE;
					boolean yawCheck = (blockRelativeYaw <= ARC / 2f && blockRelativeYaw >= -ARC / 2f) || (blockRelativeYaw >= 360 - ARC / 2f || blockRelativeYaw <= -360 + ARC / 2f);
					boolean pitchCheck = (blockRelativePitch <= ARC / 2f && blockRelativePitch >= -ARC / 2f) || (blockRelativePitch >= 360 - ARC / 2f || blockRelativePitch <= -360 + ARC / 2f);
					if (inRange && yawCheck && pitchCheck) {
						player.level.setBlock(pos, Blocks.ICE.defaultBlockState(), 0);
					}
				}
			}
		}
	}
	
	public List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
	}
	
	public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
		return player.level.getEntitiesOfClass(entityClass, player.getBoundingBox().inflate(dX, dY, dZ), e -> e != player && player.distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= player.getY() + dY);
	}
	
	public static int getDamage(int level){
		return 3 * level;
	}
	
	public int getDamage(){
		return getDamage(getLevel());
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDamage());
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> list = super.getInfo();
		list.add(new TranslationTextComponent("ds.skill.channel_cost", channelCost, "2s"));
		return list;
	}
}
