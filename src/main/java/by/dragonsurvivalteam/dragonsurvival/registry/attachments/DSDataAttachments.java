package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.EntityStateHandler;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.LightningHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DSDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> DS_ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DragonSurvival.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntityStateHandler>> ENTITY_HANDLER = DS_ATTACHMENT_TYPES.register("entity_handler", () -> AttachmentType.serializable(EntityStateHandler::new).build());
    // TODO :: does this need a custom copy handle for entering the end portal?
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DragonStateHandler>> DRAGON_HANDLER = DS_ATTACHMENT_TYPES.register("dragon_handler", () -> AttachmentType.serializable(DragonStateHandler::new).copyOnDeath().build());
    // TODO :: copy on death?
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PenaltySupply>> PENALTY_SUPPLY = DS_ATTACHMENT_TYPES.register("penalty_supply", () -> AttachmentType.serializable(PenaltySupply::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LightningHandler>> LIGHTNING_BOLT_DATA = DS_ATTACHMENT_TYPES.register("lightning_bolt_data", () -> AttachmentType.serializable(LightningHandler::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ModifiersWithDuration>> MODIFIERS_WITH_DURATION = DS_ATTACHMENT_TYPES.register("modifiers_with_duration", () -> AttachmentType.serializable(ModifiersWithDuration::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DamageReductions>> DAMAGE_REDUCTIONS = DS_ATTACHMENT_TYPES.register("damage_reductions", () -> AttachmentType.serializable(DamageReductions::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DragonMovementData>> DRAGON_MOVEMENT_DATA = DS_ATTACHMENT_TYPES.register("movement_data", () -> AttachmentType.builder(DragonMovementData::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DragonSpinData>> DRAGON_SPIN_DATA = DS_ATTACHMENT_TYPES.register("spin_data", () -> AttachmentType.serializable(DragonSpinData::new).copyOnDeath().build());
}
