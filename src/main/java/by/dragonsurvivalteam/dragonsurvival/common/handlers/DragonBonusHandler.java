package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber
public class DragonBonusHandler{
	@SubscribeEvent
	public static void dragonDamageImmunities(LivingAttackEvent event){
		LivingEntity living = event.getEntityLiving();
		DamageSource damageSource = event.getSource();
		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(ServerConfig.bonuses){
					if(DragonUtils.isDragonType(dragonStateHandler, DragonTypes.CAVE) && ServerConfig.caveFireImmunity){
						if(damageSource.isFire() && ServerConfig.caveFireImmunity){
							event.setCanceled(true);
						}
					}else if(Objects.equals(dragonStateHandler.getType(), DragonTypes.FOREST)){
						if(damageSource == DamageSource.SWEET_BERRY_BUSH && ServerConfig.forestBushImmunity){
							event.setCanceled(true);
						}else if(damageSource == DamageSource.CACTUS && ServerConfig.forestCactiImmunity){
							event.setCanceled(true);
						}
					}
				}


				if(ServerConfig.caveSplashDamage != 0.0){
					if(Objects.equals(dragonStateHandler.getType(),DragonTypes.CAVE) && !living.hasEffect(DragonEffects.FIRE)){
						if(damageSource instanceof IndirectEntityDamageSource){
							if(damageSource.getDirectEntity() instanceof Snowball){
								living.hurt(DamageSource.GENERIC, ServerConfig.caveSplashDamage.floatValue());
							}
						}
					}
				}
			}
		});
	}


	@SubscribeEvent
	public static void removeLavaFootsteps(PlaySoundAtEntityEvent event){
		if(!(event.getEntity() instanceof Player player)){
			return;
		}
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(Objects.equals(dragonStateHandler.getType(),DragonTypes.CAVE) && ServerConfig.bonuses && ServerConfig.caveLavaSwimming && DragonSizeHandler.getOverridePose(player) == Pose.SWIMMING && event.getSound().getRegistryName().getPath().contains(".step")){
					event.setCanceled(true);
				}
			}
		});
	}

	@SubscribeEvent
	public static void reduceFallDistance(LivingFallEvent livingFallEvent){
		LivingEntity living = livingFallEvent.getEntityLiving();
		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				float distance = livingFallEvent.getDistance();

				if(Objects.equals(dragonStateHandler.getType(),DragonTypes.FOREST)){

					if(ServerConfig.bonuses){
						distance -= ServerConfig.forestFallReduction.floatValue();
					}

					CliffhangerAbility ability = DragonAbilities.getSelfAbility(living, CliffhangerAbility.class);
					distance -= ability.getHeight();
				}
				distance -= dragonStateHandler.getLevel().jumpHeight;
				livingFallEvent.setDistance(distance);
			}
		});
	}
}