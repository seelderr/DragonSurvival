package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCastTime;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCasting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class ClientCastingHandler{
	private static byte timer = 0;
	private static byte abilityHoldTimer = 0;

	@SubscribeEvent
	public static void abilityKeyBindingChecks(TickEvent.ClientTickEvent clientTickEvent){
		if((Minecraft.getInstance().player == null) || (Minecraft.getInstance().level == null) || (clientTickEvent.phase != TickEvent.Phase.END) || (!DragonUtils.isDragon(Minecraft.getInstance().player))){
			return;
		}

		PlayerEntity playerEntity = Minecraft.getInstance().player;

		abilityHoldTimer = (byte)(KeyInputHandler.USE_ABILITY.isDown() ? abilityHoldTimer < 3 ? abilityHoldTimer + 1 : abilityHoldTimer : 0);
		byte modeAbility;
		if(KeyInputHandler.USE_ABILITY.isDown() && abilityHoldTimer > 1){
			modeAbility = GLFW.GLFW_REPEAT;
		}else if(KeyInputHandler.USE_ABILITY.isDown() && abilityHoldTimer == 1){
			modeAbility = GLFW.GLFW_PRESS;
		}else{
			modeAbility = GLFW.GLFW_RELEASE;
		}

		int slot = DragonStateProvider.getCap(playerEntity).map((i) -> i.getMagic().getSelectedAbilitySlot()).orElse(0);
		timer = (byte)((modeAbility == GLFW.GLFW_RELEASE) ? timer < 3 ? timer + 1 : timer : 0);

		if(timer > 1){
			return;
		}

		if(playerEntity.isSpectator()){
			return;
		}

		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			ActiveDragonAbility ability = dragonStateHandler.getMagic().getAbilityFromSlot(slot);
			if(ability.getLevel() > 0){
				if(ability.canRun(playerEntity, modeAbility) && !ability.isDisabled()){
					ability.errorTicks = 0;
					ability.errorMessage = null;

					if(ability.getCurrentCastTimer() < ability.getCastingTime() && modeAbility == GLFW.GLFW_REPEAT){
						if(dragonStateHandler.getMagic().getCurrentlyCasting() == null || dragonStateHandler.getMagic().getCurrentlyCasting().getId() != ability.getId()){
							dragonStateHandler.getMagic().setCurrentlyCasting(ability);
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(playerEntity.getId(), ability));
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastTime(playerEntity.getId(), 0));
						}
					}else if(modeAbility == GLFW.GLFW_RELEASE){
						ability.stopCasting();

						if(dragonStateHandler.getMagic().getCurrentlyCasting() != null){
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(playerEntity.getId(), null));
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastTime(playerEntity.getId(), 0));
							dragonStateHandler.getMagic().setCurrentlyCasting(null);
						}
					}else if(ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()){
						if(ability != null && (dragonStateHandler.getMagic().getCurrentlyCasting() == null || ability.getCastingTime() != dragonStateHandler.getMagic().getCurrentlyCasting().getCastingTime())){
							dragonStateHandler.getMagic().setCurrentlyCasting(ability);
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastTime(playerEntity.getId(), ability.getCurrentCastTimer()));
						}
					}
				}else{
					if(dragonStateHandler.getMagic().getCurrentlyCasting() != null){
						NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(playerEntity.getId(), null));
						NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastTime(playerEntity.getId(), 0));
						dragonStateHandler.getMagic().setCurrentlyCasting(null);
					}
				}
			}
		});
	}
}