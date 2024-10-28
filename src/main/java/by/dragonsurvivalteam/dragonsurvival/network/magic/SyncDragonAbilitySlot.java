package by.dragonsurvivalteam.dragonsurvival.network.magic;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncDragonAbilitySlot implements IMessage<SyncDragonAbilitySlot.Data> {
	public static void handleServer(final SyncDragonAbilitySlot.Data message, final IPayloadContext context) {
		Player sender = context.player();
		context.enqueueWork(() -> {
			DragonStateProvider.getOptional(sender).ifPresent(handler -> {
				if (handler.getMagicData().getAbilityFromSlot(handler.getMagicData().getSelectedAbilitySlot()) != null) {
					handler.getMagicData().getAbilityFromSlot(handler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(sender);
				}

				handler.getMagicData().setSelectedAbilitySlot(message.selectedSlot);
				handler.getMagicData().setRenderAbilities(message.displayHotbar);
			});
		});
	}

	public record Data(int playerId, int selectedSlot, boolean displayHotbar) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_ability_slot"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.VAR_INT,
				Data::selectedSlot,
				ByteBufCodecs.BOOL,
				Data::displayHotbar,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}