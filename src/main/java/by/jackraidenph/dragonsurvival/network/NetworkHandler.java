package by.jackraidenph.dragonsurvival.network;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.OpenDragonCustomization;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.SyncPlayerAllCustomization;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.SyncPlayerCustomization;
import by.jackraidenph.dragonsurvival.network.claw.DragonClawsMenuToggle;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawRender;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.jackraidenph.dragonsurvival.network.config.SyncBooleanConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncEnumConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncListConfig;
import by.jackraidenph.dragonsurvival.network.config.SyncNumberConfig;
import by.jackraidenph.dragonsurvival.network.container.OpenDragonAltar;
import by.jackraidenph.dragonsurvival.network.container.OpenDragonInventory;
import by.jackraidenph.dragonsurvival.network.container.OpenInventory;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmote;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteServer;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStats;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStatsServer;
import by.jackraidenph.dragonsurvival.network.entity.PacketSyncPredatorStats;
import by.jackraidenph.dragonsurvival.network.entity.PacketSyncXPDevour;
import by.jackraidenph.dragonsurvival.network.entity.player.*;
import by.jackraidenph.dragonsurvival.network.flight.RequestSpinResync;
import by.jackraidenph.dragonsurvival.network.flight.SyncFlightSpeed;
import by.jackraidenph.dragonsurvival.network.flight.SyncFlyingStatus;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.magic.*;
import by.jackraidenph.dragonsurvival.network.status.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler
{
	private static final String PROTOCOL_VERSION = "2";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(DragonSurvivalMod.MODID, "main"),
	                                                                             () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static int nextPacketId = 0;
	
	public static <T> void register(Class<T> clazz, IMessage<T> message) {
     CHANNEL.registerMessage(nextPacketId++, clazz, message::encode, message::decode, message::handle);
     }
	 
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
		
		 //Status
		 register(SyncGrowthState.class, new SyncGrowthState());
		 register(SyncSize.class, new SyncSize());
		 register(DiggingStatus.class, new DiggingStatus());
		
		 register(SyncPlayerAllCustomization.class, new SyncPlayerAllCustomization());
		 register(SyncPlayerCustomization.class, new SyncPlayerCustomization());
		 register(OpenDragonCustomization.class, new OpenDragonCustomization());
		
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
		 register(SyncEmoteServer.class, new SyncEmoteServer());
		 register(SyncEmote.class, new SyncEmote());
		 register(SyncEmoteStatsServer.class, new SyncEmoteStatsServer());
		 register(SyncEmoteStats.class, new SyncEmoteStats());
		 
		 //Config
		 register(SyncBooleanConfig.class, new SyncBooleanConfig());
		 register(SyncNumberConfig.class, new SyncNumberConfig());
		 register(SyncEnumConfig.class, new SyncEnumConfig());
		 register(SyncListConfig.class, new SyncListConfig());
	 }
}
