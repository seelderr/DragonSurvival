package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncPotionAddedEffect;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncPotionRemovedEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

@EventBusSubscriber
public class PotionSync{
	@SubscribeEvent
	public static void potionAdded(PotionAddedEvent event){
		if(event.getPotionEffect().getEffect() != DragonEffects.DRAIN && event.getPotionEffect().getEffect() != DragonEffects.CHARGED && event.getPotionEffect().getEffect() != DragonEffects.BURN){
			return;
		}

		LivingEntity entity = event.getEntityLiving();

		if(!entity.level.isClientSide){
			TargetPoint point = new TargetPoint(entity.position().x, entity.position().y, entity.position().z, 64, entity.level.dimension());
			NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncPotionAddedEffect(entity.getId(), Effect.getId(event.getPotionEffect().getEffect()), event.getPotionEffect().getDuration(), event.getPotionEffect().getAmplifier()));
		}
	}

	@SubscribeEvent
	public static void potionRemoved(PotionExpiryEvent event){
		if(event.getPotionEffect().getEffect() != DragonEffects.DRAIN && event.getPotionEffect().getEffect() != DragonEffects.CHARGED && event.getPotionEffect().getEffect() != DragonEffects.BURN){
			return;
		}

		LivingEntity entity = event.getEntityLiving();

		if(!entity.level.isClientSide){
			TargetPoint point = new TargetPoint(entity.position().x, entity.position().y, entity.position().z, 64, entity.level.dimension());
			NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncPotionRemovedEffect(entity.getId(), Effect.getId(event.getPotionEffect().getEffect())));
		}
	}
}