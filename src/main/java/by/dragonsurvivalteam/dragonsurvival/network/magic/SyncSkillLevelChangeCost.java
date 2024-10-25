package by.dragonsurvivalteam.dragonsurvival.network.magic;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Synchronizes the logic of consuming or giving experience to the server side to prevent de-syncs */
public class SyncSkillLevelChangeCost implements IMessage<SyncSkillLevelChangeCost.Data> {
	public static void handleServer(final SyncSkillLevelChangeCost.Data message, final IPayloadContext context) {
		Player sender = context.player();
		context.enqueueWork(() -> {
			DragonStateProvider.getOptional(sender).ifPresent(handler -> {
				if (handler.isDragon()) {
					DragonAbility staticAbility = DragonAbilities.ABILITY_LOOKUP.get(message.skill);

					if (staticAbility instanceof PassiveDragonAbility ability) {
						if (sender instanceof ServerPlayer serverPlayer) {
							DSAdvancementTriggers.UPGRADE_ABILITY.get().trigger(serverPlayer, ability.getName(), message.level);
						}
						PassiveDragonAbility playerAbility = DragonAbilities.getSelfAbility(sender, ability.getClass());
						int levelCost = message.levelChange > 0 ? -playerAbility.getLevelCost(message.levelChange) : 0;

						if (levelCost != 0 && !sender.isCreative()) {
							sender.giveExperienceLevels(levelCost);
						}
					}
				}
			});
		});
	}

	public record Data(int level, String skill, int levelChange) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "skill_level_change_cost"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			Data::level,
			ByteBufCodecs.STRING_UTF8,
			Data::skill,
			ByteBufCodecs.VAR_INT,
			Data::levelChange,
			Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}