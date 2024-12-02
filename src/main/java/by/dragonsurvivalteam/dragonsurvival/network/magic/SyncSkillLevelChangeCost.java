// FIXME
//package by.dragonsurvivalteam.dragonsurvival.network.magic;
//
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
//import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
//import by.dragonsurvivalteam.dragonsurvival.util.ExperienceUtils;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.neoforged.neoforge.network.handling.IPayloadContext;
//import org.jetbrains.annotations.NotNull;
//
//import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;
//
///** Synchronizes the logic of consuming or giving experience to the server side to prevent de-syncs */
//public record SyncSkillLevelChangeCost(String ability, int level, int change) implements CustomPacketPayload {
//    public static final Type<SyncSkillLevelChangeCost> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_skill_level_change_cost"));
//
//    public static final StreamCodec<FriendlyByteBuf, SyncSkillLevelChangeCost> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.STRING_UTF8, SyncSkillLevelChangeCost::ability,
//            ByteBufCodecs.VAR_INT, SyncSkillLevelChangeCost::level,
//            ByteBufCodecs.VAR_INT, SyncSkillLevelChangeCost::change,
//            SyncSkillLevelChangeCost::new
//    );
//
//    public static void handleServer(final SyncSkillLevelChangeCost packet, final IPayloadContext context) {
//        context.enqueueWork(() -> {
//            DragonStateHandler data = DragonStateProvider.getData(context.player());
//
//            if (data.isDragon()) {
//                DragonAbility staticAbility = DragonAbilities.ABILITY_LOOKUP.get(packet.ability());
//
//                if (staticAbility instanceof PassiveDragonAbility passiveAbility) {
//                    if (context.player() instanceof ServerPlayer serverPlayer) {
//                        DSAdvancementTriggers.UPGRADE_ABILITY.get().trigger(serverPlayer, passiveAbility.getName(), packet.level());
//                    }
//
//                    // Ability should be present
//                    PassiveDragonAbility playerAbility = DragonAbilities.getAbility(context.player(), passiveAbility.getClass(), data.getType()).orElseThrow();
//                    int experiencePoints = ExperienceUtils.getTotalExperience(playerAbility.getLevelCost(packet.change()));
//
//                    if (experiencePoints != 0 && !context.player().isCreative()) {
//                        // Subtract the experience point if the ability was leveled up
//                        context.player().giveExperiencePoints(packet.change() > 0 ? -experiencePoints : experiencePoints);
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public @NotNull Type<? extends CustomPacketPayload> type() {
//        return TYPE;
//    }
//}