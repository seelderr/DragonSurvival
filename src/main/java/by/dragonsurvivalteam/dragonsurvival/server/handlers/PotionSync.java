package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncPotionAddedEffect;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncPotionRemovedEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber
public class PotionSync{

	private static final List<Holder<MobEffect>> effectsToSync = new ArrayList<>(
		List.of(DSEffects.DRAIN,
				DSEffects.CHARGED,
				DSEffects.BURN,
				DSEffects.BLOOD_SIPHON,
				DSEffects.REGEN_DELAY,
				DSEffects.TRAPPED)
	);

	@SubscribeEvent
	public static void potionAdded(MobEffectEvent.Added event){
		if(!effectsToSync.contains(event.getEffectInstance().getEffect())){
			return;
		}

		LivingEntity entity = event.getEntity();

		if(!entity.level().isClientSide()){
			PacketDistributor.sendToPlayersTrackingEntity(entity, new SyncPotionAddedEffect.Data(entity.getId(), BuiltInRegistries.MOB_EFFECT.getId(event.getEffectInstance().getEffect().value()), event.getEffectInstance().getDuration(), event.getEffectInstance().getAmplifier()));
		}
	}

	@SubscribeEvent
	public static void potionRemoved(MobEffectEvent.Expired event){ // FIXME :: does this also need to check MobEffectEvent.Remove ?
		if(!effectsToSync.contains(event.getEffectInstance().getEffect())){
			return;
		}

		LivingEntity entity = event.getEntity();

		if(!entity.level().isClientSide()){
			PacketDistributor.sendToPlayersTrackingEntity(entity, new SyncPotionRemovedEffect.Data(entity.getId(), BuiltInRegistries.MOB_EFFECT.getId(event.getEffectInstance().getEffect().value())));
		}
	}
}