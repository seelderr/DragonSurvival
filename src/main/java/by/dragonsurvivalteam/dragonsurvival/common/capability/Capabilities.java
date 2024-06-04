package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@EventBusSubscriber( modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class Capabilities{

	public static final EntityCapability<EntityStateHandler, Void> ENTITY_CAPABILITY = EntityCapability.createVoid(
			new ResourceLocation(MODID, "entity_capability"),
			EntityStateHandler.class);

	public static final EntityCapability<DragonStateHandler, Void> DRAGON_CAPABILITY = EntityCapability.createVoid(
			new ResourceLocation(MODID, "dragon_capability"),
			DragonStateHandler.class);

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.registerEntity(DRAGON_CAPABILITY, EntityType.PLAYER, new DragonStateProvider());
		event.registerEntity(ENTITY_CAPABILITY, EntityType.PLAYER, new EntityStateProvider());
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean isFakePlayer(Player player){
		return player instanceof FakeClientPlayer;
	}

	public static void syncCapability(final Player player) {
		PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncComplete.Data(player.getId(), DragonStateProvider.getOrGenerateHandler(player).serializeNBT(player.registryAccess())));
	}
}