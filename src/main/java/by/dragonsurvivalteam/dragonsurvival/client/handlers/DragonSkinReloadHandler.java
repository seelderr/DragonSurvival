package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skins.DragonSkins;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class DragonSkinReloadHandler {
	@SubscribeEvent
	public static void onReloadEvent(AddReloadListenerEvent reloadEvent) {
		DragonSkins.init(true);
	}
}
