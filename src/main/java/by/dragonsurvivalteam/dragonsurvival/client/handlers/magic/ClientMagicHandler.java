package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.SmallFireParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.SmallPoisonParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChargeCastAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Objects;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class ClientMagicHandler{

	@SubscribeEvent
	public static void onFovEvent(FOVModifierEvent event){
		Player player = event.getEntity();

		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(Arrays.stream(cap.getEmotes().currentEmotes).anyMatch(Objects::nonNull) && DragonUtils.isDragon(player)){
				event.setNewfov(1f);
				return;
			}

			ActiveDragonAbility ability = cap.getMagic().getCurrentlyCasting();

			if(ability instanceof ChargeCastAbility chargeCastAbility){
				if(chargeCastAbility.getCastTime() > 0){
					double perc = Math.min(chargeCastAbility.getCastTime() / (float)chargeCastAbility.getSkillCastingTime(), 1) / 4;
					double c4 = 2 * Math.PI / 3;

					if(perc != 0 && perc != 1){
						perc = Math.pow(2, -10 * perc) * Math.sin((perc * 10 - 0.75) * c4) + 1;
					}

					float newFov = (float)Mth.clamp(perc, 0.75F, 1.2F);
					event.setNewfov(newFov);
				}
			}
		});
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();

		if(!entity.level.isClientSide){
			return;
		}

		if(entity == Minecraft.getInstance().player || DragonUtils.isDragon(entity)){
			return;
		}
		if(entity.hasEffect(DragonEffects.BURN)){
			ParticleOptions data = new SmallFireParticleData(37F, false);
			for(int i = 0; i < 4; i++){
				renderEffectParticle(entity, data);
			}
		}

		if(entity.hasEffect(DragonEffects.DRAIN)){
			ParticleOptions data = new SmallPoisonParticleData(37F, false);
			for(int i = 0; i < 4; i++){
				renderEffectParticle(entity, data);
			}
		}

		if(entity.hasEffect(DragonEffects.CHARGED)){
			ParticleOptions data = new LargeLightningParticleData(37F, false);
			for(int i = 0; i < 4; i++){
				renderEffectParticle(entity, data);
			}
		}
	}

	public static void renderEffectParticle(LivingEntity entity, ParticleOptions data){
		double d0 = (double)(entity.level.random.nextFloat()) * entity.getBbWidth();
		double d1 = (double)(entity.level.random.nextFloat()) * entity.getBbHeight();
		double d2 = (double)(entity.level.random.nextFloat()) * entity.getBbWidth();
		double x = entity.getX() + d0 - (entity.getBbWidth() / 2);
		double y = entity.getY() + d1;
		double z = entity.getZ() + d2 - (entity.getBbWidth() / 2);
		Minecraft.getInstance().player.level.addParticle(data, x, y, z, 0, 0, 0);
	}

	@SubscribeEvent
	@OnlyIn( Dist.CLIENT )
	public static void removeLavaAndWaterFog(RenderFogEvent event){
		LocalPlayer player = Minecraft.getInstance().player;
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(!cap.isDragon()){
				return;
			}

			if(cap.getType() == DragonType.CAVE && event.getCamera().getFluidInCamera() == FogType.LAVA){
				if(player.hasEffect(DragonEffects.LAVA_VISION)){
					event.setNearPlaneDistance(-32.0F);
					event.setFarPlaneDistance(128.0F);
					event.setCanceled(true);
				}
			}else if(cap.getType() == DragonType.SEA && event.getCamera().getFluidInCamera() == FogType.WATER){
				if(player.hasEffect(DragonEffects.WATER_VISION)){
					event.setNearPlaneDistance(-32.0F);
					event.setFarPlaneDistance(128.0F);
					event.setCanceled(true);
				}
			}
		});
	}
}