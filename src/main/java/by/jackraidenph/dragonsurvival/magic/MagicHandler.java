package by.jackraidenph.dragonsurvival.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.Abilities.Passives.SpectralImpactAbility;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MagicHandler
{
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(!cap.isDragon()) return;
			
			if(player.hasEffect(DragonEffects.HUNTER)){
				BlockState bl = player.getFeetBlockState();
				BlockState below = player.level.getBlockState(player.blockPosition().below());
				
				if(bl.is(Blocks.GRASS_BLOCK) || below.is(Blocks.GRASS_BLOCK)){
					player.addEffect(new EffectInstance(Effects.INVISIBILITY, 20, 0, false, false));
				}
				
				player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 20, 2, false, false));
			}
			
			if(player.hasEffect(DragonEffects.WATER_VISION)){
				if(player.isEyeInFluid(FluidTags.WATER)){
					player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 10, 0, false, false));
				}
			}
		});
	}
	
	@SubscribeEvent
	public static void playerDamaged(LivingDamageEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					player.removeEffect(DragonEffects.HUNTER);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerHitEntity(CriticalHitEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					EffectInstance hunter = player.getEffect(DragonEffects.HUNTER);
					player.removeEffect(DragonEffects.HUNTER);
					event.setDamageModifier((hunter.getAmplifier() + 1) * 1.5F);
					event.setResult(Result.ALLOW);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void livingHurt(LivingAttackEvent event){
		if(event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof IndirectEntityDamageSource)) {
			if (event.getSource() != null && event.getSource().getEntity() != null) {
				if (event.getSource().getEntity() instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity)event.getSource().getEntity();
					
					DragonStateProvider.getCap(player).ifPresent(cap -> {
						if (!cap.isDragon()) return;
						
						SpectralImpactAbility spectralImpact = (SpectralImpactAbility)cap.getAbilityOrDefault(DragonAbilities.SPECTRAL_IMPACT);
						boolean hit = player.level.random.nextInt(100) <= spectralImpact.getChance();
						
						if(hit){
							event.getSource().bypassArmor();
						}
					});
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public static void experienceDrop(LivingExperienceDropEvent event){
		PlayerEntity player = event.getAttackingPlayer();
		
		if(player != null) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.REVEALING_THE_SOUL)) {
					int extra = Math.min(20, event.getDroppedExperience()); //TODO Change this to a config option for max exp gain
					event.setDroppedExperience(event.getDroppedExperience() + extra);
				}
			});
		}
	}
}
