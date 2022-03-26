package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
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

@Mod.EventBusSubscriber
public class DragonBonusHandler{
	@SubscribeEvent
	public static void dragonDamageImmunities(LivingAttackEvent event){
		LivingEntity living = event.getEntityLiving();
		DamageSource damageSource = event.getSource();
		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(ConfigHandler.SERVER.bonuses.get()){
					if(dragonStateHandler.getType() == DragonType.CAVE && ConfigHandler.SERVER.caveFireImmunity.get()){
						if(damageSource.isFire() && ConfigHandler.SERVER.caveFireImmunity.get()){
							event.setCanceled(true);
						}
					}else if(dragonStateHandler.getType() == DragonType.FOREST){
						if(damageSource == DamageSource.SWEET_BERRY_BUSH && ConfigHandler.SERVER.forestBushImmunity.get()){
							event.setCanceled(true);
						}else if(damageSource == DamageSource.CACTUS && ConfigHandler.SERVER.forestCactiImmunity.get()){
							event.setCanceled(true);
						}
					}
				}


				if(ConfigHandler.SERVER.caveSplashDamage.get() != 0.0){
					if(dragonStateHandler.getType() == DragonType.CAVE && !living.hasEffect(DragonEffects.FIRE)){
						if(damageSource instanceof IndirectEntityDamageSource){
							if(damageSource.getDirectEntity() instanceof Snowball){
								living.hurt(DamageSource.GENERIC, ConfigHandler.SERVER.caveSplashDamage.get().floatValue());
							}
						}
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void removeLavaFootsteps(PlaySoundAtEntityEvent event){
		if(!(event.getEntity() instanceof Player)){
			return;
		}
		Player player = (Player)event.getEntity();
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.getType() == DragonType.CAVE && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && DragonSizeHandler.getOverridePose(player) == Pose.SWIMMING && event.getSound().getRegistryName().getPath().contains(".step")){
				event.setCanceled(true);
			}
		});
	}

	@SubscribeEvent
	public static void reduceFallDistance(LivingFallEvent livingFallEvent){
		LivingEntity living = livingFallEvent.getEntityLiving();
		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				float distance = livingFallEvent.getDistance();

				if(dragonStateHandler.getType() == DragonType.FOREST){

					if(ConfigHandler.SERVER.bonuses.get()){
						distance -= ConfigHandler.SERVER.forestFallReduction.get().floatValue();
					}

					DragonAbility ability = dragonStateHandler.getMagic().getAbility(DragonAbilities.CLIFFHANGER);

					if(ability != null){
						distance -= ((CliffhangerAbility)ability).getHeight();
					}
				}
				distance -= dragonStateHandler.getLevel().jumpHeight;
				livingFallEvent.setDistance(distance);
			}
		});
	}
}