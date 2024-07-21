package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncPotionAddedEffect;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncPotionRemovedEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber
public class PotionSync{
	@SubscribeEvent
	public static void potionAdded(MobEffectEvent.Added event){
		if(event.getEffectInstance().getEffect() != DSEffects.DRAIN && event.getEffectInstance().getEffect() != DSEffects.CHARGED && event.getEffectInstance().getEffect() != DSEffects.BURN && event.getEffectInstance().getEffect() != DSEffects.BLOOD_SIPHON && event.getEffectInstance().getEffect() != DSEffects.REGEN_DELAY){
			return;
		}

		LivingEntity entity = event.getEntity();

		if(!entity.level().isClientSide()){
			PacketDistributor.sendToPlayersNear((ServerLevel)entity.level(), null, entity.position().x, entity.position().y, entity.position().z, 64, new SyncPotionAddedEffect.Data(entity.getId(), BuiltInRegistries.MOB_EFFECT.getId(event.getEffectInstance().getEffect().value()), event.getEffectInstance().getDuration(), event.getEffectInstance().getAmplifier()));
		}
	}

	@SubscribeEvent
	public static void potionRemoved(MobEffectEvent.Expired event){
		if(event.getEffectInstance() != null && event.getEffectInstance().getEffect() != DSEffects.DRAIN && event.getEffectInstance().getEffect() != DSEffects.CHARGED && event.getEffectInstance().getEffect() != DSEffects.BURN && event.getEffectInstance().getEffect() != DSEffects.BLOOD_SIPHON && event.getEffectInstance().getEffect() != DSEffects.REGEN_DELAY){
			return;
		}

		LivingEntity entity = event.getEntity();

		if(!entity.level().isClientSide()){
			PacketDistributor.sendToPlayersNear((ServerLevel)entity.level(), null, entity.position().x, entity.position().y, entity.position().z, 64, new SyncPotionRemovedEffect.Data(entity.getId(), BuiltInRegistries.MOB_EFFECT.getId(event.getEffectInstance().getEffect().value())));
		}
	}
}