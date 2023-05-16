package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChannelingCastAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCasting;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class ClientCastingHandler{
	public static final byte StatusIdle = 0;
	public static final byte StatusInProgress = 1;
	public static final byte StatusStop = 2;
	public static byte status = StatusIdle;
	public static boolean hasCast = false;
	public static int oldChargeTime = -1;
	@SubscribeEvent
	public static void abilityKeyBindingChecks(TickEvent.ClientTickEvent clientTickEvent){
		if(Minecraft.getInstance().player == null || Minecraft.getInstance().level == null || clientTickEvent.phase != TickEvent.Phase.END)
			return;

		Player player = Minecraft.getInstance().player;
		if(player.isSpectator() || !DragonUtils.isDragon(player))
			return;

		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);


		boolean isKeyDown = KeyInputHandler.USE_ABILITY.isDown() || ClientConfig.alternateCastMode && (
								KeyInputHandler.ABILITY1.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 0
								|| KeyInputHandler.ABILITY2.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 1
								|| KeyInputHandler.ABILITY3.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 2
								|| KeyInputHandler.ABILITY4.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 3);

		if(status == StatusIdle && isKeyDown)
			status = StatusInProgress;

		if(status == StatusInProgress && !isKeyDown){
			status = StatusStop;
		}

		if(!isKeyDown){
			hasCast = false;
		}


		int slot = dragonStateHandler.getMagicData().getSelectedAbilitySlot();
		ActiveDragonAbility ability = dragonStateHandler.getMagicData().getAbilityFromSlot(slot);

		if (ability instanceof ChannelingCastAbility channelingCastAbility && status == StatusInProgress) {
			if (oldChargeTime == channelingCastAbility.getChargeTime())
				return;
			oldChargeTime = channelingCastAbility.getChargeTime();
		}
		if(status == StatusInProgress && ability.canCastSkill(player) ){
			NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(player.getId(), true, ability.saveNBT()));
		}else if(status == StatusStop || status == StatusInProgress && !ability.canCastSkill(player)){
			NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(player.getId(), false, ability.saveNBT()));
			ability.onKeyReleased(player);
			status = StatusIdle;
			oldChargeTime = -1;
		}
	/*
		if(ability != null && ability.getLevel() > 0 && !ability.isDisabled()){
			if(status == StatusInProgress && ability.canCastSkill(player) ){
				if (!(ability instanceof ChannelingCastAbility channelingCastAbility))
					NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(player.getId(), true, ability.saveNBT()));
				else if (oldChargeTime != channelingCastAbility.getChargeTime()) {
					oldChargeTime = channelingCastAbility.getChargeTime();
					NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(player.getId(), true, ability.saveNBT()));
				}
			}else if(status == StatusStop || status == StatusInProgress && !ability.canCastSkill(player)){
				NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCasting(player.getId(), false, ability.saveNBT()));

				ability.onKeyReleased(player);
				status = StatusIdle;
				oldChargeTime = -1;
			}
		}
		*/
	}
}