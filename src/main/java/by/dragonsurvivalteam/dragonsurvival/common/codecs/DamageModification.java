package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DamageModifications;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;
import javax.annotation.Nullable;

public record DamageModification(ResourceLocation id, HolderSet<DamageType> damageTypes, LevelBasedValue multiplier, LevelBasedValue duration) {
    public static final int INFINITE_DURATION = -1;

    public static final Codec<DamageModification> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DamageModification::id),
            RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).fieldOf("types").forGetter(DamageModification::damageTypes),
            LevelBasedValue.CODEC.fieldOf("multiplier").forGetter(DamageModification::multiplier),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(DamageModification::duration)
    ).apply(instance, DamageModification::new));

    public void apply(final ServerPlayer dragon, final Entity entity, final DragonAbilityInstance ability) {
        DamageModifications data = entity.getData(DSDataAttachments.DAMAGE_MODIFICATIONS);
        Instance instance = data.get(this);

        int abilityLevel = ability.getLevel();
        int newDuration = (int) duration().calculate(abilityLevel);

        if (instance != null && instance.currentDuration() == newDuration && instance.appliedAbilityLevel() == abilityLevel) {
            return;
        }

        data.remove(entity, this);

        ClientEffectProvider.ClientData clientData = new ClientEffectProvider.ClientData(ability.getAbility().icon().get(abilityLevel), /* TODO */ Component.empty(), Optional.of(dragon.getUUID()));
        data.add(new Instance(this, clientData, abilityLevel, newDuration));
        // TODO :: send packet to client
    }

    public static class Instance implements ClientEffectProvider {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DamageModification.CODEC.fieldOf("base_data").forGetter(Instance::baseData),
                ClientData.CODEC.fieldOf("client_data").forGetter(Instance::clientData),
                Codec.INT.fieldOf("applied_ability_level").forGetter(Instance::appliedAbilityLevel),
                Codec.INT.fieldOf("current_duration").forGetter(Instance::currentDuration)
        ).apply(instance, Instance::new));

        private final DamageModification baseData;
        private final ClientData clientData;
        private final int appliedAbilityLevel;
        private int currentDuration;

        public Instance(final DamageModification baseData, final ClientData clientData, int appliedAbilityLevel, int currentDuration) {
            this.baseData = baseData;
            this.currentDuration = currentDuration;
            this.appliedAbilityLevel = appliedAbilityLevel;
            this.clientData = clientData;
        }

        public Tag save() {
            return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        }

        public static @Nullable Instance load(final CompoundTag nbt) {
            return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
        }

        public boolean tick() {
            if (currentDuration == INFINITE_DURATION) {
                return false;
            }

            currentDuration--;
            return currentDuration == 0;
        }

        public float calculate(final Holder<DamageType> damageType, float damageAmount) {
            float modification = 1;

            if (baseData().damageTypes().contains(damageType)) {
                modification = Math.max(0, baseData().multiplier().calculate(appliedAbilityLevel()));
            }

            return damageAmount * modification;
        }

        public DamageModification baseData() {
            return baseData;
        }

        public int currentDuration() {
            return currentDuration;
        }

        public int appliedAbilityLevel() {
            return appliedAbilityLevel;
        }

        @Override
        public ClientData clientData() {
            return clientData;
        }

        @Override
        public int getDuration() {
            return (int) baseData().duration().calculate(appliedAbilityLevel());
        }
    }
}
