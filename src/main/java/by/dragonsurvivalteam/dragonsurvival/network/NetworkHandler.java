package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.network.claw.DragonClawsMenuToggle;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawRender;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncBooleanConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncEnumConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncListConfig;
import by.dragonsurvivalteam.dragonsurvival.network.config.SyncNumberConfig;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenInventory;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonEditorPacket;
import by.dragonsurvivalteam.dragonsurvival.network.dragon_editor.SyncPlayerSkinPreset;
import by.dragonsurvivalteam.dragonsurvival.network.emotes.SyncEmote;
import by.dragonsurvivalteam.dragonsurvival.network.entity.PacketSyncPredatorStats;
import by.dragonsurvivalteam.dragonsurvival.network.entity.PacketSyncXPDevour;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.*;
import by.dragonsurvivalteam.dragonsurvival.network.flight.RequestSpinResync;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncFlightSpeed;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncFlyingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.magic.*;
import by.dragonsurvivalteam.dragonsurvival.network.status.*;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler{
	private static final String PROTOCOL_VERSION = "2";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(DragonSurvivalMod.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static int nextPacketId = 0;

	public static void setup(){
		register(SyncTreasureRestStatus.class, new SyncTreasureRestStatus());
		register(SyncMagicSourceStatus.class, new SyncMagicSourceStatus());

		//Generic packets
		register(SynchronizeDragonCap.class, new SynchronizeDragonCap());
		register(PacketSyncCapabilityMovement.class, new PacketSyncCapabilityMovement());
		register(SyncCapabilityDebuff.class, new SyncCapabilityDebuff());
		register(PacketSyncXPDevour.class, new PacketSyncXPDevour());
		register(PacketSyncPredatorStats.class, new PacketSyncPredatorStats());
		register(PlayerJumpSync.class, new PlayerJumpSync());
		register(RefreshDragons.class, new RefreshDragons());
		register(SyncAltarCooldown.class, new SyncAltarCooldown());
		register(OpenDragonAltar.class, new OpenDragonAltar());

		register(RequestClientData.class, new RequestClientData());
		register(CompleteDataSync.class, new CompleteDataSync());

		//Status
		register(SyncGrowthState.class, new SyncGrowthState());
		register(SyncSize.class, new SyncSize());
		register(DiggingStatus.class, new DiggingStatus());

		register(SyncPlayerSkinPreset.class, new SyncPlayerSkinPreset());
		register(OpenDragonEditorPacket.class, new OpenDragonEditorPacket());

		//Flight
		register(SyncFlyingStatus.class, new SyncFlyingStatus());
		register(SyncFlightSpeed.class, new SyncFlightSpeed());
		register(SyncSpinStatus.class, new SyncSpinStatus());
		register(RequestSpinResync.class, new RequestSpinResync());

		//Inventory
		register(OpenInventory.class, new OpenInventory());
		register(SortInventoryPacket.class, new SortInventoryPacket());

		//Render settings
		register(SyncDragonClawRender.class, new SyncDragonClawRender());
		register(SyncDragonSkinSettings.class, new SyncDragonSkinSettings());
		register(DragonClawsMenuToggle.class, new DragonClawsMenuToggle());
		register(SyncDragonClawsMenu.class, new SyncDragonClawsMenu());
		register(SyncDragonClawRender.class, new SyncDragonClawRender());

		//Ability packets
		register(OpenDragonInventory.class, new OpenDragonInventory());
		register(ChangeSkillLevel.class, new ChangeSkillLevel());
		register(SyncMagicStats.class, new SyncMagicStats());
		register(SyncMagicAbilities.class, new SyncMagicAbilities());
		register(SyncDragonAbilitySlot.class, new SyncDragonAbilitySlot());
		register(SyncAbilityCasting.class, new SyncAbilityCasting());
		register(SyncAbilityCastTime.class, new SyncAbilityCastTime());
		register(ActivateClientAbility.class, new ActivateClientAbility());

		//Potion sync
		register(SyncPotionRemovedEffect.class, new SyncPotionRemovedEffect());
		register(SyncPotionAddedEffect.class, new SyncPotionAddedEffect());

		//Emote packets
		register(SyncEmote.class, new SyncEmote());

		//Config
		register(SyncBooleanConfig.class, new SyncBooleanConfig());
		register(SyncNumberConfig.class, new SyncNumberConfig());
		register(SyncEnumConfig.class, new SyncEnumConfig());
		register(SyncListConfig.class, new SyncListConfig());
	}

	public static <T> void register(Class<T> clazz, IMessage<T> message){
		CHANNEL.registerMessage(nextPacketId++, clazz, message::encode, message::decode, message::handle);
	}
}