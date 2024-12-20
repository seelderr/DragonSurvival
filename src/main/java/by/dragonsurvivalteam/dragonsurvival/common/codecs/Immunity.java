package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncHarvestBonus;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncImmunity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.HarvestBonuses;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.Immunities;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public record Immunity(ResourceLocation id, Either<HolderSet<DamageType>, Boolean> immunitiesOrFireImmune, LevelBasedValue duration) {
    public static final Codec<Immunity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(Immunity::id),
            Codec.either(RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE), Codec.BOOL).fieldOf("immunities_or_fire_immune").forGetter(Immunity::immunitiesOrFireImmune),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(DurationInstance.INFINITE_DURATION)).forGetter(Immunity::duration)
    ).apply(instance, Immunity::new));

    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final LivingEntity target) {
        int abilityLevel = ability.level();
        int newDuration = (int) duration().calculate(abilityLevel);

        Immunities data = Immunities.getData(target);
        Instance instance = data.get(id);

        if (instance != null && instance.currentDuration() == newDuration && instance.appliedAbilityLevel() == abilityLevel) {
            return;
        }

        if (instance != null) {
            data.remove(target, instance);
        }

        ClientEffectProvider.ClientData clientData = new ClientEffectProvider.ClientData(ability.getIcon(), /* TODO */ Component.empty(), Optional.of(dragon.getUUID()));
        instance = new Instance(this, clientData, abilityLevel, newDuration);
        data.add(target, instance);

        if (target instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncImmunity(player.getId(), instance, false));
        }
    }

    public void remove(final LivingEntity target) {
        Immunities data = Immunities.getData(target);
        Instance instance = data.get(id);

        if (instance != null && target instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncImmunity(player.getId(), instance, true));
        }

        data.remove(target, instance);
    }

    public static class Instance extends DurationInstance<Immunity> {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> DurationInstance.codecStart(instance, () -> Immunity.CODEC).apply(instance, Instance::new));

        public Instance(Immunity baseData, ClientData clientData, int appliedAbilityLevel, int currentDuration) {
            super(baseData, clientData, appliedAbilityLevel, currentDuration);
        }

        public boolean isImmune(DamageSource source) {
            if(baseData().immunitiesOrFireImmune.left().isPresent()) {
                return baseData().immunitiesOrFireImmune.left().get().stream().anyMatch(type -> source.is(Objects.requireNonNull(type.getKey())));
            }

            return false;
        }

        public boolean isFireImmune() {
            return baseData().immunitiesOrFireImmune.right().orElse(false);
        }

        public Tag save(@NotNull final HolderLookup.Provider provider) {
            return CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
        }

        public static @Nullable Instance load(@NotNull final HolderLookup.Provider provider, final CompoundTag nbt) {
            return CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
        }

        @Override
        public ResourceLocation id() {
            return baseData().id();
        }

        @Override
        public int getDuration() {
            return (int) baseData().duration().calculate(appliedAbilityLevel());
        }
    }
}
